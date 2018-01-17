package arun.auth.server.custom.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for User Profile Table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users_profile")
public class ProfileEntity {

    @Id
    private String userId;

    private String firstName;

    private String lastName;

    private String middleName;

    private String email;

    public ProfileEntity(String userId) {
        this.userId = userId;
    }
}
