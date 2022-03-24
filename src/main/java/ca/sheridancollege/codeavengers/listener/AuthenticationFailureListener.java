package ca.sheridancollege.codeavengers.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import ca.sheridancollege.codeavengers.services.LoginService;


@Component
public class AuthenticationFailureListener {
    private LoginService loginService;

    @Autowired
    public AuthenticationFailureListener(LoginService loginAttemptService) {
        this.loginService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();
            loginService.addUserToLoginAttemptCache(username);
        }

    }
}
