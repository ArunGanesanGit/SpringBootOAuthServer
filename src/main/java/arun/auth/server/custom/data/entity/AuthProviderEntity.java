package arun.auth.server.custom.data.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for External Provider Users Table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users_auth_provider")
public class AuthProviderEntity {

    @Id
    private String providerUserId;

    private String userId;

    private String provider;

}
