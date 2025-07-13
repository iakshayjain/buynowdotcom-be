package com.dailycodework.buynowdotcom.service.order;

import com.dailycodework.buynowdotcom.dtos.OrderItemDTO;

import java.util.List;

public interface IOrderItemService {

    OrderItemDTO getItemByOrderIdAndProductId(Long orderId, Long productId);
    List<OrderItemDTO> getAllItemsByOrderId(Long orderId);
    OrderItemDTO addItemToOrder(Long orderId, Long productId, Integer quantity);
    void deleteItemFromOrder(Long orderId, Long productId);
    OrderItemDTO updateOrderItem(Long orderId, Long productId, Integer quantity);
}
