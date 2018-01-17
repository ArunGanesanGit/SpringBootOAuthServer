package arun.auth.server.custom;

import arun.auth.server.custom.data.entity.UserEntity;
import arun.auth.server.custom.data.entity.AuthProviderEntity;
import arun.auth.server.custom.data.entity.ProfileEntity;
import arun.auth.server.custom.data.entity.UserCustomEntity;
import arun.auth.server.custom.data.repository.AuthProviderRepository;
import arun.auth.server.custom.data.repository.ProfileRepository;
import arun.auth.server.custom.data.repository.UserCustomRepository;
import arun.auth.server.custom.data.repository.UserRepository;
import arun.auth.server.custom.domain.CustomUser;
import arun.auth.server.custom.domain.SSOUser;
import arun.auth.server.custom.domain.SSOUserProfile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Custom User Details service
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCustomRepository userCustomRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserCustomEntity> userCustomEntityOptional = Optional.ofNullable(userCustomRepository.findOne(username));
        if (userCustomEntityOptional.isPresent()) {
            Optional<UserEntity> userEntityOptional = Optional.ofNullable(findUserByProviderUserId(userCustomEntityOptional.get().getUsername()));
            if (userEntityOptional.isPresent()) {
                return new SSOUser(userEntityOptional.get().getUserId(),
                        userCustomEntityOptional.get().getPassword(),
                        userEntityOptional.get().isEnabled());
            }
        }
        throw new UsernameNotFoundException("User [" + username + "] not found");
    }

    @Transactional
    public boolean createCustomUser(CustomUser customUser) {
        UserCustomEntity userCustomEntity = userCustomRepository.findOne(customUser.getUsername());
        if (userCustomEntity == null) {
            String hashedPassword = passwordEncoder.encode(customUser.getPassword());
            userCustomEntity = new UserCustomEntity(customUser.getUsername(), hashedPassword);
            userCustomRepository.save(userCustomEntity);
            return true;
        }
        return false;
    }

    public SSOUserProfile findUserProfileByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = Optional.ofNullable(userRepository.findOne(username));
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            Optional<ProfileEntity> profileEntityOptional = Optional.ofNullable(profileRepository.findOne(userEntity.getUserId()));
            return profileEntityOptional.map(profileEntity -> SSOUserProfile.builder()
                    .id(profileEntity.getUserId())
                    .firstName(profileEntity.getFirstName())
                    .middleName(profileEntity.getMiddleName())
                    .lastName(profileEntity.getLastName())
                    .email(profileEntity.getEmail())
                    .build()
            ).orElseGet(() -> SSOUserProfile.builder()
                    .id(userEntity.getUserId())
                    .build()
            );
        } else {
            throw new UsernameNotFoundException("User [" + username + "] not found");
        }
    }

    @Transactional
    public SSOUserProfile upsertUserProfile(SSOUserProfile SSOUserProfile) throws UsernameNotFoundException {
        if (SSOUserProfile != null) {
            Optional<ProfileEntity> profileEntityOptional = Optional.ofNullable(profileRepository.findOne(SSOUserProfile.getId()));
            ProfileEntity profileEntity;

            if (profileEntityOptional.isPresent()) {
                profileEntity = profileEntityOptional.get();
                profileEntity.setFirstName(SSOUserProfile.getFirstName());
                profileEntity.setMiddleName(SSOUserProfile.getMiddleName());
                profileEntity.setLastName(SSOUserProfile.getLastName());
                profileEntity.setEmail(SSOUserProfile.getEmail());

            } else {
                profileEntity = new ProfileEntity(SSOUserProfile.getId());
            }
            if (!SSOUserProfile.isCustomProvider()) {
                deleteProvider(SSOUserProfile.getId(), AuthProviderEnum.CUSTOM);
            }
            if (!SSOUserProfile.isGoogleProvider()) {
                deleteProvider(SSOUserProfile.getId(), AuthProviderEnum.GOOGLE);
            }
            if (!SSOUserProfile.isFacebookProvider()) {
                deleteProvider(SSOUserProfile.getId(), AuthProviderEnum.FACEBOOK);
            }
            if (!SSOUserProfile.isGithubProvider()) {
                deleteProvider(SSOUserProfile.getId(), AuthProviderEnum.GITHUB);
            }
            profileRepository.save(profileEntity);


        }
        return SSOUserProfile;
    }

    public UserEntity findUserByProviderUserId(String externalId) {
        Optional<AuthProviderEntity> authProviderEntityOptional = Optional.ofNullable(authProviderRepository.findOne(externalId));
        return authProviderEntityOptional.map(authProviderEntity -> userRepository.findOne(authProviderEntity.getUserId())).orElse(null);
    }

    @Transactional
    public String createUserAndProvider(String userId, String providerUserId, AuthProviderEnum provider) {
        AuthProviderEntity authProviderEntity;
        if (StringUtils.isEmpty(userId)) {
            userId = generateUserId();
            UserEntity userEntity = new UserEntity(userId, true, true, true, true);
            userRepository.save(userEntity);
            authProviderEntity = new AuthProviderEntity(providerUserId, userId, provider.toString());
        } else {
            if (isUserExists(userId)) {
                authProviderEntity = new AuthProviderEntity(providerUserId, userId, provider.toString());
            } else {
                throw new UsernameNotFoundException("User [" + userId + "] not found");
            }
        }
        authProviderRepository.save(authProviderEntity);
        return authProviderEntity.getUserId();
    }

    public List<String> findAllProvidersByUserName(String username) {
        List<AuthProviderEntity> providerEntities = authProviderRepository.findAllByUserId(username);
        List<String> providers = null;
        if (providerEntities != null && !providerEntities.isEmpty()) {
            providers = new ArrayList<>();
            for (AuthProviderEntity providerEntity : providerEntities) {
                providers.add(providerEntity.getProvider());
            }
        }
        return providers;
    }

    @Transactional
    public void deleteProvider(String username, AuthProviderEnum provider) {
        authProviderRepository.deleteAllByUserIdAndProvider(username, provider.toString());
    }

    public SSOUser findUserByUserName(String username) {
        Optional<UserEntity> userEntityOptional = Optional.ofNullable(userRepository.findOne(username));
        return userEntityOptional.map(userEntity ->
                new SSOUser(userEntity.getUserId(), "N/A", userEntity.isEnabled())
        ).orElse(null);
    }

    private boolean isUserExists(String userId) {
        Optional<UserEntity> userEntityOptional = Optional.ofNullable(userRepository.findOne(userId));
        return userEntityOptional.isPresent();
    }

    private String generateUserId() {
        String userId = UUID.randomUUID().toString();
        int attempt = 0;
        do {
            if (isUserExists(userId)) {
                userId = UUID.randomUUID().toString();
                attempt++;
            } else {
                break;
            }
        } while (attempt < 5);
        return userId;
    }

}
