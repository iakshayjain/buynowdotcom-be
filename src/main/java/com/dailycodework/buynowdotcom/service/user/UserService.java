package com.dailycodework.buynowdotcom.service.user;

import com.dailycodework.buynowdotcom.dtos.UserDTO;
import com.dailycodework.buynowdotcom.model.User;
import com.dailycodework.buynowdotcom.repository.UserRepository;
import com.dailycodework.buynowdotcom.request.UserRequest;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param request new user request
     * @return user
     */
    @Override
    public UserDTO createUser(UserRequest request) {
        return Optional.of(request).filter(userRequest -> !userRepository.existsByEmail(request.getEmail()))
                .map(user -> {
                    User newUser = mapper.map(user, User.class);
                    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
                    User savedUser = userRepository.save(newUser);
                    return mapper.map(savedUser, UserDTO.class);
                })
                .orElseThrow(() -> new EntityExistsException("User already exists!"));
    }

    /**
     * @param request update user request
     * @param userId existing user id
     * @return user
     */
    @Override
    public UserDTO updateUser(UserRequest request, Long userId) {
        User user = getUser(userId);
        Optional.ofNullable(request.getFirstName()).ifPresent(user :: setFirstName);
        Optional.ofNullable(request.getLastName()).ifPresent(user :: setLastName);
        Optional.ofNullable(request.getEmail()).ifPresent(user :: setEmail);
        Optional.ofNullable(request.getPassword()).ifPresent(user :: setPassword);
        User updatedUser = userRepository.save(user);
        return mapper.map(updatedUser, UserDTO.class);
    }

    /**
     * @param userId existing user id
     * @return user from db
     */
    @Override
    public UserDTO getUserById(Long userId) {
        User user = getUser(userId);
        return mapper.map(user, UserDTO.class);
    }

    /**
     * @return authenticated user
     */
    @Override
    public UserDTO getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new EntityNotFoundException("Login required to access the protected resources!"));
        return mapper.map(user, UserDTO.class);
    }

    /**
     * @return all users from db
     */
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(user -> mapper.map(user, UserDTO.class)).toList();
    }

    /**
     * @param userId existing user id to be deleted
     */
    @Override
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    }
}
