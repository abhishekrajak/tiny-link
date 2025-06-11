package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.dto.TinyLinkDTO;
import com.abhishek.github.tinylink.model.TinyLink;
import com.abhishek.github.tinylink.repository.TinyLinkRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

@Service
public class TinyLinkService {

    TinyLinkRepository tinyLinkRepository;

    public TinyLinkService(TinyLinkRepository tinyLinkRepository) {
        this.tinyLinkRepository = tinyLinkRepository;
    }

    public Optional<String> getRedirectionUrl(String tinyCode){
        List<TinyLink> tinyLinkList = tinyLinkRepository.findByTinyCode(tinyCode);

        if (tinyLinkList.isEmpty()) return Optional.empty();

        TinyLinkDTO tinyLinkDTO =  new TinyLinkDTO(tinyLinkList.get(0));
        return Optional.of(tinyLinkDTO.getRedirectionLink());
    }
}
