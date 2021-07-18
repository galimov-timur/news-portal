package kz.epam.newsportal.controller;

import kz.epam.newsportal.exception.NotFoundException;
import kz.epam.newsportal.model.User;
import kz.epam.newsportal.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Controller
public class UserController {

    private IUserService userService;

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    // Register new user
    @RequestMapping(value="/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> addUser(@RequestBody User user, UriComponentsBuilder uriBuilder) {
        long newUserId = userService.addUser(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriBuilder.path("/users/{id}").buildAndExpand(newUserId).toUri());
        HttpStatus httpStatus = newUserId > 0 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(headers, httpStatus);
    }

    // Get users
    @RequestMapping(value="/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<User>> getUsersList() {
        List<User> users = userService.getUsersList();
        HttpStatus httpStatus = users.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        return new ResponseEntity<>(users, httpStatus);
    }

    // Get user by id
    @RequestMapping(value="/users/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> getUser(@PathVariable long id) {
        User user = userService.getUserById(id);
        HttpStatus httpStatus = (user == null) ? HttpStatus.NOT_FOUND: HttpStatus.OK;
        return new ResponseEntity<>(user, httpStatus);
    }

    // Update user
    @RequestMapping(value="/users/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateUser(@RequestBody User updatedUser, @PathVariable long id) {
        HttpStatus httpStatus = HttpStatus.NO_CONTENT;
        updatedUser.setId(id);
        try {
            this.userService.updateUser(updatedUser);
        } catch(NotFoundException exception) {
            httpStatus = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(httpStatus);
    }

    // Delete user
    @RequestMapping(value="/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        User storedUser = userService.getUserById(id);
        if(storedUser != null) {
            userService.deleteUser(storedUser);
        }
        HttpStatus httpStatus = storedUser == null ? HttpStatus.NOT_FOUND : HttpStatus.NO_CONTENT;
        return new ResponseEntity<>(httpStatus);
    }
}
