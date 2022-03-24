package ca.sheridancollege.codeavengers.controllers;

import static ca.sheridancollege.codeavengers.consts.SecurityConst.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ca.sheridancollege.codeavengers.domain.HttpResponse;
import ca.sheridancollege.codeavengers.domain.User;
import ca.sheridancollege.codeavengers.exception.domain.EmailExistException;
import ca.sheridancollege.codeavengers.exception.domain.EmailNotFoundException;
import ca.sheridancollege.codeavengers.exception.domain.UserNotFoundException;
import ca.sheridancollege.codeavengers.exception.domain.UsernameExistException;
import ca.sheridancollege.codeavengers.services.UserPrincipal;
import ca.sheridancollege.codeavengers.services.UserService;
import ca.sheridancollege.codeavengers.utils.JWTTokenProvider;

@RestController
@RequestMapping(path = { "/", "/user" })
public class HomeController {

	// private UserRepository donerRepository;

	// @GetMapping("/")
	// public String index(Model model) {

	// model.addAttribute("doner", new User());
	// model.addAttribute("donerList", donerRepository.findAll()); //for testing
	// during developmnet - remove it later

	// model.addAttribute("bloodType", Arrays.asList(BloodType.values()));
	// return "index";
	// }

	// @PostMapping("/addDoner")
	// public String addDoner(Model model, @ModelAttribute User doner) {
	// doner.setId(null);// A Known issue with MongoRepository, hofully will be
	// fixed
	// model.addAttribute("doner", doner);

	// donerRepository.save(doner);
	// model.addAttribute("donerList", donerRepository.findAll());

	// model.addAttribute("bloodType", Arrays.asList(BloodType.values()));
	// return "redirect:/";
	// }
	public static final String EMAIL_SENT = "An email with a new password was sent to: ";
	public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
	private AuthenticationManager authenticationManager;
	private UserService userService;
	private JWTTokenProvider jwtTokenProvider;

	@Autowired
	public HomeController(AuthenticationManager authenticationManager, UserService userService,
			JWTTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user) {
		System.out.println("***** login 1 *****");
		authenticate(user.getUsername(), user.getPassword());
		System.out.println("***** login 2 *****");
		User loginUser = userService.findUserByUsername(user.getUsername());
		System.out.println("***** login 3 *****");
		UserPrincipal userPrincipal = new UserPrincipal(loginUser);
		HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
		System.out.println("***** login 4 *****");
		return new ResponseEntity<>(loginUser, jwtHeader, OK);
	}

	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody User user)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		User newUser = userService.register(user.getName(), user.getUsername(),
				user.getEmail(), user.getPostCode());
		return new ResponseEntity<>(newUser, OK);
	}


	@PostMapping("/registerinst")
	public ResponseEntity<User> registerinst(@RequestBody User user)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		User newInst = userService.registerinst(user.getName(), user.getUsername(),
				user.getEmail(), user.getCode(), user.getCity());
		return new ResponseEntity<>(newInst, OK);
	}

	@PostMapping("/add")
	public ResponseEntity<User> addNewUser(@RequestParam("name") String name,
			@RequestParam("username") String username,
			@RequestParam("email") String email,
			@RequestParam("role") String role,
			@RequestParam("isActive") String isActive,
			@RequestParam("isNonLocked") String isNonLocked)
			throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
		// id should be null
		User newUser = userService.addNewUser(name, username, email, role,
				Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive));
		return new ResponseEntity<>(newUser, OK);
	}

	@PostMapping("/update")
	public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
			@RequestParam("name") String name,
			@RequestParam("username") String username,
			@RequestParam("email") String email,
			@RequestParam("role") String role,
			@RequestParam("isActive") String isActive,
			@RequestParam("isNonLocked") String isNonLocked,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
			throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
		User updatedUser = userService.updateUser(currentUsername, name, username, email, role,
				Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive));
		return new ResponseEntity<>(updatedUser, OK);
	}

	@GetMapping("/find/{username}")
	public ResponseEntity<User> getUser(@PathVariable("username") String username) {
		User user = userService.findUserByUsername(username);
		return new ResponseEntity<>(user, OK);
	}

	@GetMapping("/list")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.getUsers();
		return new ResponseEntity<>(users, OK);
	}

	@GetMapping("/resetpassword/{email}")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
			throws MessagingException, EmailNotFoundException {
		userService.resetPassword(email);
		return response(OK, EMAIL_SENT + email);
	}

	@DeleteMapping("/delete/{username}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
		userService.deleteUser(username);
		return response(OK, USER_DELETED_SUCCESSFULLY);
	}

	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(),
				message), httpStatus);
	}

	private HttpHeaders getJwtHeader(UserPrincipal user) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
		return headers;
	}

	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

}
