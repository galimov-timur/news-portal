package kz.epam.newsportal.repository.hibernate;

import kz.epam.newsportal.exception.NotFoundException;
import kz.epam.newsportal.model.*;
import kz.epam.newsportal.repository.IUserRepository;
import org.hibernate.*;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository {

    private static final String HQL_USER_BY_EMAIL = "SELECT u FROM User u JOIN FETCH u.roles ur WHERE u.email = :email";
    private static final String HQL_USER_BY_ID = "SELECT u FROM User u JOIN FETCH u.roles ur WHERE u.id=:id";
    private static final String HQL_FIND_ALL_USERS = "SELECT u FROM User u JOIN FETCH u.roles";

    private static final String EMAIL_PARAM = "email";
    private static final String ID_PARAM = "id";

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(HQL_USER_BY_EMAIL);
        query.setParameter(EMAIL_PARAM, email);
        User user = (User) query.uniqueResult();
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(HQL_FIND_ALL_USERS);
        List<User> users =  (List<User>) query.list();
        return users;
    }

    @Override
    public long save(User user) {
        Session session = sessionFactory.getCurrentSession();
        return (long) session.save(user);
    }

    @Override
    public User findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(HQL_USER_BY_ID, User.class);
        query.setParameter(ID_PARAM, id);
        return (User) query.uniqueResult();
    }

    @Override
    public void delete(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(user);
    }

    @Override
    public void update(User updatedUser, long id) {
        Session session = sessionFactory.getCurrentSession();
        User user = session.get(User.class, id);
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());

        for(Role role : user.getRoles()) {
            session.delete(role);
        }
        for(Role role: updatedUser.getRoles()) {
            role.setUser(user);
        }

        user.setRoles(updatedUser.getRoles());
        session.update(user);
    }
}
