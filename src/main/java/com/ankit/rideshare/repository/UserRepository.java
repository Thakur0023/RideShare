package com.ankit.rideshare.repository;

import java.util.Optional;
import com.ankit.rideshare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
}
