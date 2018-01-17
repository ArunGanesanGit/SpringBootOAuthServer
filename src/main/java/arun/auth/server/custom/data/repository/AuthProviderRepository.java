package arun.auth.server.custom.data.repository;

import arun.auth.server.custom.data.entity.AuthProviderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for External Provider User Details
 */
public interface AuthProviderRepository extends CrudRepository<AuthProviderEntity, String> {

    List<AuthProviderEntity> findAllByUserId(String username);

    void deleteAllByUserIdAndProvider(String username, String provider);

}
