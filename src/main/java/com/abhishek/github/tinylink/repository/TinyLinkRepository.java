package com.abhishek.github.tinylink.repository;

import com.abhishek.github.tinylink.model.Prefix;
import com.abhishek.github.tinylink.model.TinyLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TinyLinkRepository extends JpaRepository<TinyLink, Long> {

    List<TinyLink> findByTinyCode(String tinyCode);

    @Query("SELECT COUNT(p) > 0 FROM Prefix p WHERE LOWER(LEFT(:code, LENGTH(p.prefix))) = LOWER(p.prefix)")
    boolean existsPrefixConflict(@Param("code") String code);

    @Query(value = "SELECT * FROM Prefixes p " +
            "WHERE LOWER(LEFT(:code, LENGTH(p.prefix))) = LOWER(p.prefix) " +
            "ORDER BY LENGTH(p.prefix) DESC LIMIT 1", nativeQuery = true)
    Optional<Prefix> findFirstMatchingPrefix(@Param("code") String code);

    boolean existsTinyLinkByTinyCode(String tinyCode);
}
