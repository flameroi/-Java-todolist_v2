package org.example.todolistv2.controllers;

import org.example.todolistv2.entity.User;
import org.example.todolistv2.exceptions.NotFoundObjectException;
import org.example.todolistv2.exceptions.NotFoundOwnerException;
import org.example.todolistv2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class UserController {
    @Autowired
    private UserService userServices;

    @RequestMapping(method = GET, value = "/users")
    @ResponseBody
    public List<User> userList() {
        return userServices.found();
    }

    @RequestMapping(method = GET, value = "/users/{userId}")
    @ResponseBody
    public User userInfo(@PathVariable String userId) {
        return userServices.getInfo(userId);
    }


    @RequestMapping(method = POST, value = "/users")
    @ResponseBody
    public ResponseEntity<?> userCreator(@RequestBody User user) {
        userServices.create(user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = PUT, value = "/users/{userId}")
    @ResponseBody
    public ResponseEntity<?> userUpdate(@RequestBody User uploadUser,
                           @PathVariable String userId) {
        userServices.update(userId, uploadUser);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = DELETE, value = "/users/{userId}")
    @ResponseBody
    public ResponseEntity<String> userDelete(@PathVariable String userId) {
         userServices.remove(userId);
         return ResponseEntity.ok().build();
    }

    @ExceptionHandler
    public ResponseEntity<String> exceptionHandler(Exception exception) {
        if (exception instanceof NotFoundObjectException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (exception instanceof NotFoundOwnerException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
