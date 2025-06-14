package in.bg.ByeByeBG.repository;

import in.bg.ByeByeBG.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByClerkId(String clerkId);

    boolean existsByClerkId(String clerkId);
}
