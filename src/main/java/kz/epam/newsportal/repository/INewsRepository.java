package kz.epam.newsportal.repository;

import kz.epam.newsportal.exception.NotFoundException;
import kz.epam.newsportal.model.News;

import java.util.List;

public interface INewsRepository {
    List<News> findAll();
    News findById(long id);
    long save(News newsItem);
    void delete(News newsItem);
    void update(News newsItem);
}
