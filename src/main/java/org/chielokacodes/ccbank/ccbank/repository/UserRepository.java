package org.chielokacodes.ccbank.ccbank.repository;

import org.chielokacodes.ccbank.ccbank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Optional<User> findUserByAccountNumber(String accountNumber);
}
