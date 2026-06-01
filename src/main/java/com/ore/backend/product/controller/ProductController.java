package com.ore.backend.product.controller;

import com.ore.backend.product.dto.CreateProductRequest;
import com.ore.backend.product.dto.CreateProductResponse;
import com.ore.backend.product.dto.GetProductDetailResponse;
import com.ore.backend.product.dto.GetProductResponse;
import com.ore.backend.product.service.ProductService;
import com.ore.backend.security.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public ResponseEntity<CreateProductResponse> createProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody CreateProductRequest request
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        UUID userId = jwtProvider.getUserId(accessToken);

        CreateProductResponse response = productService.createProduct(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GetProductResponse>> getProducts(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) Boolean isFavorite
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        List<GetProductResponse> response = productService.getProducts(userId, isFavorite);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<GetProductDetailResponse> getProductDetail(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        GetProductDetailResponse response = productService.getProductDetail(userId, productId);

        return ResponseEntity.ok(response);
    }
}