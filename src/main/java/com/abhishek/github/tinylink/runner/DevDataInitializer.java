package com.abhishek.github.tinylink.runner;

import com.abhishek.github.tinylink.dto.TinyLinkGenerateRequestDTO;
import com.abhishek.github.tinylink.model.Prefix;
import com.abhishek.github.tinylink.model.TinyLink;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.PrefixRepository;
import com.abhishek.github.tinylink.repository.TinyLinkRepository;
import com.abhishek.github.tinylink.repository.UserRepository;
import com.abhishek.github.tinylink.service.TinyLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!prod")
@Component
public class DevDataInitializer implements CommandLineRunner {

    TinyLinkService tinyLinkService;
    TinyLinkRepository tinyLinkRepository;

    UserRepository userRepository;

    PrefixRepository prefixRepository;

    @Autowired
    DevDataInitializer(TinyLinkRepository tinyLinkRepository, UserRepository userRepository,
                       PrefixRepository prefixRepository, TinyLinkService tinyLinkService) {
        this.tinyLinkRepository = tinyLinkRepository;
        this.userRepository = userRepository;
        this.prefixRepository = prefixRepository;
        this.tinyLinkService = tinyLinkService;
    }

    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setEmailId("abhishekrajak100@gmail.com");
        user.setUserType(User.UserType.BASE);
        user.setCreatedAt(java.time.Instant.now());
        userRepository.save(user);

        TinyLink tinyLink = new TinyLink("abcde", "https://www.github.com/abhishekrajak", user, true, "CUSTOM");
        tinyLinkRepository.save(tinyLink);

        Prefix prefix = new Prefix("xoxo", user, Prefix.ReservedFor.SPECIAL);
        Prefix prefix2 = new Prefix("abcd", user, Prefix.ReservedFor.SPECIAL);
        prefixRepository.save(prefix);
        prefixRepository.save(prefix2);

        boolean isPrefixAlreadyExists = tinyLinkRepository.existsPrefixConflict("xoxo");
        boolean isPrefixAlreadyExists2 = tinyLinkRepository.existsPrefixConflict("mango");

        System.out.println("isPrefixAlreadyExists = " + isPrefixAlreadyExists);
        System.out.println("isPrefixAlreadyExists = " + isPrefixAlreadyExists2);

        TinyLinkGenerateRequestDTO tinyLinkGenerateRequestDTO = new TinyLinkGenerateRequestDTO(
                "abcd", "https://www.linkedin.com", user
        );

        tinyLinkService.insertTinyLink(tinyLinkGenerateRequestDTO);
    }

}
