package arun.auth.server.security;

import arun.auth.server.custom.AuthProviderEnum;
import arun.auth.server.custom.CustomUserDetailsService;
import arun.auth.server.custom.data.entity.UserEntity;
import arun.auth.server.custom.domain.CustomUser;
import arun.auth.server.custom.domain.SSOUser;
import arun.auth.server.custom.domain.SSOUserProfile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * Web Controller for User Login and Main Page
 */
@Controller
public class LoginController {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public LoginController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @RequestMapping("/login")
    public ModelAndView login(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("login");
        return modelAndView;
    }

    @RequestMapping("/login/covisint")
    public String loginCovisint() {
        return "login-covisint";
    }

    @RequestMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public ModelAndView createCustomUser(@ModelAttribute CustomUser customUser) {
        ModelAndView modelAndView = new ModelAndView("redirect:register");
        if (validateInputs(customUser)) {
            if (customUserDetailsService.createCustomUser(customUser)) {
                customUserDetailsService.createUserAndProvider(null, customUser.getUsername(), AuthProviderEnum.CUSTOM);
                modelAndView.addObject("registered",true);
                return modelAndView;
            } else {
                Optional<UserEntity> userEntityOptional = Optional.ofNullable(customUserDetailsService.findUserByProviderUserId(customUser.getUsername()));
                String userName = userEntityOptional.map(UserEntity::getUserId).orElse(null);
                customUserDetailsService.createUserAndProvider(userName, customUser.getUsername(), AuthProviderEnum.CUSTOM);
                modelAndView.addObject("userExists",true);
                return modelAndView;
            }
        } else {
            modelAndView.addObject("invalid",true);
            return modelAndView;
        }
    }

    @RequestMapping({"/", "/profile"})
    public ModelAndView home(Authentication authentication) {
        SSOUser SSOUser = (SSOUser) authentication.getPrincipal();
        SSOUserProfile SSOUserProfile = getUserProfile(SSOUser.getUsername());
        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject("user", SSOUserProfile);
        return modelAndView;
    }

    @PostMapping("/profile")
    public ModelAndView updateProfile(@ModelAttribute SSOUserProfile SSOUserProfile, Authentication authentication) throws Exception {
        SSOUser SSOUser = (SSOUser) authentication.getPrincipal();
        ModelAndView modelAndView = new ModelAndView("profile");
        if(StringUtils.equals(SSOUserProfile.getId(), SSOUser.getUsername())) {
            customUserDetailsService.upsertUserProfile(SSOUserProfile);
            SSOUserProfile = getUserProfile(SSOUser.getUsername());
            modelAndView.addObject("user", SSOUserProfile);
            modelAndView.addObject("updated", true);
        } else {
            throw new Exception("Invalid User");
        }
        return modelAndView;
    }

    private SSOUserProfile getUserProfile(String username) {
        SSOUserProfile SSOUserProfile = customUserDetailsService.findUserProfileByUsername(username);
        List<String> providers = customUserDetailsService.findAllProvidersByUserName(username);
        for (String provider : providers) {
            if(provider.equals(AuthProviderEnum.CUSTOM.toString())) {
                SSOUserProfile.setCustomProvider(true);
            } else if (provider.equals(AuthProviderEnum.GOOGLE.toString())) {
                SSOUserProfile.setGoogleProvider(true);
            } else if (provider.equals(AuthProviderEnum.FACEBOOK.toString())) {
                SSOUserProfile.setFacebookProvider(true);
            } else if (provider.equals(AuthProviderEnum.GITHUB.toString())) {
                SSOUserProfile.setGithubProvider(true);
            }
        }
        return SSOUserProfile;
    }

    private boolean validateInputs (CustomUser customUser) {
        if (customUser != null) {
            if (StringUtils.isNotEmpty(customUser.getUsername()) &&
                    StringUtils.isNotEmpty(customUser.getPassword()) &&
                    StringUtils.equals(customUser.getPassword(), customUser.getConfirmPassword())) {
                return true;
            }
        }
        return false;
    }
}
