package com.ore.backend.productImage.controller;

import com.ore.backend.productImage.dto.ProductImageResponse;
import com.ore.backend.productImage.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping(
            value = "/{productId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductImageResponse> uploadProductImage(
            @PathVariable UUID productId,
            @RequestPart("image") MultipartFile image
    ) {
        ProductImageResponse response = productImageService.uploadProductImage(productId, image);

        return ResponseEntity.ok(response);
    }
}