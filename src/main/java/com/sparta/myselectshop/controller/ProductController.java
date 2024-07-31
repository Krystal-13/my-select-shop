package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.ApiUseTime;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.ApiUseTimeRepository;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final ApiUseTimeRepository apiUseTimeRepository;
    @PostMapping("/products")
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        long startTime = System.currentTimeMillis();

        try {
            return productService.createProduct(requestDto, userDetails.getUser());
        } finally {
            long endTime = System.currentTimeMillis();
            long runTime = endTime - startTime;

            User loginUser = userDetails.getUser();

            ApiUseTime apiUseTime = apiUseTimeRepository.findByUser(loginUser)
                    .orElse(null);
            if (apiUseTime == null) {
                apiUseTime = new ApiUseTime(loginUser, runTime);
            } else {
                apiUseTime.addUseTime(runTime);
            }

            System.out.println("[API Use Time] Username: " + loginUser.getUsername() + ", Total Time: " + apiUseTime.getTotalTime() + " ms");
            apiUseTimeRepository.save(apiUseTime);
        }
    }

    @PutMapping("/products/{id}")
    public ProductResponseDto updateProduct(@PathVariable Long id,
                                            @RequestBody ProductMypriceRequestDto requestDto
    ) {
        return productService.updateProduct(id, requestDto);
    }

    @GetMapping("/products")
    public Page<ProductResponseDto> getProducts(@RequestParam("page") int page,
                                                @RequestParam("size") int size,
                                                @RequestParam("sortBy") String sortBy,
                                                @RequestParam("isAsc") boolean isAsc,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return productService.getProducts(userDetails.getUser(),  page-1, size, sortBy, isAsc);
    }

    @PostMapping("/products/{productId}/folder")
    public void addFolder(@PathVariable Long productId,
                          @RequestParam Long folderId,
                          @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
       productService.addFolder(productId, folderId, userDetails.getUser());
    }

    @GetMapping("/folders/{folderId}/products")
    public Page<ProductResponseDto> getProductsInFolder(@PathVariable Long folderId,
                                                        @RequestParam int page,
                                                        @RequestParam int size,
                                                        @RequestParam String sortBy,
                                                        @RequestParam boolean isAsc,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return productService.getProductsInFolder(folderId, page-1, size,
                                                    sortBy, isAsc, userDetails.getUser());
    }
}
