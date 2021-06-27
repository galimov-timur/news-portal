package kz.epam.newsportal.service.concrete;
import kz.epam.newsportal.exception.*;
import kz.epam.newsportal.model.*;
import kz.epam.newsportal.model.account.UserRegisterModel;
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

    private static final String USER_ALREADY_EXIST_MSG = "This email is already in use !!!";
    private static final String USERS_NOT_FOUND_MSG = "Could not find any users";
    private static final String USER_NOT_FOUND_MSG = "User not found";

    private IUserRepository userRepository;

    @Autowired
    public void setUserRepository(IUserRepository userRepository) {
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

    @Transactional
    public boolean emailExist(String email) {
        Optional<User> userOptional =  userRepository.findUserByEmail(email);
        return userOptional.isPresent();
    }

    @Override
    @Transactional
    public long addUser(User user) throws UserAlreadyExistException {
        String userEmail = user.getEmail();
        if(emailExist(userEmail)) {
            throw new UserAlreadyExistException(USER_ALREADY_EXIST_MSG);
        }
        return this.userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersList() throws NotFoundException {
        List<User> users = this.userRepository.findAll();
        if(users.isEmpty()) {
            throw new NotFoundException(USERS_NOT_FOUND_MSG);
        }
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(long id) throws NotFoundException {
        User user = this.userRepository.findById(id);
        if(user == null) {
            throw new NotFoundException(USER_NOT_FOUND_MSG);
        }
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(long id) throws NotFoundException {
        User user = userRepository.findById(id);
        if(user == null) {
            throw new NotFoundException(USER_NOT_FOUND_MSG);
        }
        this.userRepository.delete(user);
    }

    @Override
    @Transactional
    public void updateUser(User updatedUser, long id) throws NotFoundException {
        User user = userRepository.findById(id);
        if(user == null) {
            throw new NotFoundException(USER_NOT_FOUND_MSG);
        }
        this.userRepository.update(updatedUser, user.getId());
    }
}