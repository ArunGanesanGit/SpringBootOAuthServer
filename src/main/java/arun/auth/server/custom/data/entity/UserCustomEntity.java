package arun.auth.server.custom.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for Custom User Table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users_custom")
public class UserCustomEntity {

    @Id
    private String username;

    private String password;

}
