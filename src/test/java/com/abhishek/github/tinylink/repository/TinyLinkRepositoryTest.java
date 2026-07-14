package com.abhishek.github.tinylink.repository;

import com.abhishek.github.tinylink.model.LinkStatus;
import com.abhishek.github.tinylink.model.TinyLink;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.model.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
public class TinyLinkRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("tiny_link_test")
            .withUsername("test_user")
            .withPassword("test_password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private TinyLinkRepository tinyLinkRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndQueryTinyLink() {
        User user = new User();
        user.setEmailId("alice@example.com");
        user.setName("Alice");
        user.setUsername("alice");
        user.setPassword("secret");
        user.setCreatedAt(Instant.now());
        user.setUserType(User.UserType.BASE);
        user.setUserStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

        TinyLink tinyLink = new TinyLink(
                "ABC12345",
                "https://example.com",
                user,
                false,
                "AUTO",
                LinkStatus.ACTIVE
        );
        tinyLink = tinyLinkRepository.save(tinyLink);

        Optional<TinyLink> found = tinyLinkRepository.findByTinyCode("ABC12345");
        assertThat(found).isPresent();
        assertThat(found.get().getTinyCode()).isEqualTo("ABC12345");

        List<TinyLink> byUser = tinyLinkRepository.findByUserId(user.getUserId(), LinkStatus.ACTIVE.name());
        assertThat(byUser).hasSize(1);

        long count = tinyLinkRepository.countByUserId(user.getUserId());
        assertThat(count).isEqualTo(1);

        boolean exists = tinyLinkRepository.existsTinyLinkByTinyCode("ABC12345");
        assertThat(exists).isTrue();
    }

    @Test
    void updateTinyLinkStatus() {
        User user = new User();
        user.setEmailId("bob@example.com");
        user.setName("Bob");
        user.setUsername("bob");
        user.setPassword("secret");
        user.setCreatedAt(Instant.now());
        user.setUserType(User.UserType.BASE);
        user = userRepository.save(user);

        TinyLink tinyLink = new TinyLink(
                "XYZ98765",
                "https://redirect.example.com",
                user,
                true,
                "CUSTOM",
                LinkStatus.ACTIVE
        );
        tinyLink = tinyLinkRepository.save(tinyLink);

        Optional<TinyLink> before = tinyLinkRepository.findByTinyCode("XYZ98765");
        assertThat(before).isPresent();
        assertThat(before.get().getStatus()).isEqualTo(LinkStatus.ACTIVE);

        int updated = tinyLinkRepository.updateTinyLinkStatus(
                user.getUserId(),
                "XYZ98765",
                LinkStatus.INACTIVE.name()
        );

        assertThat(updated).isEqualTo(1);

        Optional<TinyLink> after = tinyLinkRepository.findByTinyCode("XYZ98765");
        assertThat(after).isPresent();
        assertThat(after.get().getStatus()).isEqualTo(LinkStatus.INACTIVE);
    }
}

