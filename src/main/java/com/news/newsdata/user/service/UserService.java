package com.news.newsdata.user.service;

import com.news.newsdata.common.code.MessageCmmCode;
import com.news.newsdata.common.exception.CommonException;
import com.news.newsdata.common.exception.ExceptionCode;
import com.news.newsdata.common.service.JwtProviderService;
import com.news.newsdata.common.service.LogService;
import com.news.newsdata.token.dto.TokenDto;
import com.news.newsdata.token.entity.Token;
import com.news.newsdata.token.service.TokenService;
import com.news.newsdata.user.entity.UserInfo;
import com.news.newsdata.user.repository.UserRepository;
import com.news.newsdata.user.request.UserLogoutReq;
import com.news.newsdata.user.response.LoginRes;
import com.news.newsdata.user.response.UserInfoRes;
import com.news.newsdata.user.response.UserLoginRes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final JwtProviderService jwtProviderService;

    private final LogService logService;

    private final TokenService tokenService;

    public LoginRes loginGoogle(com.news.newsdata.oauth2.dto.UserInfo oauthUserInfo, HttpServletResponse response) {
        String snsId = oauthUserInfo.getId();
        String email = oauthUserInfo.getEmail();
        String nickname = oauthUserInfo.getNickname();
        Integer snsType = oauthUserInfo.getOAuthProvider().getCode();
        String thumbnailImageUrl = oauthUserInfo.getThumbnailImageUrl();
        String profileImageUrl = oauthUserInfo.getProfileImageUrl();

        Optional<UserInfo> userInfo = userRepository.findBySnsTypeAndSnsId(snsType, snsId);

        // 유저 정보가 있다면 업데이트 없으면 등록
        if (userInfo.isPresent()) {
            UserInfo userInfoInfoRst = userInfo.get();

            Long userId = userInfoInfoRst.getId();

            String strUserId = String.valueOf(userId);

            // 로그인 할때마다 토큰 새로 발급(갱신)
            TokenDto tokenDto = jwtProviderService.createAllToken(strUserId);

            // response 헤더에 Access Token / Refresh Token 넣음
            String uuid = setResponseNmtoken(response, tokenDto);

            UserLoginRes userLoginRes = UserLoginRes.builder()
                    .snsId(userInfoInfoRst.getSnsId())
                    .id(userId)
                    .email(userInfoInfoRst.getEmail())
                    .name(userInfoInfoRst.getName())
                    .snsType(userInfoInfoRst.getSnsType())
                    .thumbnailImage(userInfoInfoRst.getThumbnailImage())
                    .profileImage(userInfoInfoRst.getProfileImage())
                    .regDate(userInfoInfoRst.getRegDate())
                    .updDate(userInfoInfoRst.getUpdDate())
                    .build();
            log.info("기존유저 {}, {}",userLoginRes.getId(), userLoginRes.getName());
            // API 로그 적재
            logService.loginUserLogSave(userLoginRes.getId(), "기존유저 " + userLoginRes.getId() + "," + userLoginRes.getName());
            return new LoginRes(HttpStatus.OK.value(), uuid, userLoginRes);
        } else {
            UserInfo user = UserInfo.builder()
                    .snsId(snsId)
                    .email(email)
                    .name(nickname)
                    .snsType(snsType)
                    .thumbnailImage(thumbnailImageUrl)
                    .profileImage(profileImageUrl)
                    .regDate(new Date())
                    .build();
            UserInfo userInfoResult = userRepository.save(user);
            log.info("신규유저 {}, {}", userInfoResult.getId(), userInfoResult.getName());

            Long userId = userInfoResult.getId();

            String strUserId = String.valueOf(userId);

            // 로그인 할때마다 토큰 새로 발급(갱신)
            TokenDto tokenDto = jwtProviderService.createAllToken(strUserId);

            // response 헤더에 Access Token / Refresh Token 넣음
            String uuid = setResponseNmtoken(response, tokenDto);

            // API 로그 적재
            logService.newUserLogSave(userInfoResult.getId(), "신규유저 " + userInfoResult.getId() + userInfoResult.getName());
            return new LoginRes(Integer.parseInt(MessageCmmCode.NEW_USER.getCode()), uuid, null);
        }
    }

    public String setResponseNmtoken(HttpServletResponse response, TokenDto tokenDto) {
        String userId = tokenDto.getUserId();
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        // 리다이렉트일경우 헤더 전송안됨
        String uuid = UUID.randomUUID().toString();

        // redis refreshToken 저장
        Token newToken = new Token(refreshToken,  userId, accessToken);
        tokenService.saveHeader(uuid, newToken);

        return uuid;
    }

    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, UserLogoutReq userLogoutReq) {
        // 로그아웃은 무조건 성공
        try {
            jwtProviderService.deleteAllToken(request, response);
        } catch (Exception e) {
            log.error("로그아웃 에러 발생 {}", e.getMessage());
        }
        return ResponseEntity.ok(MessageCmmCode.OK.getMessage());
    }

    public UserInfoRes selectUserInfoForUserId(Long userId) {
        Optional<UserInfo> usersInfo = userRepository.findById(userId);
        if (usersInfo.isEmpty()) {
            throw new CommonException(ExceptionCode.USER_NULL.getMessage(), ExceptionCode.USER_NULL.getCode());
        }

        UserInfo userInfo = usersInfo.get();

        return UserInfoRes.builder()
                .id(userInfo.getId())
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .thumbnailImage(userInfo.getThumbnailImage())
                .profileImage(userInfo.getProfileImage())
                .regDate(userInfo.getRegDate())
                .updDate(userInfo.getUpdDate())
                .build();
    }
}
