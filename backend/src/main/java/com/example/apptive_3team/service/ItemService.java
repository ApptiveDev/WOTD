package com.example.apptive_3team.service;

import com.example.apptive_3team.dto.ItemRequestDTO;
import com.example.apptive_3team.entity.Item;
import com.example.apptive_3team.exception.Item.ItemNotFoundException;
import com.example.apptive_3team.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    /**
     * 사용자 ID와 날짜를 기반으로 챙길 물품 목록을 조회하는 메서드.
     *
     * @param date 조회할 날짜
     *
     * @return 해당 사용자 ID에 속한 챙길 물품 DTO 리스트를 Optional로 감싼 형태로 반환
     */
        public Optional<ItemRequestDTO> getItemByDateAndUserId(LocalDate date, Long userId) {
            return itemRepository.findByUserIdAndDeadline(userId, date)
                    .filter(item -> item.getName() != null && !item.getName().isBlank())
                    .map(item -> new ItemRequestDTO(
                            item.getId(),
                            item.getName(),
                            item.getDeadline()
                    ));
        }

        /**
         * 챙길 물품 ID를 기반으로 챙길 물품 1개를 조회하는 메서드.
         *
         * @param id 챙길 물품 ID
         *
         * @return 챙길 물품 DTO를 Optional로 감싼 형태로 반환
         */
        public ItemRequestDTO getItemById(Long id) {

            return itemRepository.findById(id)
                    .filter(item -> item.getName() != null && !item.getName().isBlank())
                    .map(item -> new ItemRequestDTO(
                            item.getId(),
                            item.getName(),
                            item.getDeadline()
                    )).orElseThrow(ItemNotFoundException::new);
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
