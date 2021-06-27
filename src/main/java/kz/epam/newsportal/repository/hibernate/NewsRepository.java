package kz.epam.newsportal.repository.hibernate;

import kz.epam.newsportal.model.News;
import kz.epam.newsportal.repository.INewsRepository;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NewsRepository implements INewsRepository {

    private static final String CREATED_FIELD = "created";
    private static final String UPDATE_NEWS_QUERY = "UPDATE news SET title=?, brief=?, content=? WHERE id=?";

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("depricated")
    public List<News> findAll() {
        Criteria cr = sessionFactory.getCurrentSession().createCriteria(News.class);
        cr.addOrder(Order.desc(CREATED_FIELD));
        return (List<News>) cr.list();
    }

    @Override
    public News findById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (News) session.get(News.class, id);
    }

    @Override
    public long save(News newsItem) {
        Session session = sessionFactory.getCurrentSession();
        return (long) session.save(newsItem);
    }

    @Override
    public void delete(News newsItem) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(newsItem);
    }

    @Override
    public void update(News newsItem) {
        Session session = sessionFactory.getCurrentSession();
        session.createSQLQuery(UPDATE_NEWS_QUERY)
                .setParameter(1, newsItem.getTitle())
                .setParameter(2, newsItem.getBrief())
                .setParameter(3, newsItem.getContent())
                .setParameter(4, newsItem.getId())
                .executeUpdate();
    }
}
