package kz.epam.newsportal.service;

import kz.epam.newsportal.exception.*;
import kz.epam.newsportal.model.News;

import java.util.List;

public interface INewsService {
    long addNewsItem(News newsItem) throws InvalidPropertyFormatException;
    List<News> getNewsList() throws NotFoundException;
    News getNewsItemById(long id) throws NotFoundException;
    void deleteNewsItem(long id) throws NotFoundException;
    void updateNewsItem(News newsItem) throws NotFoundException;
}
