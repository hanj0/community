package com.han.community.service;

import com.han.community.dto.AuthDto;
import com.han.community.entity.Role;
import com.han.community.entity.User;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public User signup(AuthDto.SignupRequest request) {

        if(userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    public Authentication login(AuthDto.LoginRequest request) {

        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BusinessException(ErrorCode.INVALID_LOGIN);
        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }
    }

    // todo: 일정기간 많은 요청 시 요청제한해야함
    //  다른 세션/리프레시 토큰 무효화
    //  변경 완료 이메일 알림
    @Transactional
    public void changePassword(Long userId, AuthDto.ChangePasswordRequest requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(requestDto.currentPassword(), user.getPassword()))
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);

        if(passwordEncoder.matches(requestDto.newPassword(), user.getPassword()))
            throw new BusinessException(ErrorCode.SAME_AS_CURRENT_PASSWORD);

        userRepository.updatePassword(userId, passwordEncoder.encode(requestDto.newPassword()));
    }

}
