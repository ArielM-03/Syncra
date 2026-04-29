package com.syncra.syncra.service;

import com.syncra.syncra.model.User;
import com.syncra.syncra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByUserEmail(email);
    }

    public User createUser(User user) {
        if (emailExists(user.getUserEmail())) {
            throw new IllegalArgumentException("Email already in use.");
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}