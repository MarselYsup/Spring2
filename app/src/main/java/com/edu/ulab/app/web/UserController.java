package com.edu.ulab.app.web;

import com.edu.ulab.app.facade.UserDataFacade;
import com.edu.ulab.app.web.constant.WebConstant;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import static com.edu.ulab.app.web.constant.WebConstant.REQUEST_ID_PATTERN;
import static com.edu.ulab.app.web.constant.WebConstant.RQID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = WebConstant.VERSION_URL + "/user",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User Controller",
        description = "CRUD operation with User")
public class UserController {
    private final UserDataFacade userDataFacade;

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create user",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema =
                                    @Schema(implementation = UserBookResponse.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "400",
                    description = "User should not be null",
                    content = @Content)

    })
    public UserBookResponse createUserWithBooks(@Valid @RequestBody UserBookRequest request,
                                                @RequestHeader(RQID) @Pattern(regexp = REQUEST_ID_PATTERN) final String requestId) {
        UserBookResponse response = userDataFacade.createUserWithBooks(request);
        log.info("Response with created user and his books: {}", response);
        return response;
    }

    @PutMapping(value = "/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update user",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema =
                                    @Schema(implementation = UserBookResponse.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "404",
                    description = "User id was not found",
                    content = @Content)

    })
    public UserBookResponse updateUserWithBooks(@RequestBody @Valid UserBookRequest request, @PathVariable Long userId) {
        UserBookResponse response = userDataFacade.updateUserWithBooks(request, userId);
        log.info("Response with updated user and his books: {}", response);
        return response;
    }

    @GetMapping(value = "/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get user with books",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema =
                                    @Schema(implementation = UserBookResponse.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "404",
                    description = "User was not found",
                    content = @Content)

    })
    public UserBookResponse updateUserWithBooks(@PathVariable  Long userId) {
        UserBookResponse response = userDataFacade.getUserWithBooks(userId);
        log.info("Response with user and his books: {}", response);
        return response;
    }

    @DeleteMapping(value = "/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was deleted",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema =
                                    @Schema(implementation = UserBookResponse.class)
                            )
                    }
            )
    })
    public void deleteUserWithBooks(@PathVariable Long userId) {
        log.info("Delete user and his books:  userId {}", userId);
        userDataFacade.deleteUserWithBooks(userId);
    }
}
