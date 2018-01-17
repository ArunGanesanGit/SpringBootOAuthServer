package arun.auth.server.custom.data.repository;

import arun.auth.server.custom.data.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by GZ4QPF on 12/11/2017.
 */
public interface UserRepository extends CrudRepository<UserEntity, String> {
}
