package com.rbouaro.aimentor.docs;

import com.rbouaro.aimentor.dto.global.AppErrorResponse;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.dto.user.UserRegisterRequest;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.entity.UserGoal;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(
        name = "User API",
        description = "User registration, profile management, and personal goals"
)
@RequestMapping("/api/v1/users")
public interface UserApiDocs {

    // ===================== REGISTER =====================

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and sets authentication cookies/tokens"
    )
    @ApiResponse(
            responseCode = "201",
            description = "User successfully registered",
            content = @Content(
                    schema = @Schema(implementation = AppResponse.class),
                    examples = @ExampleObject(
                            name = "Success",
                            value = """
                                    {
                                      "success": true,
                                      "message": "User registered successfully",
                                      "data": {
                                        "id": 50480cdf-69c0-416f-b0ce-d77c218a3fe5,
                                        "username": "username",
                                        "email": "username@example.com"
                                      }
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid registration data",
            content = @Content(
                    schema = @Schema(implementation = AppErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Validation Error",
                            value = """
                                    {
                                      "success": false,
                                      "message": "Validation failed",
                                      "errors": {
                                        "email": "Email is invalid",
                                        "password": "Password must be at least 8 characters"
                                      }
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/register")
    AppResponse<UserProfile> register(
            @Valid @RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Register Request",
                                    value = """
                                            {
                                              "username": "username",
                                              "email": "username@example.com",
                                              "password": "StrongPassword123"
                                            }
                                            """
                            )
                    ))
                    UserRegisterRequest userRegisterRequest,
            HttpServletResponse response
    );

    // ===================== CURRENT USER =====================

    @Operation(
            summary = "Get current authenticated user profile",
            description = "Returns profile details of the currently logged-in user"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "User profile retrieved successfully",
            content = @Content(
                    schema = @Schema(implementation = AppResponse.class),
                    examples = @ExampleObject(
                            name = "Profile Response",
                            value = """
                                    {
                                      "success": true,
                                      "message": "User profile retrieved",
                                      "data": {
                                        "id": 50480cdf-69c0-416f-b0ce-d77c218a3fe5,
                                        "username": "username",
                                        "email": "username@example.com"
                                      }
                                    }
                                    """
                    )
            )
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
    @GetMapping("/me")
    AppResponse<UserProfile> getCurrentUser(
            @AuthenticationPrincipal User user
    );

    // ===================== USER GOALS =====================

    @Operation(
            summary = "Get current user's goals",
            description = "Returns all goals associated with the authenticated user"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Goals retrieved successfully",
            content = @Content(
                    examples = @ExampleObject(
                            name = "Goals List",
                            value = """
                                    {
                                      "success": true,
                                      "message": "Goals retrieved",
                                      "data": [
                                        {
                                          "id": 50480cdf-69c0-416f-b0ce-d77c218a3fe5,
                                          "title": "Learn Spring Boot",
                                          "description": "Build a REST API with Swagger"
                                        }
                                      ]
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = AppErrorResponse.class))
    )
    @GetMapping("/me/goals")
    AppResponse<List<UserGoal>> getCurrentUserGoals(
            @AuthenticationPrincipal User user
    );

    @Operation(
            summary = "Create a new user goal",
            description = "Creates a new goal for the authenticated user"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Goal created successfully",
            content = @Content(
                    examples = @ExampleObject(
                            name = "Goal Created",
                            value = """
                                    {
                                      "id": 50480cdf-69c0-416f-b0ce-d77c218a3,
                                      "title": "Finish AI Mentor",
                                      "description": "Complete backend APIs"
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid goal data",
            content = @Content(
                    schema = @Schema(implementation = AppErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Bad Request",
                            value = """
                                    {
                                      "success": false,
                                      "message": "Title must not be empty"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/me/goals")
    ResponseEntity<UserGoal> createGoal(
            @AuthenticationPrincipal User user,
            @RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Create Goal Request",
                                    value = """
                                            {
                                              "title": "Learn Docker",
                                              "description": "Containerize Spring Boot app"
                                            }
                                            """
                            )
                    )
            )
            Map<String, String> request
    );
}
