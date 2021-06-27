package kz.epam.newsportal.service;
import kz.epam.newsportal.model.News;

import java.util.List;

public interface INewsService {
    long addNewsItem(News newsItem);
    List<News> getNewsList();
    News getNewsItemById(long id);
    void deleteNewsItem(News news);
    void updateNewsItem(News newsItem);
}
