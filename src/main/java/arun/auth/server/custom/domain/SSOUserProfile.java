package arun.auth.server.custom.domain;

import lombok.*;

import java.io.Serializable;

/**
 * It is a common User Details model having user profile info
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class SSOUserProfile implements Serializable {

    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private boolean customProvider;
    private boolean googleProvider;
    private boolean facebookProvider;
    private boolean githubProvider;

}
