package com.dailycodework.buynowdotcom.service.order;

import com.dailycodework.buynowdotcom.dtos.OrderDTO;

import java.util.List;

public interface IOrderService {

    OrderDTO placeOrder();
    List<OrderDTO> getUserOrder();
}
