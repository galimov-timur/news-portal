package kz.epam.newsportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.epam.newsportal.model.News;
import kz.epam.newsportal.repository.INewsRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:context/spring-mvc-test.xml",
        "classpath:context/spring-mvc.xml","classpath:context/spring-root.xml", "classpath:context/spring-security.xml"})
public class NewsControllerTest {

    private MockMvc mockMvc;
    private News mockNewsItem;
    @Autowired
    INewsRepository newsRepositoryMock;
    @Autowired
    private NewsController newsController;

    private static final int NEWS_COUNT = 5;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(newsController).build();
        this.mockNewsItem = createMockNews(1).get(0);
    }

    @Test
    public void shouldReturnNewsList() throws Exception {
        List<News> mockNewsList = createMockNews(NEWS_COUNT);
        when(newsRepositoryMock.findAll()).thenReturn(mockNewsList);

        ResultActions resultActions = mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", Matchers.hasSize(NEWS_COUNT)));

        assertMockNews(resultActions, mockNewsList);
    }

    @Test
    public void shouldReturnNewsItemById() throws Exception {
        when(newsRepositoryMock.findById(mockNewsItem.getId())).thenReturn(mockNewsItem);

        ResultActions resultActions = mockMvc.perform(get("/news/{id}", mockNewsItem.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        assertMockNewsItem(resultActions, mockNewsItem, true);
    }

    @Test
    public void shouldCreateNewsItem() throws Exception {
        when(newsRepositoryMock.save(Mockito.any(News.class))).thenReturn(mockNewsItem.getId());
        String locationHeader = String.format("/news/%d", mockNewsItem.getId());

        mockMvc.perform(post("/news").contentType(MediaType.APPLICATION_JSON).content(asJsonString(mockNewsItem)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", containsString(locationHeader)));
    }

    @Test
    public void shouldUpdateNewsItem() throws Exception {
        when(newsRepositoryMock.findById(mockNewsItem.getId())).thenReturn(mockNewsItem);
        doNothing().when(newsRepositoryMock).update(Mockito.any(News.class));

        mockMvc.perform(put("/news/{id}", mockNewsItem.getId())
                .contentType(MediaType.APPLICATION_JSON).content(asJsonString(mockNewsItem)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateNewsItemShouldReturnNotFound() throws Exception {
        when(newsRepositoryMock.findById(mockNewsItem.getId())).thenReturn(null);
        doNothing().when(newsRepositoryMock).update(Mockito.any(News.class));

        mockMvc.perform(put("/news/{id}", mockNewsItem.getId())
                .contentType(MediaType.APPLICATION_JSON).content(asJsonString(mockNewsItem)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteNewsItem() throws Exception {
        when(newsRepositoryMock.findById(mockNewsItem.getId())).thenReturn(mockNewsItem);
        doNothing().when(newsRepositoryMock).delete(Mockito.any(News.class));

        mockMvc.perform(delete("/news/{id}", mockNewsItem.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNewsItemShouldReturnNotFound() throws Exception {
        when(newsRepositoryMock.findById(mockNewsItem.getId())).thenReturn(null);
        doNothing().when(newsRepositoryMock).delete(Mockito.any(News.class));

        mockMvc.perform(delete("/news/{id}", mockNewsItem.getId()))
                .andExpect(status().isNotFound());
    }

    // Helper methods
    private List<News> createMockNews(int count) {
        List<News> newsList = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            News newsItem = new News("Title " + i, null,"Brief " + i, "Content " + i);
            newsItem.setId((long) i);
            newsList.add(newsItem);
        }
        return newsList;
    }

    private void assertMockNews(ResultActions actions, List<News> newsList) throws Exception {
        for (News mockNews : newsList) {
            assertMockNewsItem(actions, mockNews, false);
        }
    }

    private void assertMockNewsItem(ResultActions actions, News mockNewsItem, boolean isSingle) throws Exception {
        String path = null;
        if (isSingle) {
            path = "$";
        } else {
            path = String.format("$[%d]", (mockNewsItem.getId() - 1));
        }
        actions.andExpect(jsonPath(path + ".id", is((int) mockNewsItem.getId())));
        actions.andExpect(jsonPath(path + ".title", is("Title " + mockNewsItem.getId())));
        actions.andExpect(jsonPath(path + ".brief", is("Brief " + mockNewsItem.getId())));
        actions.andExpect(jsonPath(path + ".content", is("Content " + mockNewsItem.getId())));
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

