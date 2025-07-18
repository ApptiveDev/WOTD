package com.example.apptive_3team.controller;

import com.example.apptive_3team.ApiResponse;
import com.example.apptive_3team.dto.ItemRequestDTO;
import com.example.apptive_3team.service.ItemService;
import com.example.apptive_3team.service.KakaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 챙길 물품 관련 기능을 수행하는 Controller.
 */
@Slf4j
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final KakaoService kakaoService;

    // itemId로 조회
    @GetMapping("/requestById/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable Long itemId) {
        log.info("📥 [GET] item/request/{itemId} API 호출됨");

        ItemRequestDTO data = itemService.getItemById(itemId);
        log.debug("찾은 item: item_id={}, name={}, deadline={}", data.id(), data.name(), data.deadline());

        return ResponseEntity.ok(ApiResponse.success("챙길 물품 조회를 완료했습니다.", data));
    }

    // date로 조회
    @GetMapping("/requestByDate/{date}")
    public ResponseEntity<?> getItemsByDate(@RequestHeader("Authorization") String token,
                                            @PathVariable LocalDate date) {
        log.info("📥 [GET] item/request/{date} API 호출됨");

        Long user_id = kakaoService.getUserIdFromJwtToken(token);

        Optional<ItemRequestDTO> data = itemService.getItemByDateAndUserId(date, user_id);
        if (data.isPresent()) {
            ItemRequestDTO item = data.get();
            log.debug("찾은 item: item_id={}, name={}, deadline={}", item.id(), item.name(), item.deadline());
        } else {
            log.warn("해당 날짜에 일치하는 item이 없습니다. date={}, userId={}", date, user_id);
        }

        return ResponseEntity.ok(ApiResponse.success("챙길 물품 조회를 완료했습니다.", data));
    }

    /**
     * 챙길 물품의 정보를 DB에 저장하는 기능.
     *
     * @param request
     * @return 저장 여부에 대한 통보
     */
    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestHeader("Authorization") String token,
                                     @Valid @RequestBody ItemRequestDTO request) {
        log.info("📥 [POST] item/add API 호출됨");

        Long user_id = kakaoService.getUserIdFromJwtToken(token);

        itemService.saveItem(user_id, request);
        return ResponseEntity.ok(ApiResponse.success("챙길 물품 등록을 완료했습니다."));
    }

    /**
     * DB에 저장된 챙길 물품의 정보를 수정하는 기능.
     *
     * @param request
     * @return 수정 여부에 대한 통보
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateItem(@RequestHeader("Authorization") String token,
                                        @RequestBody ItemRequestDTO request) {
        log.info("📥 [POST] /item/update API 호출됨");

        Long user_id = kakaoService.getUserIdFromJwtToken(token);
        itemService.updateItem(user_id, request);

        return ResponseEntity.ok(ApiResponse.success("챙길 물품 수정을 완료했습니다."));
    }

    /**
     * DB에 저장된 챙길 물품의 정보를 삭제하는 기능.
     *
     * @return 삭제 여부에 대한 통보
     */
    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId,
                                        @RequestHeader("Authorization") String token) {
        log.info("📥 [DELETE] /item/delete/{itemId} API 호출됨");

        Long user_id = kakaoService.getUserIdFromJwtToken(token);
        itemService.deleteItem(user_id, itemId); // ← itemId만 넘김
        return ResponseEntity.ok(ApiResponse.success("챙길 물품 삭제를 완료했습니다."));
    }

}