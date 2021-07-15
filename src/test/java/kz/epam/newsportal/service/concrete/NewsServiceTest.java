package kz.epam.newsportal.service.concrete;

import kz.epam.newsportal.model.News;
import kz.epam.newsportal.repository.INewsRepository;
import kz.epam.newsportal.util.ValidationUtility;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;
import java.time.ZonedDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class NewsServiceTest {

    News testNewsItem;
    @Mock
    INewsRepository newsRepository;
    @InjectMocks
    NewsService newsService;

    @BeforeEach
    void setup() {
        long id = 1;
        ZonedDateTime created = ZonedDateTime.now();
        testNewsItem = new News( "title", created,"brief", "Content");
        testNewsItem.setId(id);
    }

    @Test
    void testSaveNews() {
        // given
        newsService.setFormUtil(new ValidationUtility());
        // when
        newsService.addNewsItem(testNewsItem);
        // then
        verify(newsRepository).save(testNewsItem);
    }

    @Test
    void testGetNewsList() {
        // given
        ZonedDateTime created = ZonedDateTime.now();
        News testNewsItem2 = new News("title2", created,"brief2", "Content2");
        Mockito.when(newsRepository.findAll()).thenReturn(Arrays.asList(testNewsItem, testNewsItem2));
        // when
        List<News> newsList = newsService.getNewsList();
        // then
        assertEquals(2, newsList.size());
        assertTrue(newsList.get(0).getTitle().equals(testNewsItem.getTitle()));
    }

    @Test
    void testGetNewsItemById() {
        // given
        long searchedNewsId = 1;
        Mockito.when(newsRepository.findById(searchedNewsId)).thenReturn(testNewsItem);
        // when
        News news = newsService.getNewsItemById(searchedNewsId);
        // then
        assertNotNull(news);
        assertEquals(searchedNewsId, news.getId());
    }

    @Test
    void testDeleteNewsItem() {
        // when
        newsService.deleteNewsItem(testNewsItem);
        // then
        verify(newsRepository).delete(testNewsItem);
    }

    @Test
    void testUpdateNews() {
        // when
        newsService.updateNewsItem(testNewsItem);
        //then
        verify(newsRepository).update(testNewsItem);
    }
}
