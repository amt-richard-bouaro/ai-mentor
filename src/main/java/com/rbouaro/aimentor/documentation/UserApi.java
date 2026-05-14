package com.rbouaro.aimentor.documentation;

import com.rbouaro.aimentor.dto.global.AppErrorResponse;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.dto.goal.CreateGoalRequest;
import com.rbouaro.aimentor.dto.user.UserRegisterRequest;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.entity.UserGoal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "User registration and profile management")
public interface UserApi {

    @Operation(summary = "Register", description = "Create a new user account. Returns the user profile and sets an authentication cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Username or email already taken",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @SecurityRequirements
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    AppResponse<UserProfile> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest, HttpServletResponse response);

    @Operation(summary = "Get current user", description = "Returns the profile of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<UserProfile> getCurrentUser(@AuthenticationPrincipal User user);

    @Operation(summary = "Get current user's goals", description = "Returns the list of learning goals for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goals retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @GetMapping(value = "/me/goals", produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<List<UserGoal>> getCurrentUserGoals(@AuthenticationPrincipal User user);

    @Operation(summary = "Create goal", description = "Creates a new learning goal for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "400", description = "Title is required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class)))
    })
    @PostMapping(value = "/goals", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<UserGoal> createGoal(@AuthenticationPrincipal User user, @Valid @RequestBody CreateGoalRequest request);
}