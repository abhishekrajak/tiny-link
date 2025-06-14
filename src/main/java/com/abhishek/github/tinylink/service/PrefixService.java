package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.model.Prefix;
import com.abhishek.github.tinylink.repository.PrefixRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrefixService {

    PrefixRepository prefixRepository;

    public PrefixService(PrefixRepository prefixRepository) {
        this.prefixRepository = prefixRepository;
    }

    List<Prefix> getAllPrefixes() {
        return prefixRepository.findAll();
    }

}
