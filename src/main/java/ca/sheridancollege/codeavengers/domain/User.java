package ca.sheridancollege.codeavengers.domain;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import ca.sheridancollege.codeavengers.enumerationclasses.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	private String id;
	//Generated ID to be displayed
	private String userId;
	private String name;
	private String username;
	//email needs validation
	// @Indexed(unique = true)
	private String email;
	private Address address;
	private String city;	
	@Enumerated(EnumType.STRING)
	private BloodType bloodType;
	//password needs validation with security
	private String password;
	private Date lastLoginDate;
	private Date getLastLoginDateDisplay;
	private Date joinDate;
	private String role; 
	private String[] authorities;
	private boolean isAvailable;
	private boolean isActive;
	private boolean isNotLocked;
	private UserType userTyper;
	private String code;
	private String postCode;
}
