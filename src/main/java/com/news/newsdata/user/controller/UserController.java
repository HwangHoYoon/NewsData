package com.news.newsdata.user.controller;

import com.news.newsdata.common.dto.UserDetailsImpl;
import com.news.newsdata.user.request.UserLogoutReq;
import com.news.newsdata.user.response.UserInfoRes;
import com.news.newsdata.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 API")
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 정보 조회", description = "유저 정보 조회")
    @GetMapping("/getUserInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    }
    )
    public UserInfoRes getUserInfo(Authentication authentication) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl)authentication.getPrincipal();
        if (userDetailsImpl == null || userDetailsImpl.getUser() == null) {
            log.error("User details not found in authentication");
            return null;
        }
        Long userId = userDetailsImpl.getUser().getId();
        return userService.selectUserInfoForUserId(userId);
    }

    @Operation(summary = "로그아웃", description = "로그아웃")
    @PostMapping("/logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
    }
    )
    public ResponseEntity<?> logout(@RequestBody UserLogoutReq userLogoutReq,
                                    @Parameter(hidden = true) HttpServletRequest request,
                                    @Parameter(hidden = true) HttpServletResponse response
    ) {
        return userService.logout(request, response, userLogoutReq);
    }
}
