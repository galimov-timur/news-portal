package kz.epam.newsportal.repository;

import kz.epam.newsportal.exception.NotFoundException;
import kz.epam.newsportal.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    Optional<User> findUserByEmail(String email);
    List<User> findAll();
    long save(User user);
    User findById(long id);
    void delete(User user);
    void update(User user, long id);
}
