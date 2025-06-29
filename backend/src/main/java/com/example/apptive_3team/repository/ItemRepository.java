package com.example.apptive_3team.repository;

import com.example.apptive_3team.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 챙길 물품 데이터를 관리하는 Repository.
 *
 * <p><구성 기능>
 * <br>findById(Long id): item_id를 기반으로 챙길 물품 데이터를 조회
 * <br>findByUser_id(Long user_id): user_id를 기반으로 챙길 물품 데이터를 조회
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * item_id를 기반으로 저장한 챙길 물품 데이터를 조회.
     *
     * @param id 챙길 물품 ID
     * @return 챙길 물품 객체를 Optional로 감싼 형태로 반환.
     */
    Optional<Item> findById(Long id);

    /**
     * user_id를 기반으로 저장한 챙길 물품 데이터를 조회.
     *
     * @param userId 사용자 ID
     * @return 챙길 물품 리스트를 Optional로 감싼 형태로 반환.
     */
    Optional<List<Item>> findByUserId(Long userId);

    /**
     * 사용자 id와 날짜를 기반으로, 한 사용자가 한 날짜에 필요한 챙길 물품을 조회.
     *
     * @param userId 사용자 ID
     * @param deadline 챙길 물품이 필요한 날짜
     * @return 챙길 물품 리스트를 Optional로 감싼 형태로 반환.
     */
    Optional<List<Item>> findByUserIdAndDeadline(Long userId, LocalDate deadline);
}
