package kz.epam.newsportal.controller;

import kz.epam.newsportal.exception.*;
import kz.epam.newsportal.model.Role;
import kz.epam.newsportal.model.User;
import kz.epam.newsportal.model.account.*;
import kz.epam.newsportal.service.IUserService;
import kz.epam.newsportal.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Controller
public class UserController {

    private static final String DELETED_MSG = "Deleted successfully";
    private static final String UPDATED_MSG = "Updated successfully";
    private static final String ROLE_USER = "USER_ROLE";

    private IUserService userService;
    private ValidationUtility formUtil;

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setFormUtil(ValidationUtility validationUtility) {
        this.formUtil = validationUtility;
    }

    // Register new user
    @RequestMapping(value="/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> registerAccount(@RequestBody @Valid UserRegisterModel userRegisterModel, UriComponentsBuilder uriBuilder) {
        try {
            formUtil.validateEmail(userRegisterModel.getEmail());
            formUtil.validatePasswordMatching(userRegisterModel.getPassword(), userRegisterModel.getMatchingPassword());

            String userEmail = userRegisterModel.getEmail();
            String userName = userRegisterModel.getUserName();
            String userPassword = userRegisterModel.getPassword();
            Role userRole = new Role(ROLE_USER);
            User user = new User(userEmail, userPassword, userName, true,true,true,true);
            userRole.setUser(user);
            user.addRole(userRole);

            long newUserId = userService.addUser(user);
            URI location = uriBuilder.path("/users/{id}").buildAndExpand(newUserId).toUri();
            return new ResponseEntity<>(location, HttpStatus.CREATED);
        } catch(UserEmailInvalidException | UserPasswordMismatchException badCredentialsException) {
            return new ResponseEntity<>(badCredentialsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(UserAlreadyExistException userExistException) {
            return new ResponseEntity<>(userExistException.getMessage(), HttpStatus.CONFLICT);
        }
    }

    // Get users
    @RequestMapping(value="/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getUsersList() {
        try {
            List<User> users = userService.getUsersList();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch(NotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Get user by id
    @RequestMapping(value="/users/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getUser(@PathVariable long id) {
        try {
            User user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch(NotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Update user
    @RequestMapping(value="/users/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable long id) {
        try {
            userService.updateUser(user, id);
            return new ResponseEntity<>(UPDATED_MSG ,HttpStatus.OK);
        } catch (NotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Delete user
    @RequestMapping(value="/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(DELETED_MSG, HttpStatus.NO_CONTENT);
        } catch (NotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
