package kz.epam.newsportal.controller;

import kz.epam.newsportal.exception.UserAlreadyExistException;
import kz.epam.newsportal.exception.UserEmailInvalidException;
import kz.epam.newsportal.exception.UserPasswordMismatchException;
import kz.epam.newsportal.model.JwtToken;
import kz.epam.newsportal.model.Role;
import kz.epam.newsportal.model.User;
import kz.epam.newsportal.model.account.LoginRequestModel;
import kz.epam.newsportal.model.account.UserRegisterModel;
import kz.epam.newsportal.service.IUserService;
import kz.epam.newsportal.util.JwtUtil;
import kz.epam.newsportal.util.ValidationUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@Controller
public class AccountController {

    private IUserService userService;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private ValidationUtility formUtil;

    private static final String ROLE_USER = "USER_ROLE";
    private static final String EMPTY_STRING = "";

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setFormUtil(ValidationUtility validationUtility) {
        this.formUtil = validationUtility;
    }

    @RequestMapping(value="/authenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> createJwtToken(@RequestBody LoginRequestModel loginRequestModel) {
        String userEmail = loginRequestModel.getEmail();
        String userPassword = loginRequestModel.getPassword();
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userEmail, userPassword);
            authenticationManager.authenticate(authToken);
            User userDetails = (User) userService.loadUserByUsername(loginRequestModel.getEmail());
            String token = jwtUtil.generateToken(userDetails);
            JwtToken jwtToken = new JwtToken(token);
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Register new user
    @RequestMapping(value="/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> registerAccount(@RequestBody @Valid UserRegisterModel userRegisterModel, UriComponentsBuilder uriBuilder) {
        HttpStatus httpStatus;
        String message = EMPTY_STRING;

        try {
            formUtil.validateEmail(userRegisterModel.getEmail());
            formUtil.validatePasswordMatching(userRegisterModel.getPassword(), userRegisterModel.getMatchingPassword());

            String userEmail = userRegisterModel.getEmail();
            String userName = userRegisterModel.getUserName();
            String userPassword = userRegisterModel.getPassword();
            Role userRole = new Role(ROLE_USER);
            User user = new User(userEmail, userPassword, userName, true,true,true,true);
            user.addRole(userRole);

            long newUserId = userService.addUser(user);
            httpStatus = newUserId > 0 ? HttpStatus.CREATED: HttpStatus.CONFLICT;

        } catch(UserEmailInvalidException | UserPasswordMismatchException badCredentialsException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            message = badCredentialsException.getMessage();
        }

        return new ResponseEntity<>(message, httpStatus);
    }
}
