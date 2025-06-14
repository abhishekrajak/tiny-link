package com.abhishek.github.tinylink.repository;

import com.abhishek.github.tinylink.model.TinyLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TinyLinkRepository extends JpaRepository<TinyLink, Long> {

    List<TinyLink> findByTinyCode(String tinyCode);

    @Query("SELECT COUNT(p) > 0 FROM Prefix p WHERE LOWER(LEFT(:code, LENGTH(p.prefix))) = LOWER(p.prefix)")
    boolean existsPrefixConflict(@Param("code") String code);

    boolean existsTinyLinkByTinyCode(String tinyCode);
}
