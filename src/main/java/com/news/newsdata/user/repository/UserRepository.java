package com.news.newsdata.user.repository;


import com.news.newsdata.user.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findBySnsTypeAndSnsId(Integer SnsType, String snsId);

    Optional<UserInfo> findByEmailAndSnsType(String email, Integer SnsType);
}
