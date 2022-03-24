package ca.sheridancollege.codeavengers.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import ca.sheridancollege.codeavengers.services.LoginService;
import ca.sheridancollege.codeavengers.services.UserPrincipal;


@Component
public class AuthenticationSuccessListener {
    private LoginService loginService;

    @Autowired
    public AuthenticationSuccessListener(LoginService loginAttemptService) {
        this.loginService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof UserPrincipal) {
            UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
