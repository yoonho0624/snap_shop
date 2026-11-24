package com.yunho.shop.service.cart;

import com.yunho.shop.dto.cart.CartItemAddRequest;
import com.yunho.shop.dto.cart.CartItemResponse;
import com.yunho.shop.dto.cart.CartItemUpdateRequest;
import com.yunho.shop.dto.cart.CartResponse;
import com.yunho.shop.entity.order.CartItem;
import com.yunho.shop.entity.product.Product;
import com.yunho.shop.entity.product.ProductOption;
import com.yunho.shop.entity.product.SaleStatus;
import com.yunho.shop.global.exception.BusinessException;
import com.yunho.shop.global.exception.ErrorCode;
import com.yunho.shop.repository.cart.CartItemRepository;
import com.yunho.shop.repository.member.MemberRepository;
import com.yunho.shop.repository.product.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
    private static final int MAX_QUANTITY = 99;

    private final CartItemRepository cartItemRepository;
    private final ProductOptionRepository productOptionRepository;
    private final MemberRepository memberRepository;

    // 내 장바구니
    public CartResponse getCart(Long memberId) {
        List<CartItemResponse> items = cartItemRepository.findAllWithProductByMember_MemberId(memberId)
                .stream()
                .map(CartItemResponse::from)
                .toList();
        return CartResponse.from(items);
    }

    // 담기 : 같은 옵션이 있으면 수량을 더하고, 없으면 새로 만든다
    @Transactional
    public CartItemResponse addItem(Long memberId, CartItemAddRequest request) {
        ProductOption option = productOptionRepository.findWithProductByProductOptionId(request.productOptionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_OPTION_NOT_FOUND));
        validateOnSale(option.getProduct());

        CartItem cartItem = cartItemRepository
                .findByMember_MemberIdAndProductOption_ProductOptionId(memberId, option.getProductOptionId())
                .map(existing -> {
                    validateMaxQuantity(existing.getQuantity() + request.quantity());
                    existing.addQuantity(request.quantity());
                    return existing;
                })
                .orElseGet(() -> cartItemRepository.save(
                        CartItem.builder()
                                .member(memberRepository.getReferenceById(memberId))
                                .productOption(option)
                                .quantity(request.quantity())
                                .build()
                ));

        return CartItemResponse.from(cartItem);
    }

    // 수량 변경
    @Transactional
    public CartItemResponse changeQuantity(Long memberId, Long cartItemId, CartItemUpdateRequest request) {
        CartItem cartItem = findMyCartItem(memberId, cartItemId);
        cartItem.changeQuantity(request.quantity());
        return CartItemResponse.from(cartItem);
    }

    // 한 개 삭제
    @Transactional
    public void removeItem(Long memberId, Long cartItemId) {
        CartItem cartItem = findMyCartItem(memberId, cartItemId);
        cartItemRepository.delete(cartItem);
    }

    // 선택 삭제
    @Transactional
    public void removeItems(Long memberId, List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return;
        }
        cartItemRepository.deleteAllByCartItemIdAndMember_MemberId(new HashSet<>(cartItemIds), memberId);
    }

    // 내부 메서드
    private CartItem findMyCartItem(Long memberId, Long cartItemId) {
        return cartItemRepository.findByCartItemIdAndMember_MemberId(cartItemId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
    }

    private void validateOnSale(Product product) {
        if (product.isDeleted() || product.getSaleStatus() != SaleStatus.ON_SALE) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }
    }

    private void validateMaxQuantity(int quantity) {
        if (quantity > MAX_QUANTITY) {
            throw new BusinessException(ErrorCode.CART_QUANTITY_EXCEEDED);
        }
    }
}
