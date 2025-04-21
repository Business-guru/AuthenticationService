package com.example.AuthenticationService.repository;

import com.example.AuthenticationService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmailId(String email);
    Optional<User> findByToken(String email);
    @Query("SELECT u.isVerified FROM User u WHERE u.emailId = :emailId")
    Boolean findIsVerifiedByEmail(@Param("emailId") String emailId);
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.emailId = :email")
    boolean existsByEmail(@Param("email") String email);
}
