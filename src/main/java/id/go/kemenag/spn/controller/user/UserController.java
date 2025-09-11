package id.go.kemenag.spn.controller.user;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.dto.user.request.CreateUserRequest;
import id.go.kemenag.spn.dto.user.response.UserResponse;
import id.go.kemenag.spn.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/user")
@Tag(name = "User", description = "User API")
public class UserController {

    final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(AuthConstant.ROLE_ADMIN)
    @Operation(summary = "test documentation summary", description = "test documentation description")
    UserResponse createUser(@RequestBody @Valid CreateUserRequest request) {
        return this.userService.createUser(request);
    }

    @GetMapping("check-availability")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(AuthConstant.ROLE_ADMIN)
    @Operation(summary = "check username availability", description = "check if a username is available" )
    Boolean checkUsernameAvailability(@RequestParam String username) {
        return this.userService.checkUsernameAvailability(username);
    }
}
