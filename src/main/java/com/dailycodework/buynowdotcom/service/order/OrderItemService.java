package com.dailycodework.buynowdotcom.service.order;

import com.dailycodework.buynowdotcom.dtos.OrderItemDTO;
import com.dailycodework.buynowdotcom.model.Order;
import com.dailycodework.buynowdotcom.model.OrderItem;
import com.dailycodework.buynowdotcom.model.Product;
import com.dailycodework.buynowdotcom.repository.OrderItemRepository;
import com.dailycodework.buynowdotcom.repository.OrderRepository;
import com.dailycodework.buynowdotcom.repository.ProductRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderItemService implements IOrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper mapper;

    /**
     * @param orderId existing order id
     * @param productId existing product id
     * @return order item
     */
    @Override
    public OrderItemDTO getItemByOrderIdAndProductId(Long orderId, Long productId) {
        Set<OrderItem> orderItems = orderRepository
                .findById(orderId)
                .map(Order::getOrderItems).orElseThrow(() -> new NoResultException("Order is empty"));
        return orderItems
                .stream()
                .filter(orderItem -> orderItem.getProduct().getId().equals(productId))
                .findFirst()
                .map(orderItem -> mapper.map(orderItem, OrderItemDTO.class))
                .orElseThrow(() -> new EntityNotFoundException("Item with id: " + productId + "not exist inside the order"));
    }

    /**
     * @param orderId existing order id
     * @return all items inside the order
     */
    @Override
    public List<OrderItemDTO> getAllItemsByOrderId(Long orderId) {
        Set<OrderItem> orderItems = orderRepository
                .findById(orderId)
                .map(Order::getOrderItems)
                .orElseThrow(() -> new NoResultException("Order is empty"));
        return orderItems
                .stream()
                .map(orderItem -> mapper.map(orderItem, OrderItemDTO.class))
                .toList();
    }

    /**
     * @param orderId existing order id
     * @param productId existing product id
     * @return order item
     */
    @Override
    public OrderItemDTO addItemToOrder(Long orderId, Long productId, Integer quantity) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        return order.getOrderItems().stream().filter(orderItem -> !orderItem.getProduct().getId().equals(productId))
                .map(orderItem -> {
                    OrderItem newOrderItem = new OrderItem();
                    Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found!"));
                    newOrderItem.setOrder(order);
                    newOrderItem.setQuantity(quantity);
                    newOrderItem.setProduct(product);
                    newOrderItem.setPrice();
                    order.getOrderItems().add(newOrderItem);
                    orderRepository.save(order);
                    OrderItem saveOrderItem = orderItemRepository.save(newOrderItem);
                    return mapper.map(saveOrderItem, OrderItemDTO.class);
                }).findFirst().orElseThrow(() -> new EntityExistsException("Item already exist inside the order"));
    }

    /**
     * @param orderId existing order id
     * @param productId existing product id
     */
    @Override
    public void deleteItemFromOrder(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found!"));
        order.getOrderItems().stream().filter(orderItem -> orderItem.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresentOrElse(orderItem -> {
                    order.getOrderItems().remove(orderItem);
                    orderItemRepository.delete(orderItem);
                    orderRepository.save(order);
                }, () -> {
                    throw new EntityNotFoundException("Item not found inside the order!");
                });
    }

    /**
     * @param orderId existing order id
     * @param productId existing product id
     * @param quantity quantity updated
     * @return updated order item
     */
    @Override
    public OrderItemDTO updateOrderItem(Long orderId, Long productId, Integer quantity) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        return order.getOrderItems().stream().filter(orderItem -> orderItem.getProduct().getId().equals(productId))
                .findFirst()
                .map(orderItem -> {
                    orderItem.setQuantity(quantity);
                    OrderItem saveOrderItem = orderItemRepository.save(orderItem);
                    return mapper.map(saveOrderItem, OrderItemDTO.class);
                }).orElseThrow(() -> new EntityExistsException("Item not found inside the order!"));
    }
}
