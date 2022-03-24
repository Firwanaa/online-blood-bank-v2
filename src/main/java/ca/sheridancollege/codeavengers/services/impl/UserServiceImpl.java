package ca.sheridancollege.codeavengers.services.impl;

import static ca.sheridancollege.codeavengers.enumerationclasses.Role.ROLE_INST;
import static ca.sheridancollege.codeavengers.enumerationclasses.Role.ROLE_USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.sheridancollege.codeavengers.consts.UserImplConst;
import ca.sheridancollege.codeavengers.domain.User;
import ca.sheridancollege.codeavengers.enumerationclasses.Role;
import ca.sheridancollege.codeavengers.enumerationclasses.UserType;
import ca.sheridancollege.codeavengers.exception.domain.EmailExistException;
import ca.sheridancollege.codeavengers.exception.domain.EmailNotFoundException;
import ca.sheridancollege.codeavengers.exception.domain.UserNotFoundException;
import ca.sheridancollege.codeavengers.exception.domain.UsernameExistException;
import ca.sheridancollege.codeavengers.repositories.UserRepository;
import ca.sheridancollege.codeavengers.services.EmailService;
import ca.sheridancollege.codeavengers.services.LoginService;
import ca.sheridancollege.codeavengers.services.UserPrincipal;
import ca.sheridancollege.codeavengers.services.UserService;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());// or UserSeriveImpl.class
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private LoginService loginAttemptService;
	private EmailService emailService;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
			LoginService loginAttemptService, EmailService emailService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.loginAttemptService = loginAttemptService;
		this.emailService = emailService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		if (user == null) {
			LOGGER.error("User not found by username: " + username);
			throw new UsernameNotFoundException("User not found by username: " + username);
		} else {
			validateLoginAttempt(user);
			user.setLastLoginDate(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			userRepository.save(user);
			UserPrincipal userPrincipal = new UserPrincipal(user);
			LOGGER.info("Returning found user by username: " + username);
			return userPrincipal;
		}
	}

	@Override
	public User register(String name, String username, String email, String postCode)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		validateNewUsernameAndEmail(EMPTY, username, email);
		User user = new User();
		user.setUserId(generateUserId());
		String password = generatePassword();
		user.setName(name);
		user.setPostCode(postCode);
		user.setUsername(username);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodePassword(password));
		user.setActive(true);
		user.setAvailable(true);
		user.setUserTyper(UserType.USER);
		user.setNotLocked(true);
		user.setRole(ROLE_USER.name());
		user.setAuthorities((ROLE_USER.getAuthorities()));
		userRepository.save(user);
		LOGGER.info("New user password: " + password);
		emailService.sendNewPasswordEmail(name, password, email);
		return user;
	}

	@Override
	public User registerinst(String name, String username, String email, String code, String city)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		validateNewUsernameAndEmail(EMPTY, username, email);
		User user = new User();
		user.setUserId(generateUserId());
		String password = generatePassword();
		user.setName(name);
		user.setCity(city);
		user.setUsername(username);
		user.setEmail(email);
		user.setCode(code);
		user.setJoinDate(new Date());
		user.setPassword(encodePassword(password));
		user.setActive(true);
		user.setAvailable(true);
		user.setUserTyper(UserType.INST);
		user.setNotLocked(true);
		user.setRole(ROLE_INST.name());
		user.setAuthorities((ROLE_INST.getAuthorities()));
		userRepository.save(user);
		LOGGER.info("New user password: " + password);
		emailService.sendNewPasswordEmail(name, password, email);
		return user;
	}

	@Override
	public User addNewUser(String name, String username, String email, String role,
			boolean isNonLocked, boolean isActive) throws UserNotFoundException,
			UsernameExistException, EmailExistException, IOException {
		validateNewUsernameAndEmail(EMPTY, username, email);
		User user = new User();
		String password = generatePassword();
		user.setId(null);// becuse its MongoDB
		user.setUserId(generateUserId());
		user.setName(name);
		user.setJoinDate(new Date());
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(encodePassword(password));
		user.setActive(isActive);
		user.setNotLocked(isNonLocked);
		user.setRole(getRoleEnumName(role).name());
		user.setAuthorities(getRoleEnumName(role).getAuthorities());
		userRepository.save(user);
		// LOGGER.info("New user password: " + password);
		return user;
	}

	@Override
	public User updateUser(String currentUsername, String newName, String newUsername,
			String newEmail, String role, boolean isNonLocked, boolean isActive)
			throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
		User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
		currentUser.setName(newName);
		currentUser.setUsername(newUsername);
		currentUser.setEmail(newEmail);
		currentUser.setActive(isActive);
		currentUser.setNotLocked(isNonLocked);
		currentUser.setRole(getRoleEnumName(role).name());
		currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
		userRepository.save(currentUser);
		return currentUser;
	}

	@Override
	public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
		User user = userRepository.findUserByEmail(email);
		if (user == null) {
			throw new EmailNotFoundException(UserImplConst.NO_USER_FOUND_BY_EMAIL + email);
		}
		String password = generatePassword();
		user.setPassword(encodePassword(password));
		userRepository.save(user);
		LOGGER.info("New user password: " + password);
		emailService.sendNewPasswordEmail(user.getName(), password, user.getEmail());
	}

	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}

	@Override
	public void deleteUser(String username) throws IOException {
		User user = userRepository.findUserByUsername(username);
		userRepository.deleteById(user.getId());
	}

	private Role getRoleEnumName(String role) {
		return Role.valueOf(role.toUpperCase());
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	private void validateLoginAttempt(User user) {
		if (user.isNotLocked()) {
			if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
				user.setNotLocked(false);
			} else {
				user.setNotLocked(true);
			}
		} else {
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}
	}

	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
			throws UserNotFoundException, UsernameExistException, EmailExistException {
		User userByNewUsername = findUserByUsername(newUsername);
		User userByNewEmail = findUserByEmail(newEmail);
		if (StringUtils.isNotBlank(currentUsername)) {
			User currentUser = findUserByUsername(currentUsername);
			if (currentUser == null) {
				throw new UserNotFoundException(
						UserImplConst.NO_USER_FOUND_BY_USERNAME + currentUsername);
			}
			if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
				throw new UsernameExistException(UserImplConst.USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
				throw new EmailExistException(UserImplConst.EMAIL_ALREADY_EXISTS);
			}
			return currentUser;
		} else {
			if (userByNewUsername != null) {
				throw new UsernameExistException(UserImplConst.USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null) {
				throw new EmailExistException(UserImplConst.EMAIL_ALREADY_EXISTS);
			}
			return null;
		}
	}

	@Override
	public List<User> findUserByCity(String city) {

		return userRepository.findUserByCity(city);
	}

}
