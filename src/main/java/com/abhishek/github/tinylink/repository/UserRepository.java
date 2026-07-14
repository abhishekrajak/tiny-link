package com.abhishek.github.tinylink.repository;

import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailId(String emailId);

    Boolean existsByEmailId(String emailId);

    @Modifying(clearAutomatically = true)
    @Query(value =
            "UPDATE users " +
                    "SET user_status = :userStatus " +
                    "WHERE user_id = :userId",
            nativeQuery = true
    )
    int updateUser(@Param("userId") UUID userId, @Param("userStatus") String userStatus);
    
}
