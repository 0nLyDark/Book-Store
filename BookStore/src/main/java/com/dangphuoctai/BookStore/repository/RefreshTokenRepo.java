package com.dangphuoctai.BookStore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.RefreshToken;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String refreshtoken);

    @Query("DELETE FROM RefreshToken rft WHERE rft.user.userId = :userId")
    void deleteByUserId(Long userId);

}
