package ca.sheridancollege.codeavengers.services;

import com.google.common.cache.*;
import com.google.common.cache.LoadingCache;

import org.springframework.stereotype.Service;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.concurrent.ExecutionException;

@Service
public class LoginService {
	private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
	private static final int ATTEMPT_INCREMENT = 1;
	private LoadingCache<String, Integer> loginAttemptCache;

	public LoginService() {
		super();
		loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES)
				.maximumSize(100).build(new CacheLoader<String, Integer>() {
					public Integer load(String key) {
						return 0;
					}
				});
	}

	public void evictUserFromLoginAttemptCache(String username) {
		loginAttemptCache.invalidate(username);
	}

	public void addUserToLoginAttemptCache(String username) {
		int attempts = 0;
		try {
			attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		loginAttemptCache.put(username, attempts);
	}

	public boolean hasExceededMaxAttempts(String username) {
		try {
			return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}
}
