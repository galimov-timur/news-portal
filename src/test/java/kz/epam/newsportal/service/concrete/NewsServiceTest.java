package kz.epam.newsportal.service.concrete;

import kz.epam.newsportal.exception.NotFoundException;
import kz.epam.newsportal.model.News;
import kz.epam.newsportal.repository.INewsRepository;
import kz.epam.newsportal.util.ValidationUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class NewsServiceTest {
    @Mock
    INewsRepository newsRepository;
    @InjectMocks
    NewsService newsService;

    @Test
    void testSaveNews() {
        ZonedDateTime created = ZonedDateTime.now();
        News newsItem = new News(5, "title", created,"brief", "Content");

        try {
            newsService.setFormUtil(new ValidationUtility());
            Mockito.when(newsRepository.save(newsItem)).thenReturn((long)1);
            long id = newsService.addNewsItem(newsItem);
            Assertions.assertTrue(id > 0);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetNewsList() {
        ZonedDateTime created = ZonedDateTime.now();
        News firstNews = new News(5, "title", created,"brief", "Content");
        News secondNews = new News(6, "title2", created,"brief2", "Content3");

        try {
            Mockito.when(newsRepository.findAll()).thenReturn(Arrays.asList(firstNews, secondNews));
            List<News> newsList = newsService.getNewsList();
            Assertions.assertEquals(2, newsList.size(), "Method getNewsList should return 2 news");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetNewsListThrowsNotFoundException() {
        Mockito.when(newsRepository.findAll()).thenReturn(Arrays.asList());
        Assertions.assertThrows(NotFoundException.class, ()->newsService.getNewsList());
    }

    @Test
    void testGetNewsItemById() {
        long id = 5;
        ZonedDateTime created = ZonedDateTime.now();
        News firstNews = new News(5, "title", created,"brief", "Content");

        try {
            Mockito.when(newsRepository.findById(id)).thenReturn(firstNews);
            News news = newsService.getNewsItemById(id);
            Assertions.assertNotNull(news, "Object should not be null");
            Assertions.assertEquals(id, news.getId(), "Object's id should be " + id);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetNewsItemByIdThrowsNotFoundException() {
        long id = 5;
        Mockito.when(newsRepository.findById(id)).thenReturn(null);
        Assertions.assertThrows(NotFoundException.class, ()->newsService.getNewsItemById(id));
    }

    @Test
    void testDeleteNewsItem() {
        ZonedDateTime created = ZonedDateTime.now();
        News news= new News(2, "title 2", created,"brief 2", "Content 2");
        long id = 2;
        try {
            Mockito.when(newsRepository.findById(id)).thenReturn(news);
            newsService.deleteNewsItem(id);
            Mockito.verify(newsRepository).delete(news);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testDeleteNewsItemThrowsNotFoundException() {
        long id = 1;
        Mockito.when(newsRepository.findById(id)).thenReturn(null);
        Assertions.assertThrows(NotFoundException.class, () -> newsService.deleteNewsItem(id));
    }

    @Test
    void testUpdateNews() {
        ZonedDateTime created = ZonedDateTime.now();
        News updatedNews= new News(2, "updated title", created,"updated brief", "updated content");
        News storedNews= new News(2, "stored title", created,"stored brief", "stored content");

        try {
            Mockito.when(newsRepository.findById(updatedNews.getId())).thenReturn(storedNews);
            newsService.updateNewsItem(updatedNews);
            Mockito.verify(newsRepository).update(updatedNews);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testUpdateNewsThrowsNotFoundException() {
        ZonedDateTime created = ZonedDateTime.now();
        News updatedNews = new News(2, "updated title", created, "updated brief", "updated content");
        Mockito.when(newsRepository.findById(updatedNews.getId())).thenReturn(null);
        Assertions.assertThrows(NotFoundException.class, () -> newsService.updateNewsItem(updatedNews), "Should throw NotFoundException");
    }
}
