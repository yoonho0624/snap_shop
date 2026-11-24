package com.yunho.shop.controller.cart;

import com.yunho.shop.dto.cart.CartItemAddRequest;
import com.yunho.shop.dto.cart.CartItemResponse;
import com.yunho.shop.dto.cart.CartItemUpdateRequest;
import com.yunho.shop.dto.cart.CartResponse;
import com.yunho.shop.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 로그인한 회원의 장바구니(SecurityConfig의 anyRequest().authenticated()에 해당)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    // 내 장바구니 조회
    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal Long memberId) {
        return cartService.getCart(memberId);
    }

    // 담기(같은 옵션이면 수량 추가)
    @PostMapping("/items")
    public CartItemResponse addItem(@AuthenticationPrincipal Long memberId,
                                    @Valid @RequestBody CartItemAddRequest request) {
        return cartService.addItem(memberId, request);
    }

    // 수량 변경
    @PostMapping("/items/{cartItemId}")
    public CartItemResponse changeQuantity(@AuthenticationPrincipal Long memberId,
                                           @PathVariable Long cartItemId,
                                           @Valid @RequestBody CartItemUpdateRequest request) {
        return cartService.changeQuantity(memberId, cartItemId, request);
    }

    // 한 개 삭제
    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@AuthenticationPrincipal long memberId,
                           @PathVariable Long cartItemId) {
        cartService.removeItem(memberId, cartItemId);
    }

    // 선택 삭제
    @DeleteMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItems(@AuthenticationPrincipal Long memberId,
                            @RequestParam List<Long> ids) {
        cartService.removeItems(memberId, ids);
    }
}
