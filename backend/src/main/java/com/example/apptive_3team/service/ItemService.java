package com.example.apptive_3team.service;

import com.example.apptive_3team.dto.ItemDTO;
import com.example.apptive_3team.entity.Item;
import com.example.apptive_3team.exception.Item.ItemNotFoundException;
import com.example.apptive_3team.exception.Item.UserAlreadyHas20ItemsException;
import com.example.apptive_3team.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 챙길 물품 관련 기능을 구현한 Service.
 */
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    /**
     * 주어진 userId와 ItemDTO 정보를 기반으로 챙길 물품을 생성하고 DB에 저장하는 메서드.
     *
     * <p>validateUserHasFewerThan20Items 메서드를 이용하여
     *  사용자가 등록한 챙길물품이 20개 미만일 경우에만 물품등록이 가능하도록 구현.
     *
     * @param userId 사용자 ID
     * @param itemDTO 저장할 아이템 데이터
     */
    public void saveItem(Long userId, ItemDTO itemDTO) {

        validateUserHasFewerThan20ItemsOnDate(userId, itemDTO.deadline());

        Item item = new Item(userId, itemDTO.name(), itemDTO.deadline());

        itemRepository.save(item);
    }

    /**
     * 기존에 저장된 챙길 물품의 내용을 수정하여 DB에 저장하는 메서드.
     *
     * <p>주의) 메서드 실행 전, 전달받은 챙길 물품 Id로 userId를 조회하여
     * 올바른 사용자가 요청한 것인지 확인이 필요.
     *
     * @param itemDTO 챙길 물품 DTO
     */
    @Transactional
    public void updateItem(ItemDTO itemDTO) {

        Item item = itemRepository.findById(itemDTO.id())
                .orElseThrow(ItemNotFoundException::new);

        item.setName(itemDTO.name());
        item.setDeadline(itemDTO.deadline());
    }

    /**
     * itemDTO의 챙길 물품 Id를 기반으로, 기존에 저장된 챙길 물품을 DB에서 삭제하는 메서드.
     *
     * @param itemDTO 챙길 물품 DTO
     */
    @Transactional
    public void deleteItem(ItemDTO itemDTO) {
        Long itemId = itemDTO.id();

        validateItemIsExist(itemId);

        itemRepository.deleteById(itemId);
    }

    /**
     * userId를 기반으로 챙길 물품 목록을 조회하는 메서드.
     *
     * @param userId 조회할 사용자의 ID
     *
     * @return 해당 사용자 ID에 속한 챙길 물품 DTO 리스트를 Optional로 감싼 형태로 반환
     */
    public Optional<List<ItemDTO>> getItemsByUserId(Long userId) {
        return itemRepository.findByUserId(userId)
                .map(items -> items.stream()
                        .map(item -> new ItemDTO(
                                null,
                                item.getName(),
                                item.getDeadline()
                        ))
                        .toList());
    }

    /**
     * 챙길 물품 ID를 기반으로 챙길 물품 1개를 조회하는 메서드.
     *
     * @param id 챙길 물품 ID
     *
     * @return 챙길 물품 DTO를 Optional로 감싼 형태로 반환
     */
    public Optional<ItemDTO> getItemById(Long id) {

        validateItemIsExist(id);

        return itemRepository.findById(id)
                .map(item -> new ItemDTO(
                        null,
                        item.getName(),
                        item.getDeadline()
                ));
    }

    /**
     * userId를 기반으로 챙길 물품 목록을 조회하여
     * 사용자가 등록한 챙길 물품 개수가 20개 미만인지
     * 확인하는 예외처리 메서드.
     *
     * @param userId 조회할 사용자의 ID
     * @param deadline 조회할 날짜
     */
    public void validateUserHasFewerThan20ItemsOnDate(Long userId, LocalDate deadline) {
        Optional<List<Item>> itemsOnDate = itemRepository.findByUserIdAndDeadline(userId, deadline);
        boolean hasFewerThan20 = itemsOnDate.map(items -> items.size() < 20).orElse(true);

        if (!hasFewerThan20) {
            throw new UserAlreadyHas20ItemsException();
        }
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
