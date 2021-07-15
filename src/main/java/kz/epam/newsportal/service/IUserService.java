package kz.epam.newsportal.service;

import kz.epam.newsportal.exception.NotFoundException;
import kz.epam.newsportal.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IUserService {
    long addUser(User user);
    UserDetails loadUserByUsername(String userEmail);
    List<User> getUsersList();
    User getUserById(long id);
    void deleteUser(User user);
    void updateUser(User user) throws NotFoundException;
}
