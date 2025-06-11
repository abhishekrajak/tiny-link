package com.abhishek.github.tinylink.repository;

import com.abhishek.github.tinylink.model.TinyLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TinyLinkRepository extends JpaRepository<TinyLink, Long> {

    List<TinyLink> findByTinyCode(String tinyCode);
 }
