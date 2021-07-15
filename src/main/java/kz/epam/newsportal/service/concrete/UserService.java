package kz.epam.newsportal.service.concrete;
import kz.epam.newsportal.exception.*;
import kz.epam.newsportal.model.*;
import kz.epam.newsportal.repository.*;
import kz.epam.newsportal.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService implements UserDetailsService, IUserService {

    private static final String EXCEPTION_USER_NOT_FOUND = "User is not found";

    private static final long ZERO_VALUE = 0;
    private static final String USERS_NOT_FOUND_MSG = "Could not find any users";
    private static final String USER_NOT_FOUND_MSG = "User not found";

    private IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Optional<User> userOptional = this.userRepository.findUserByEmail(userEmail);
        if(!userOptional.isPresent()) {
            throw new UsernameNotFoundException(USER_NOT_FOUND_MSG);
        } else {
            return userOptional.get();
        }
    }

    @Override
    @Transactional
    public long addUser(User user) {
        Optional<User> userOptional =  userRepository.findUserByEmail(user.getEmail());
        if(userOptional.isPresent()) {
            return ZERO_VALUE;
        }
        return this.userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersList() {
        List<User> users = this.userRepository.findAll();
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(long id) {
        User user = this.userRepository.findById(id);
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        this.userRepository.delete(user);
    }

    @Override
    @Transactional
    public void updateUser(User updatedUser) throws NotFoundException {
        this.userRepository.update(updatedUser);
    }
}