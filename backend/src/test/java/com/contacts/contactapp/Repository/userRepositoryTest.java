package com.contacts.contactapp.Repository;

import com.contacts.contactapp.model.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface userRepository extends JpaRepository<user, Long> {
    Optional<user> findByEmail(String email);
}