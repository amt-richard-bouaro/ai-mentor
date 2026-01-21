package com.rbouaro.aimentor.controller;

import com.rbouaro.aimentor.docs.AdminApiDocs;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.global.PaginatedResponse;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController implements AdminApiDocs {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public AppResponse<PaginatedResponse<UserProfile>> getAllUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Admin request: list users. Search: '{}', Page: {}, Size: {}", search, page, size);
        return userService.getAllUsers(search, page, size);
    }
}
