package com.ore.backend.productSource.controller;

import com.ore.backend.productSource.dto.CreateProductSourceResponse;
import com.ore.backend.productSource.service.ProductSourceService;
import com.ore.backend.security.JwtProvider;
import lombok.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-sources")
public class ProductSourceController {

    private final ProductSourceService productSourceService;
    private final JwtProvider jwtProvider;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateProductSourceResponse> createProductSource(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestPart("image") MultipartFile image,
            @RequestPart("sourceType") String sourceType
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");

        UUID userId = jwtProvider.getUserId(accessToken);

        CreateProductSourceResponse response =
                productSourceService.createProductSource(userId, image, sourceType);

        return ResponseEntity.ok(response);
    }

}