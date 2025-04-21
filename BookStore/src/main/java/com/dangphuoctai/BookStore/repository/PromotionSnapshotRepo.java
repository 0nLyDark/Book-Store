package com.dangphuoctai.BookStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.PromotionSnapshot;

@Repository
public interface PromotionSnapshotRepo extends JpaRepository<PromotionSnapshot, Long> {

    PromotionSnapshot findByHash(String hash);

}
