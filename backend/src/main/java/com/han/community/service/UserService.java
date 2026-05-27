package com.han.community.service;

import com.han.community.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    protected UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
