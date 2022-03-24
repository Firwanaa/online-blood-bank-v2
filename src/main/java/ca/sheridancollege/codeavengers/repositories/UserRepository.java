package ca.sheridancollege.codeavengers.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import ca.sheridancollege.codeavengers.domain.User;

public interface UserRepository extends MongoRepository<User, String> {
	User findUserByUsername(String username);

	User findUserByEmail(String email);
	
	List<User> findUserByCity(String city);
}
