package ca.sheridancollege.codeavengers.bootstrap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ca.sheridancollege.codeavengers.domain.Address;
import ca.sheridancollege.codeavengers.domain.BloodType;
import ca.sheridancollege.codeavengers.domain.User;
import ca.sheridancollege.codeavengers.enumerationclasses.Role;
import ca.sheridancollege.codeavengers.repositories.UserRepository;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BootStrapData implements CommandLineRunner {
	private UserRepository userRepository;

	@Override
	public void run(String... args) throws Exception {

		Address a1 = Address.builder()
				.country("Canada")
				.city("Mississauga")
				.postCode("L23 63D")
				.build();
		// List<String> authList = new ArrayList<String>();
		// authList.add(Role.ROLE_SUPER_ADMIN.name());
		// String[] temp = authList.toArray(new String[authList.size()]);
		User d1 = User.builder()
				.name("Alqassam")
				.email("firwanaa@sheridancollege.ca")
				.bloodType(BloodType.ONeg)
				.role(Role.ROLE_SUPER_ADMIN.name())
				.authorities(Role.ROLE_SUPER_ADMIN.getAuthorities())
				.build();
		d1.setAddress(a1);
		userRepository.save(d1);
	}

}
