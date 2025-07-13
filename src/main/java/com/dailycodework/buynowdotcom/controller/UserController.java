package com.dailycodework.buynowdotcom.controller;

import com.dailycodework.buynowdotcom.dtos.UserDTO;
import com.dailycodework.buynowdotcom.request.UserRequest;
import com.dailycodework.buynowdotcom.response.ApiResponse;
import com.dailycodework.buynowdotcom.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@CrossOrigin(value = "http://localhost:5100")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {

    private final IUserService userService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> allUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity
                .ok()
                .body(new ApiResponse(users, false));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(user, false));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> saveUser(@RequestBody UserRequest request) {
        UserDTO user = userService.createUser(request);
        return ResponseEntity
                .created(URI.create("/api/v1/users/" + user.getId()))
                .body(new ApiResponse(user, false));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        UserDTO user = userService.updateUser(request, id);
        return ResponseEntity
                .accepted()
                .body(new ApiResponse(user, false));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity
                .ok()
                .body(new ApiResponse("User deleted!", false));
    }
}
