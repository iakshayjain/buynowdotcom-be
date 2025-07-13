package com.dailycodework.buynowdotcom.service.cart;

import com.dailycodework.buynowdotcom.dtos.CartDTO;
import com.dailycodework.buynowdotcom.dtos.CartItemDTO;
import com.dailycodework.buynowdotcom.dtos.UserDTO;
import com.dailycodework.buynowdotcom.model.Cart;
import com.dailycodework.buynowdotcom.model.CartItem;
import com.dailycodework.buynowdotcom.model.Product;
import com.dailycodework.buynowdotcom.repository.CartItemRepository;
import com.dailycodework.buynowdotcom.repository.CartRepository;
import com.dailycodework.buynowdotcom.repository.ProductRepository;
import com.dailycodework.buynowdotcom.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final ModelMapper mapper;
    private final CartService cartService;
    private final UserService userService;

    /**
     * @param productId existing product id
     * @param quantity no. of quantity to be added
     */
    @Override
    public CartItemDTO addItemToCart(Long productId, int quantity) {
        var userId = userService.getAuthenticatedUser().getId();
        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if(cart == null) {
            var cartDTO = cartService.initializeNewCartForUser(userId);
            cart = mapper.map(cartDTO, Cart.class);
        }
        
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found!"));
        CartItem cartItem = cart
                .getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(new CartItem());

        if (cartItem.getId() == null) {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartItem.calculateAndSetUnitPrice();
        cartItem.calculateAndSetTotalPrice();
        CartItem savedCartItem = cartItemRepository.save(cartItem);
        cart.addItem(savedCartItem);
        cartRepository.save(cart);
        return mapper.map(savedCartItem, CartItemDTO.class);
    }

    /**
     * @param productId existing product id
     * @param quantity no. of quantity to be added
     */
    @Override
    public CartItemDTO updateItemToCart(Long productId, int quantity) {
        UserDTO user = userService.getAuthenticatedUser();
        CartDTO cartDTO = user.getCart();
        CartItem cartItem = getCartItemByCartIdAndProductId(cartDTO.getId(), productId);
        cartItem.setQuantity(quantity);
        cartItem.calculateAndSetUnitPrice();
        cartItem.calculateAndSetTotalPrice();

        cartItemRepository.save(cartItem);

        Cart cart = cartService.getCartByCartId(cartDTO.getId());
        cart.updateTotalAmount();
        cartRepository.save(cart);
        return mapper.map(cartItem, CartItemDTO.class);
    }

    /**
     * @param productId existing product id
     */
    @Override
    public void removeItemFromCart(Long productId) {
        UserDTO userDTO = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(userDTO.getId()).orElseThrow(() -> new EntityNotFoundException("cart not exist!"));
        CartItem cartItem = getCartItemByCartIdAndProductId(cart.getId(), productId);
        cart.removeItem(cartItem);
        cartRepository.save(cart);
        cartItemRepository.delete(cartItem);
    }

    /**
     * @param productId existing product id
     * @return cart item
     */
    @Override
    public CartItemDTO getCartItem(Long productId) {
        UserDTO userDTO = userService.getAuthenticatedUser();
        CartDTO cartDTO = userDTO.getCart();
        CartItem cartItem = getCartItemByCartIdAndProductId(cartDTO.getId(), productId);
        return mapper.map(cartItem, CartItemDTO.class);
    }

    /**
     * @return all cart items
     */
    @Override
    public Set<CartItemDTO> getCartItems() {
        UserDTO userDTO = userService.getAuthenticatedUser();
        CartDTO cartDTO = userDTO.getCart();
        Set<CartItem> cartItems = cartService.getCartByCartId(cartDTO.getId()).getItems();
        return cartItems.stream().map(cartItem -> mapper.map(cartItem, CartItemDTO.class)).collect(Collectors.toSet());
    }

    private CartItem getCartItemByCartIdAndProductId(Long cartId, Long productId) {
        Cart cart = cartService.getCartByCartId(cartId);
        return cart
                .getItems()
                .stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Product id: " + productId + " not found inside the cart"));
    }
}
