package com.dailycodework.buynowdotcom.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private CartDTO cart;
    private List<OrderDTO> orders;
}
