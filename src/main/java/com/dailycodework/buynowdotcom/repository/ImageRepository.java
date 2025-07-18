package com.dailycodework.buynowdotcom.repository;

import com.dailycodework.buynowdotcom.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProductId(Long productId);

    Optional<Image> findByFileName(String fileName);
}
