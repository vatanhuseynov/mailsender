package io.getarrays.userservice.repository;

import io.getarrays.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmailIgnoreCase(String email);
    Boolean existsByEmail(String email);
}
