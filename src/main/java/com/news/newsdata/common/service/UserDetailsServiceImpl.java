package com.news.newsdata.common.service;

import com.news.newsdata.common.dto.UserDetailsImpl;
import com.news.newsdata.user.entity.UserInfo;
import com.news.newsdata.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl {

    private final UserRepository userRepository;

    public UserDetails loadUserByUserId(Long id) throws UsernameNotFoundException {
        UserInfo userInfo = userRepository.findById(id).orElseThrow(() -> {
            log.error("토큰 id정보가 올바르지 않습니다.");
            return null;
            }
        );
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setUser(userInfo);

        return userDetails;
    }
}
