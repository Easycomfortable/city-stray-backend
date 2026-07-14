package com.citystray.service;

import com.citystray.entity.User;
import java.util.Map;

public interface UserService {
    Map<String, Object> login(String username, String password);
    Map<String, Object> getUserInfo();
    void logout();
}
