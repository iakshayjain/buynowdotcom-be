package com.dailycodework.buynowdotcom.service.order;

import com.dailycodework.buynowdotcom.dtos.OrderDTO;
import com.dailycodework.buynowdotcom.dtos.UserDTO;
import com.dailycodework.buynowdotcom.enums.OrderStatus;
import com.dailycodework.buynowdotcom.model.*;
import com.dailycodework.buynowdotcom.repository.CartRepository;
import com.dailycodework.buynowdotcom.repository.OrderRepository;
import com.dailycodework.buynowdotcom.repository.ProductRepository;
import com.dailycodework.buynowdotcom.service.cart.ICartService;
import com.dailycodework.buynowdotcom.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final ICartService cartService;
    private final ModelMapper mapper;

    /**
     * @return order
     */
    @Override
    public OrderDTO placeOrder() {
        UserDTO userDTO = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(userDTO.getId()).orElseThrow(() -> new EntityNotFoundException("User not found!"));
        Order order = createOrder(cart);
        Set<OrderItem> orderItems = new HashSet<>(createOrderItems(order, cart));
        order.setOrderItems(orderItems);
        order.setTotalAmount(calculateTotalPrice(orderItems));
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(cart.getId());
        return mapper.map(savedOrder, OrderDTO.class);
    }

    /**
     * @return order list
     */
    @Override
    public List<OrderDTO> getUserOrder() {
        UserDTO userDTO = userService.getAuthenticatedUser();
        return orderRepository
                .findByUserId(userDTO.getId())
                .stream()
                .map(order -> mapper.map(order, OrderDTO.class))
                .toList();
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setUser(cart.getUser());
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart
                .getItems()
                .stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    product.setInventory(product.getInventory() - cartItem.getQuantity());
                    productRepository.save(product);
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setPrice(cartItem.getUnitPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setProduct(product);
                    return orderItem;
                }).toList();
    }

    private BigDecimal calculateTotalPrice(Set<OrderItem> orderItems) {
        return orderItems
                .stream()
                .map(orderItem -> orderItem
                        .getPrice()
                        .multiply(new BigDecimal(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
