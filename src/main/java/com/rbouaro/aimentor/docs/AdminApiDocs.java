package com.rbouaro.aimentor.docs;

import com.rbouaro.aimentor.dto.global.AppErrorResponse;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.global.PaginatedResponse;
import com.rbouaro.aimentor.dto.user.UserProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(
        name = "Admin Functions API",
        description = "Admin operations and functions on users and system"
)
@RequestMapping("/api/v1/admin")
public interface AdminApiDocs {

    // ===================== GET ALL SYSTEM USERS =====================
    @Operation(
            summary = "List all users with pagination and filtering (Admin only)",
            description = "Returns a list of all system users"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(
                    schema = @Schema(implementation = AppResponse.class),
                    examples = @ExampleObject(
                            name = "Users List",
                            value = """
                                    {
                                      "success": true,
                                      "message": "Users retrieved",
                                      "data": [
                                        {
                                          "id": 50480cdf-69c0-416f-b0ce-d77c218a3fe5,
                                          "username": "username",
                                          "email": "username@example.com"
                                        },
                                        {
                                          "id": 50480cdf-69c0-416f-b0ce-d77c218a3fe5,
                                          "username": "username",
                                          "email": "username@example.com"
                                        }
                                      ],
                                      "pageNumber": 0,
                                      "pageSize": 10,
                                      "totalElements": 2
                                      "totalPages": 1,
                                      "last": true
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
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(
                    schema = @Schema(implementation = AppErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Forbidden",
                            value = """
                                    {
                                      "success": false,
                                      "message": "Access denied"
                                    }
                                    """
                    )
            )
    )
    @GetMapping("/users")
    AppResponse<PaginatedResponse<UserProfile>> getAllUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );

    // ===================== GET SINGLE USER BY ID =====================



}
