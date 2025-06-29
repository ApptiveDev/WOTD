package com.example.apptive_3team.controller;

import com.example.apptive_3team.ApiResponse;
import com.example.apptive_3team.dto.ItemDTO;
import com.example.apptive_3team.dto.ItemRequestDTO;
import com.example.apptive_3team.service.ItemService;
import com.example.apptive_3team.service.KakaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 챙길 물품 관련 기능을 수행하는 Controller.
 */
@RestController
@RequestMapping("/item")
public class ItemController {
    private final ItemService itemService;
    private final KakaoService kakaoService;

    public ItemController(ItemService itemService,
                          KakaoService kakaoService) {
        this.itemService = itemService;
        this.kakaoService = kakaoService;
    }

    /**
     * 챙길 물품 ID를 기반으로 챙길 물품 1개를 조회하는 기능.
     *
     * @param request
     * @return 챙길 물품 1개에 대한 정보
     */
    @PostMapping("/request")
    public ResponseEntity<?> getItem(@RequestBody ItemRequestDTO request) {
        Optional<ItemDTO> data = itemService.getItemById(request.itemDTO().id());
        return ResponseEntity.ok(ApiResponse.success("챙길 물품 조회를 완료했습니다.", data));
    }

    /**
     * 사용자 ID를 기반으로 사용자가 등록한 모든 챙길 물품을 조회하는 기능.
     *
     * @param request
     * @return 챙길 물품에 대한 정보 리스트
     */
    @PostMapping("/requestAll")
    public ResponseEntity<?> getItems(@RequestBody ItemRequestDTO request) {
        Long user_id = kakaoService.getUserIdFromAccessToken(request.accessToken());
        Optional<List<ItemDTO>> data = itemService.getItemsByUserId(user_id);

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
    public ResponseEntity<?> addItem(@Valid @RequestBody ItemRequestDTO request) {
        Long user_id = kakaoService.getUserIdFromAccessToken(request.accessToken());

        itemService.saveItem(user_id, request.itemDTO());
        return ResponseEntity.ok(ApiResponse.success("챙길 물품 등록을 완료했습니다."));
    }

    /**
     * DB에 저장된 챙길 물품의 정보를 수정하는 기능.
     *
     * @param request
     * @return 수정 여부에 대한 통보
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateItem(@Valid @RequestBody ItemRequestDTO request) {
        itemService.updateItem(request.itemDTO());
        return ResponseEntity.ok(ApiResponse.success("챙길 물품 수정을 완료했습니다."));
    }

    /**
     * DB에 저장된 챙길 물품의 정보를 삭제하는 기능.
     *
     * @param request
     * @return 삭제 여부에 대한 통보
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteItem(@RequestBody ItemRequestDTO request) {
        itemService.deleteItem(request.itemDTO());
        return ResponseEntity.ok(ApiResponse.success("챙길 물품 삭제를 완료했습니다."));
    }
}
