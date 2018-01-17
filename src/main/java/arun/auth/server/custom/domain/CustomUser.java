package arun.auth.server.custom.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Model object to receive registration details
 */
@Data
public class CustomUser implements Serializable {

    private String username;
    private String password;
    private String confirmPassword;

}
