package com.abhishek.github.tinylink.repository;


import com.abhishek.github.tinylink.model.Prefix;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrefixRepository extends JpaRepository<Prefix, Long> {
}
