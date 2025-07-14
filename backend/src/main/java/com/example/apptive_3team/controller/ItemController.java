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

import java.util.List;
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

    /**
     * 챙길 물품 ID를 기반으로 챙길 물품 1개를 조회하는 기능.
     *
     * @return 챙길 물품 1개에 대한 정보
     */
    @GetMapping("/request/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable Long itemId) {
        log.info("📥 [GET] item/request/{itemId} API 호출됨");

        ItemRequestDTO data = itemService.getItemById(itemId);
        return ResponseEntity.ok(ApiResponse.success("챙길 물품 조회를 완료했습니다.", data));
    }

    /**
     * user_id를 기반으로 등록된 모든 items 출력하는 함수
     *
     * @param token
     * 헤더의 토큰 추출하여 user_id 검증
     *
     * @return
     *
     * GET /items/requestAll
     * Authorization: Bearer eyJ0eXAiOiJKV1QiLCJh...
     */
    @GetMapping("/requestAll")
    public ResponseEntity<?> getItems(@RequestHeader("Authorization") String token) {
        log.info("📥 [GET] item/requestAll API 호출됨");

        Long user_id = kakaoService.getUserIdFromJwtToken(token);

        Optional<List<ItemRequestDTO>> data = itemService.getItemsByUserId(user_id);

        if (data.isPresent() && !data.get().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("챙길 물품 조회를 완료했습니다.", data));
        } else {
            return ResponseEntity.ok(ApiResponse.success(
                    "챙길 물품 조회를 완료했습니다.",
                    "챙겨야 할 물품이 없어요! 혹시 챙겨야 할 물품이 있나요?"
            ));
        }
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
                                        @Valid @RequestBody ItemRequestDTO request) {
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
