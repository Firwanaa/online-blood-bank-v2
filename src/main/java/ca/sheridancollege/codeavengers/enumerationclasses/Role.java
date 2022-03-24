package ca.sheridancollege.codeavengers.enumerationclasses;

import ca.sheridancollege.codeavengers.consts.AuthorityConst;

public enum Role {
	ROLE_USER(AuthorityConst.USER_AUTHORITIES),
	ROLE_INST(AuthorityConst.INST_AUTHORITIES),
	ROLE_MANAGER(AuthorityConst.MANAGER_AUTHORITIES),
	ROLE_ADMIN(AuthorityConst.ADMIN_AUTHORITIES),
	ROLE_SUPER_ADMIN(AuthorityConst.SUPER_ADMIN_AUTHORITIES);

	private String[] authorities;

	Role(String... authorities) {
		this.authorities = authorities;
	}

	public String[] getAuthorities() {
		return authorities;
	}
}
