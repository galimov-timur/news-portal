package kz.epam.newsportal.repository.hibernate;

import kz.epam.newsportal.model.Role;
import kz.epam.newsportal.repository.IRoleRepository;
import org.hibernate.*;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class RoleRepository implements IRoleRepository {

    private static final String HQL_ROLES_BY_USER_ID = "FROM Role r where r.user = :id";
    private static final String ID_PARAM = "id";
    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Role> findRolesByUserId(long userId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(HQL_ROLES_BY_USER_ID);
        query.setParameter(ID_PARAM, userId);
        List result = query.list();
        return (List<Role>) result;
    }

    @Override
    public long save(Role newRole) {
        Session session = sessionFactory.getCurrentSession();
        return (long) session.save(newRole);
    }

    @Override
    public Role find(long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Role.class, id);
    }
}
