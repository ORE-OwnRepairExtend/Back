package com.ore.backend.product.controller;

import com.ore.backend.product.dto.*;
import com.ore.backend.product.service.ProductService;
import com.ore.backend.security.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PatchMapping(
            value = "/{productId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UpdateProductResponse> updateProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId,
            @RequestParam(required = false) String nickname,
            @RequestPart(required = false) MultipartFile image,
            @RequestParam(required = false) String category
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        UpdateProductResponse response = productService.updateProduct(
                userId,
                productId,
                nickname,
                image,
                category
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{productId}/favorite")
    public ResponseEntity<UpdateProductFavoriteResponse> updateFavorite(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductFavoriteRequest request
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        UpdateProductFavoriteResponse response = productService.updateFavorite(
                userId,
                productId,
                request.getIsFavorite()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/warranty")
    public ResponseEntity<GetProductWarrantyResponse> getProductWarranty(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        GetProductWarrantyResponse response = productService.getProductWarranty(userId, productId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        UUID userId = jwtProvider.getUserId(accessToken);

        productService.deleteProduct(userId, productId);

        return ResponseEntity.noContent().build();
    }
}