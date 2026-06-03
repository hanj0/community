package com.han.community;


import com.han.community.entity.Channel;
import com.han.community.entity.Role;
import com.han.community.entity.User;
import com.han.community.repository.ChannelRepository;
import com.han.community.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChannelRepository channelRepository;

    @PostConstruct
    public void init() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(
                    new User(
                            "admin",
                            "admin",
                            passwordEncoder.encode("1234"),
                            Role.ADMIN
                    )
            );
        }
    }
}
