package io.getarrays.userservice.service;

import io.getarrays.userservice.model.User;

public interface UserService {
    User saveUser(User user);
    Boolean verifyToken(String token);

}
