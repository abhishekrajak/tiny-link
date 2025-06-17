package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.model.Prefix;
import com.abhishek.github.tinylink.repository.PrefixRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PrefixService {

    private final PrefixRepository prefixRepository;

    List<Prefix> getAllPrefixes() {
        return prefixRepository.findAll();
    }

}
