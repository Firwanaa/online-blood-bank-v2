package ca.sheridancollege.codeavengers.consts;

public class AuthorityConst {
	public static final String[] USER_AUTHORITIES = { "user:read" };
	public static final String[] INST_AUTHORITIES = { "user:read", "user:update" };
	public static final String[] MANAGER_AUTHORITIES = { "user:read", "user:update" };
	public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:create", "user:update" };
	public static final String[] SUPER_ADMIN_AUTHORITIES = { "user:read", "user:create", "user:update",
			"user:delete" };
}
