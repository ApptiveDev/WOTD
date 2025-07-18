package com.example.apptive_3team.service;

import com.example.apptive_3team.dto.ItemRequestDTO;
import com.example.apptive_3team.entity.Item;
import com.example.apptive_3team.exception.Item.ItemNotFoundException;
import com.example.apptive_3team.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 챙길 물품 관련 기능을 구현한 Service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 주어진 userId와 ItemDTO 정보를 기반으로 챙길 물품을 생성하고 DB에 저장하는 메서드.
     *
     * <p>validateUserHasFewerThan20Items 메서드를 이용하여
     *  사용자가 등록한 챙길물품이 20개 미만일 경우에만 물품등록이 가능하도록 구현.
     *
     * @param userId 사용자 ID
     * @param request 저장할 아이템 데이터
     */
    public void saveItem(Long userId, ItemRequestDTO request) {

        log.info("📥 [SERVICE] saveItem() 호출됨 - userId={}, deadline={}, name='{}'", userId, request.deadline(), request.name());

        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new IllegalArgumentException("물품 이름은 비어 있을 수 없습니다.");
        }
        if (request.deadline() == null) {
            throw new IllegalArgumentException("날짜는 반드시 입력해야 합니다.");
        }
        Optional<Item> existingItem = itemRepository.findByUserIdAndDeadline(userId, request.deadline());

        Item item = new Item();

        if (existingItem.isPresent()) {
            item.setId(existingItem.get().getId());
            item.setName(request.name());
        } else {
            item.setUserId(userId);
            item.setName(request.name());
            item.setDeadline(request.deadline());
        }
        log.info("✅ 저장 완료 - itemId={}, userId={}, name='{}', deadline={}",
                item.getId(), item.getUserId(), item.getName(), item.getDeadline());
        itemRepository.save(item);
    }

    /**
     * 기존에 저장된 챙길 물품의 내용을 수정하여 DB에 저장하는 메서드.
     *
     * <p>주의) 메서드 실행 전, 전달받은 챙길 물품 Id로 userId를 조회하여
     * 올바른 사용자가 요청한 것인지 확인이 필요.
     *
     * @param request 챙길 물품 DTO
     */
    @Transactional
    public void updateItem(Long userId, ItemRequestDTO request) {
        Item item = itemRepository.findById(request.id())
                .orElseThrow(ItemNotFoundException::new);

        if (!item.getUserId().equals(userId)) {
            throw new AccessDeniedException("해당 아이템을 수정할 권한이 없습니다.");
        }

        item.setName(request.name());
        item.setDeadline(request.deadline());
    }

    /**
     * itemDTO의 챙길 물품 Id를 기반으로, 기존에 저장된 챙길 물품을 DB에서 삭제하는 메서드
     * @param userId
     * @param itemId
     */
    @Transactional
    public void deleteItem(Long userId, Long itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        if (!item.getUserId().equals(userId)) {
            throw new AccessDeniedException("해당 아이템을 삭제할 권한이 없습니다.");
        }

        itemRepository.delete(item);
    }

    // 📌 날짜 + 사용자로 조회
    public Optional<ItemRequestDTO> getItemByDateAndUserId(LocalDate date, Long userId) {
        log.info("📥 [SERVICE] getItemByDateAndUserId() 호출됨 - userId={}, date={}", userId, date);

        return itemRepository.findByUserIdAndDeadline(userId, date)
                .filter(item -> {
                    boolean valid = item.getName() != null && !item.getName().isBlank();
                    if (!valid) {
                        log.warn("⚠️ 조회된 item의 name이 null 또는 공백입니다. itemId={}", item.getId());
                    }
                    return valid;
                })
                .map(item -> {
                    log.info("✅ 조회 성공 - itemId={}, name='{}', deadline={}", item.getId(), item.getName(), item.getDeadline());
                    return new ItemRequestDTO(
                            item.getId(),
                            item.getName(),
                            item.getDeadline()
                    );
                });
    }

        // 📌 ID로 조회
        public ItemRequestDTO getItemById(Long id) {
            log.info("📥 [SERVICE] getItemById() 호출됨 - itemId={}", id);

            return itemRepository.findById(id)
                    .filter(item -> {
                        boolean valid = item.getName() != null && !item.getName().isBlank();
                        if (!valid) {
                            log.warn("⚠️ 조회된 item의 name이 null 또는 공백입니다. itemId={}", item.getId());
                        }
                        return valid;
                    })
                    .map(item -> {
                        log.info("✅ 조회 성공 - itemId={}, name='{}', deadline={}", item.getId(), item.getName(), item.getDeadline());
                        return new ItemRequestDTO(
                                item.getId(),
                                item.getName(),
                                item.getDeadline()
                        );
                    })
                    .orElseThrow(() -> {
                        log.warn("❌ itemId={}에 해당하는 아이템을 찾을 수 없습니다.", id);
                        return new ItemNotFoundException();
                    });
        }


    /**
     * item ID를 기반으로 챙길 물품 목록을 조회하여
     * 등록된 챙길 물품이 있는지 확인하는 예외처리 메서드.
     *
     * @param id 챙길 물품 ID
     */
    public void validateItemIsExist(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new ItemNotFoundException();
        }
    }

}
