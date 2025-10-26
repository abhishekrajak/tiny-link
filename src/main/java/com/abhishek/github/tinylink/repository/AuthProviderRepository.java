package com.abhishek.github.tinylink.repository;

import com.abhishek.github.tinylink.model.AuthProviderEntity;
import com.abhishek.github.tinylink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthProviderRepository extends JpaRepository<AuthProviderEntity, UUID> {
    Optional<AuthProviderEntity> findByProviderAndProviderUserId(AuthProviderEntity.AuthProvider provider, String providerUserId);

    boolean existsByProviderAndProviderUserId(AuthProviderEntity.AuthProvider provider, String providerUserId);

    void deleteByUserAndProvider(User user, AuthProviderEntity.AuthProvider provider);
}
