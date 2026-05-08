package com.ghr360.service;

import com.ghr360.dto.response.UserResponse;
import com.ghr360.entity.User;
import com.ghr360.exception.ResourceNotFoundException;
import com.ghr360.repository.UserRepository;
import com.ghr360.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with username: " + username));
        return mapToUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated: {}", user.getUsername());
    }

    // ─── Mapper ─────────────────────────────────────────────────────────────────

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .salutation(user.getSalutation())
                .lat(user.getLat())
                .longitude(user.getLongitude())
                .userType(user.getUserType())
                .isFirstTimeLogin(user.getIsFirstTimeLogin())
                .isActive(user.getIsActive())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .country(user.getCountry())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .alternativeNo(user.getAlternativeNo())
                .resourceCode(user.getResourceCode())
                .build();
    }
    
    public Map<String,String> getUserMap(){
       List<User> users = userRepository.findAll();
       Map<String,String> userMap = new HashMap<>();
       
       for(User user: users) {
    	      String value = user.getSalutation() +" "+user.getFirstname()+" "+user.getLastname();
    	      userMap.put(user.getUsername(),value);
    	      
       }
       
       return userMap;
    }
}
