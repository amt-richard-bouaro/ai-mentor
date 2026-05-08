package com.rbouaro.aimentor.docs;

import com.rbouaro.aimentor.dto.global.AppErrorResponse;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.user.LoginRequest;
import com.rbouaro.aimentor.dto.user.UserProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Authentication API",
        description = "User authentication, login, and logout"
)
@RequestMapping("/api/v1/auth")
public interface AuthApiDocs {

    // ===================== LOGIN =====================

    @Operation(
            summary = "User login",
            description = "Authenticates a user and sets authentication cookies or tokens"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                    schema = @Schema(implementation = AppResponse.class),
                    examples = @ExampleObject(
                            name = "Login Success",
                            value = """
                                    {
                                      "success": true,
                                      "message": "Login successful",
                                      "data": {
                                        "id": 50480cdf-69c0-416f-b0ce-d77c218a3fe5,
                                        "username": "linus",
                                        "email": "linus@example.com"
                                      }
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid login credentials",
            content = @Content(
                    schema = @Schema(implementation = AppErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Invalid Credentials",
                            value = """
                                    {
                                      "success": false,
                                      "message": "Invalid username or password"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/login")
    AppResponse<UserProfile> login(
            @Valid
            @RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Login Request",
                                    value = """
                                            {
                                              "username": "linus",
                                              "password": "StrongPassword123"
                                            }
                                            """
                            )
                    )
            )
            LoginRequest loginRequest,
            HttpServletResponse response
    );

    // ===================== LOGOUT =====================

    @Operation(
            summary = "User logout",
            description = "Logs out the current user and clears authentication cookies or tokens"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "204",
            description = "Logout successful (No Content)"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                    schema = @Schema(implementation = AppErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Unauthorized",
                            value = """
                                    {
                                      "success": false,
                                      "message": "Unauthorized access"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/logout")
    void logout(HttpServletResponse response);
}
