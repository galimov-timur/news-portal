package kz.epam.newsportal.service.concrete;

import kz.epam.newsportal.model.News;
import kz.epam.newsportal.repository.INewsRepository;
import kz.epam.newsportal.util.ValidationUtility;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
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
        // given
        ZonedDateTime created = ZonedDateTime.now();
        News newsItem = new News( "title", created,"brief", "Content");
        newsService.setFormUtil(new ValidationUtility());
        // when
        newsService.addNewsItem(newsItem);
        // then
        verify(newsRepository).save(newsItem);
    }

    @Test
    void testGetNewsList() {
        // given
        ZonedDateTime created = ZonedDateTime.now();
        News news1 = new News("title", created,"brief", "Content");
        News news2 = new News("title2", created,"brief2", "Content2");
        Mockito.when(newsRepository.findAll()).thenReturn(Arrays.asList(news1, news2));
        // when
        List<News> newsList = newsService.getNewsList();
        // then
        assertEquals(2, newsList.size());
        assertTrue(newsList.get(0).getTitle().equals(news1.getTitle()));
    }

    @Test
    void testGetNewsItemById() {
        // given
        long searchedNewsId = 1;
        ZonedDateTime created = ZonedDateTime.now();
        News searchedNews = new News("title", created,"brief", "Content");
        searchedNews.setId(searchedNewsId);
        Mockito.when(newsRepository.findById(searchedNewsId)).thenReturn(searchedNews);
        // when
        News news = newsService.getNewsItemById(searchedNewsId);
        // then
        assertNotNull(news);
        assertEquals(searchedNewsId, news.getId());
    }

    @Test
    void testDeleteNewsItem() {
        // given
        ZonedDateTime created = ZonedDateTime.now();
        News newsToDelete = new News("title", created,"brief", "Content");
        // when
        newsService.deleteNewsItem(newsToDelete);
        // then
        verify(newsRepository).delete(newsToDelete);
    }

    @Test
    void testUpdateNews() {
        // given
        ZonedDateTime created = ZonedDateTime.now();
        News updatedNews= new News("updated title", created,"updated brief", "updated content");
        // when
        newsService.updateNewsItem(updatedNews);
        //then
        verify(newsRepository).update(updatedNews);
    }
}
