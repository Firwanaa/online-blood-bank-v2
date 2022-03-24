package ca.sheridancollege.codeavengers.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ca.sheridancollege.codeavengers.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;

public class UserPrincipal implements UserDetails {
	private User user;

	public UserPrincipal(User user) {
		this.user = user;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// List<GrantedAuthority> authorities =
		// user.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		return Arrays.stream(this.user.getAuthorities()).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
		// return authorities;
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.user.isNotLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.user.isActive();
	}
}
