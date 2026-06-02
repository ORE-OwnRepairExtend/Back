package com.ore.backend.productManual.controller;

import com.ore.backend.productManual.dto.ProductManualResponse;
import com.ore.backend.productManual.service.ProductManualService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductManualController {

    private final ProductManualService productManualService;

    @GetMapping("/{productId}/manual")
    public ResponseEntity<ProductManualResponse> getProductManual(
            @PathVariable UUID productId
    ) {
        ProductManualResponse response = productManualService.getProductManual(productId);

        return ResponseEntity.ok(response);
    }
}