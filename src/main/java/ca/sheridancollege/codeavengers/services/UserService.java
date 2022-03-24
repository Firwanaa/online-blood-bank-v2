package ca.sheridancollege.codeavengers.services;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import ca.sheridancollege.codeavengers.domain.User;
import ca.sheridancollege.codeavengers.exception.domain.*;

public interface UserService {

	User register(String name, String username, String email, String postCode)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

	User registerinst(String name, String username, String email, String code, String city)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

	List<User> getUsers();

	User findUserByUsername(String username);

	User findUserByEmail(String email);
	List<User> findUserByCity(String city);

	User addNewUser(String name, String username, String email, String role,
			boolean isNonLocked, boolean isActive) throws UserNotFoundException,
			UsernameExistException, EmailExistException, IOException;

	User updateUser(String currentUsername, String newName, String newUsername,
			String newEmail, String role, boolean isNonLocked, boolean isActive)
			throws UserNotFoundException, UsernameExistException, EmailExistException, IOException;

	void deleteUser(String username) throws IOException;

	void resetPassword(String email) throws MessagingException, EmailNotFoundException;


}
