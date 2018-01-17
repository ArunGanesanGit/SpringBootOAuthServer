package arun.auth.server.user;

import arun.auth.server.custom.domain.SSOUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @RequestMapping({"/user", "/me"})
    public Map<String, String> getUser(Authentication authentication) {
        Map<String, String> userMap = new HashMap<>();
        SSOUser SSOUser;
        if (authentication.getPrincipal() instanceof SSOUser) {
            SSOUser = (SSOUser) authentication.getPrincipal();
            userMap.put("username", SSOUser.getUsername());
        } else {
            userMap.put("username", authentication.getPrincipal().toString());
        }
        return userMap;
    }

}