package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.dto.TinyLinkUserDTO;
import com.abhishek.github.tinylink.model.AuthProviderEntity;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.UserRepository;
import com.abhishek.github.tinylink.util.DemoUserGenerator;
import com.abhishek.github.tinylink.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public TinyLinkUserDTO createAndSaveDemoUser() {
        String demoUserId = DemoUserGenerator.generateDemoUserId();
        String userName = "demo-" + demoUserId;

        String emailId = String.format("%s@armatrix.dev", userName);

        User newUser = generateUserBasisParams(
                emailId,
                userName,
                false,
                AuthProviderEntity.AuthProvider.DEMO,
                userName,
                "",
                User.UserRole.ROLE_USER,
                User.UserType.BASE
        );

        newUser.setEmailId(emailId);
        newUser.setName(userName);
        newUser.setUsername(emailId);
        newUser.setPassword("");
        newUser.setRegistrationCompleted(false);
        newUser.setRoles(Set.of(User.UserRole.ROLE_USER));
        newUser.setUserType(User.UserType.BASE);
        newUser.setCreatedAt(Instant.now());
        newUser.addAuthProvider(AuthProviderEntity.AuthProvider.DEMO, userName);

        User createdUser = userRepository.save(newUser);

        String jwtToken = jwtTokenUtil.generateToken(newUser.getUserId().toString(), newUser.getEmailId(), newUser.getName(),
                newUser.getRoles()
                        .stream()
                        .map(Enum::name)
                        .toList());

        return new TinyLinkUserDTO(createdUser.getEmailId(), jwtToken);
    }

    @Transactional
    public User createAndSaveUser(String email, String name, boolean isEmailVerified,
                                  AuthProviderEntity.AuthProvider
                                  provider, String providerUserId,
                                  String password, User.UserRole userRole,
                                  User.UserType userType) {

        User newUser = generateUserBasisParams(
                email, name,
                isEmailVerified, provider, providerUserId,
                password, userRole, userType
        );

        return userRepository.save(newUser);
    }

    User generateUserBasisParams(String email, String name, boolean isEmailVerified,
                                 AuthProviderEntity.AuthProvider
                                         provider, String providerUserId,
                                 String password, User.UserRole userRole,
                                 User.UserType userType) {

        User user = new User();

        user.setEmailId(email);
        user.setName(name);
        user.setUsername(email);
        user.setPassword("");
        user.setRegistrationCompleted(isEmailVerified);
        user.setRoles(Set.of(userRole));
        user.setUserType(userType);
        user.setCreatedAt(Instant.now());
        user.addAuthProvider(provider, providerUserId);

        return user;
    }

}
