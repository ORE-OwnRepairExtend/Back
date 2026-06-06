package com.ore.backend.productManual.controller;

import com.ore.backend.productManual.dto.ProductManualResponse;
import com.ore.backend.productManual.dto.SearchProductManualRequest;
import com.ore.backend.productManual.service.ProductManualService;
import com.ore.backend.security.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductManualController {

    private final ProductManualService productManualService;
    private final JwtProvider jwtProvider;

    @GetMapping("/{productId}/manual")
    public ResponseEntity<ProductManualResponse> getProductManual(
            @PathVariable UUID productId
    ) {
        ProductManualResponse response = productManualService.getProductManual(productId);

        return ResponseEntity.ok(response);
    }

    // sourceId 기준으로 공식 매뉴얼 검색 및 저장
    @PostMapping("/manual/search")
    public ResponseEntity<ProductManualResponse> searchProductManual(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody SearchProductManualRequest request
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        ProductManualResponse response = productManualService.searchProductManual(
                userId,
                request.getSourceId()
        );

        return ResponseEntity.ok(response);
    }
}