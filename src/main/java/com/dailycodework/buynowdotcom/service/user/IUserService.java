package com.dailycodework.buynowdotcom.service.user;

import com.dailycodework.buynowdotcom.dtos.UserDTO;
import com.dailycodework.buynowdotcom.request.UserRequest;

import java.util.List;

public interface IUserService {

    UserDTO createUser(UserRequest request);
    UserDTO updateUser(UserRequest request, Long userId);
    UserDTO getUserById(Long userId);
    UserDTO getAuthenticatedUser();
    List<UserDTO> getAllUsers();
    void deleteUser(Long userId);

}
