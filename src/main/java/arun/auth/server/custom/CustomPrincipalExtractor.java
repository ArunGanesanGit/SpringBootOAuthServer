package arun.auth.server.custom;

import arun.auth.server.custom.data.entity.UserEntity;
import arun.auth.server.custom.domain.SSOUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedPrincipalExtractor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.Optional;

/**
 * It extracts the principal object after social authentication and finds/creates a corresponding ID.
 */
public class CustomPrincipalExtractor extends FixedPrincipalExtractor {

    private CustomUserDetailsService customUserDetailsService;

    private AuthProviderEnum authProvider;

    public CustomPrincipalExtractor(CustomUserDetailsService customUserDetailsService, AuthProviderEnum authProvider) {
        this.customUserDetailsService = customUserDetailsService;
        this.authProvider = authProvider;
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        String socialUserId = null;
        switch (authProvider) {
            case FACEBOOK:
            case GITHUB:
                socialUserId = getUserId(map, "id");
                break;
            case GOOGLE:
                socialUserId = getUserId(map, "sub");
                break;
        }
        if (StringUtils.isEmpty(socialUserId)) socialUserId = (String)super.extractPrincipal(map);

        Optional<UserEntity> userEntityOptional = Optional.ofNullable(customUserDetailsService.findUserByProviderUserId(socialUserId));
        String userName = userEntityOptional.map(UserEntity::getUserId).orElse(null);

        // If the userName is empty, then it means there is no existing User for the currently logging in user
        if (StringUtils.isEmpty(userName)) {

            // If user has already logged in with other provider, get the User from already logged in session to associate this new provider
            Optional<Authentication> authenticationOptional = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
            if (authenticationOptional.isPresent()) {
                if (authenticationOptional.get().getPrincipal() instanceof SSOUser) {
                    SSOUser SSOUser = (SSOUser) authenticationOptional.get().getPrincipal();
                    userName = SSOUser.getUsername();
                } else {
                    userName = authenticationOptional.get().getPrincipal().toString();
                }
            }
            // If userName is empty, it creates a new one otherwise it adds a provider to an existing user
            userName = customUserDetailsService.createUserAndProvider(userName, socialUserId, authProvider);
        }
        return customUserDetailsService.findUserByUserName(userName);
    }

    private String getUserId(Map map, String key) {
        if(map.containsKey(key)) {
            Optional<Object> valueOptional = Optional.ofNullable(map.get(key));
            if (valueOptional.isPresent()) {
                return valueOptional.get().toString();
            }
        }
        return null;
    }
}
