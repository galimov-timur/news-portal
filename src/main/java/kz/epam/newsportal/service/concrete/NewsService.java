package kz.epam.newsportal.service.concrete;

import kz.epam.newsportal.exception.*;
import kz.epam.newsportal.model.News;
import kz.epam.newsportal.repository.INewsRepository;
import kz.epam.newsportal.service.INewsService;
import kz.epam.newsportal.util.ValidationUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class NewsService implements INewsService {

    private INewsRepository newsRepository;
    private ValidationUtility validationUtility;

    private static final int MAX_TITLE_LENGTH = 250;
    private static final int MAX_BRIEF_LENGTH = 1000;
    private static final int MAX_CONTENT_LENGTH = 5000;

    private static final String PROPERTY_NAME_TITLE = "News title";
    private static final String PROPERTY_NAME_BRIEF = "News brief";
    private static final String PROPERTY_NAME_CONTENT = "News content";

    private static final String NEWS_NOT_FOUND_MSG = "News not found";

    @Autowired
    public void setNewsRepository(INewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }
    @Autowired
    public void setFormUtil(ValidationUtility validationUtility) {
        this.validationUtility = validationUtility;
    }

    @Override
    @Transactional
    public long addNewsItem(News newsItem) throws InvalidPropertyFormatException {

        validationUtility.validateStringProperty(newsItem.getTitle(), MAX_TITLE_LENGTH, PROPERTY_NAME_TITLE);
        validationUtility.validateStringProperty(newsItem.getBrief(), MAX_BRIEF_LENGTH, PROPERTY_NAME_BRIEF);
        validationUtility.validateStringProperty(newsItem.getContent(), MAX_CONTENT_LENGTH, PROPERTY_NAME_CONTENT);

        ZonedDateTime creationDate = ZonedDateTime.now();
        newsItem.setCreated(creationDate);
        return this.newsRepository.save(newsItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<News> getNewsList() throws NotFoundException {
        List<News> newsList = this.newsRepository.findAll();
        if(newsList.isEmpty()) {
            throw new NotFoundException(NEWS_NOT_FOUND_MSG);
        }
        return newsList;
    }

    @Override
    @Transactional(readOnly = true)
    public News getNewsItemById(long id) throws NotFoundException {
        News newsItem = this.newsRepository.findById(id);
        if(newsItem == null) {
            throw new NotFoundException(NEWS_NOT_FOUND_MSG);
        }
        return newsItem;
    }

    @Override
    @Transactional
    public void deleteNewsItem(long id) throws NotFoundException {
        News newsItem = this.newsRepository.findById(id);
        if(newsItem == null) {
            throw new NotFoundException(NEWS_NOT_FOUND_MSG);
        }
        this.newsRepository.delete(newsItem);
    }

    @Override
    @Transactional
    public void updateNewsItem(News updatedNews) throws NotFoundException {
        News storedNews = this.newsRepository.findById(updatedNews.getId());
        if(storedNews == null) {
            throw new NotFoundException(NEWS_NOT_FOUND_MSG);
        }
        newsRepository.update(updatedNews);
    }
}
