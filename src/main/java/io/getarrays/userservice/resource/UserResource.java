package io.getarrays.userservice.resource;

import io.getarrays.userservice.model.HttpResponse;
import io.getarrays.userservice.model.User;
import io.getarrays.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<HttpResponse> createUser(@RequestBody User user){
        User newUser= userService.saveUser(user);
            return ResponseEntity.created(URI.create("")).body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("user",newUser))
                            .message("user created")
                            .status(HttpStatus.CREATED)
                            .statusCode(HttpStatus.CREATED.value())
                            .build()
        );
    }

    @GetMapping      // ("/{token}")
    public ResponseEntity<HttpResponse> confirmationUserAccount(@RequestParam("token") String token){
        Boolean isSuccess = userService.verifyToken(token);

        HttpResponse httpResponse=HttpResponse.builder().timeStamp(LocalDateTime.now().toString())
                .data(Map.of("Success",isSuccess))
                .message("Account verified")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value()).build();

        return ResponseEntity.ok(httpResponse);
    }
}
