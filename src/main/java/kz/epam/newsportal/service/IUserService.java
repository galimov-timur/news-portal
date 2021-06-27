package kz.epam.newsportal.service;

import kz.epam.newsportal.exception.*;
import kz.epam.newsportal.model.User;
import kz.epam.newsportal.model.account.UserRegisterModel;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IUserService {
    long addUser(User user) throws UserAlreadyExistException;
    UserDetails loadUserByUsername(String userEmail);
    List<User> getUsersList() throws NotFoundException;
    User getUserById(long id) throws NotFoundException;
    void deleteUser(long id) throws NotFoundException;
    void updateUser(User user, long id) throws NotFoundException;
}
