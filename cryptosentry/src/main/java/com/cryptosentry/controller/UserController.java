package com.cryptosentry.controller;

import com.cryptosentry.entity.User;
import com.cryptosentry.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public User create(@RequestBody User user) {
        return service.create(user);
    }
    @GetMapping("/test")
    public String test() {
        return "ok";
    }
}