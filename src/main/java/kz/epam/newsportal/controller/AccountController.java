package kz.epam.newsportal.controller;

import kz.epam.newsportal.model.JwtToken;
import kz.epam.newsportal.model.User;
import kz.epam.newsportal.model.account.LoginRequestModel;
import kz.epam.newsportal.service.IUserService;
import kz.epam.newsportal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AccountController {

    private IUserService userService;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

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
}
