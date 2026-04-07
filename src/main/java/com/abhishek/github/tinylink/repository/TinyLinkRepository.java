package com.abhishek.github.tinylink.repository;

import com.abhishek.github.tinylink.model.Prefix;
import com.abhishek.github.tinylink.model.TinyLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TinyLinkRepository extends JpaRepository<TinyLink, Long> {

    Optional<TinyLink> findByTinyCode(String tinyCode);

    @Query("SELECT COUNT(p) > 0 FROM Prefix p WHERE LOWER(LEFT(:code, LENGTH(p.prefix))) = LOWER(p.prefix)")
    boolean existsPrefixConflict(@Param("code") String code);

    @Query(value = "SELECT * FROM Prefixes p " +
            "WHERE LOWER(LEFT(:code, LENGTH(p.prefix))) = LOWER(p.prefix) " +
            "ORDER BY LENGTH(p.prefix) DESC LIMIT 1", nativeQuery = true)
    Optional<Prefix> findFirstMatchingPrefix(@Param("code") String code);

    boolean existsTinyLinkByTinyCode(String tinyCode);

    @Query("SELECT t from TinyLink t where t.user.userId = :userId")
    List<TinyLink> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(t) FROM TinyLink t WHERE t.user.userId = :userId")
    long countByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query(value = "UPDATE tiny_links SET status = :status WHERE user_id = :userId AND tiny_code = :tinyCode", nativeQuery = true)
    int updateTinyLinkStatus(@Param("userId") UUID userId, @Param("tinyCode") String
                              tinyCode, @Param("status") String status);
}
