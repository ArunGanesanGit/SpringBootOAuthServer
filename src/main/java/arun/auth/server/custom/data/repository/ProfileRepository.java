package arun.auth.server.custom.data.repository;

import arun.auth.server.custom.data.entity.ProfileEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for User Profile
 */
public interface ProfileRepository extends CrudRepository<ProfileEntity, String> {
}
