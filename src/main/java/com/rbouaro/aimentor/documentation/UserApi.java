package com.rbouaro.aimentor.documentation;

import com.rbouaro.aimentor.dto.chat.ChatMessageRequest;
import com.rbouaro.aimentor.dto.global.AppErrorResponse;
import com.rbouaro.aimentor.dto.global.AppResponse;
import com.rbouaro.aimentor.dto.goal.CreateGoalRequest;
import com.rbouaro.aimentor.dto.goal.UserGoalResponse;
import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.dto.user.UserRegisterRequest;
import com.rbouaro.aimentor.entity.User;
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

@Tag(name = "Users", description = "User registration, profile management, and AI chat")
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
    AppResponse<List<UserGoalResponse>> getCurrentUserGoals(@AuthenticationPrincipal User user);

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
    AppResponse<UserGoalResponse> createGoal(@AuthenticationPrincipal User user, @Valid @RequestBody CreateGoalRequest request);

    @Operation(summary = "Send message", description = "Send a message to the AI mentor and receive a response.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response from the AI mentor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Message is empty",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "AI processing error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<String> chat(@AuthenticationPrincipal User user, @Valid @RequestBody ChatMessageRequest request);

    @Operation(summary = "Health check", description = "Check if the AI mentor service is up and ready.")
    @ApiResponse(responseCode = "200", description = "Service is healthy",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppResponse.class)))
    @SecurityRequirements
    @GetMapping(value = "/chat/health", produces = MediaType.APPLICATION_JSON_VALUE)
    AppResponse<String> chatHealth();
}