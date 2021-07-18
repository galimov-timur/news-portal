package kz.epam.newsportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.epam.newsportal.model.News;
import kz.epam.newsportal.repository.INewsRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(locations = {"classpath:context/spring-mvc.xml",
        "classpath:context/spring-root.xml", "classpath:context/spring-security.xml",
        "classpath:context/spring-repository.xml"})

public class NewsControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private INewsRepository newsRepository;
    @Autowired
    private NewsController newsController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(newsController).build();
    }

    @Test
    public void getNewsShouldReturnNewsList() throws Exception {
        // Initialize mock news
        createMockNews(2);
        // Execute get request
        mockMvc.perform(get("/news"))
                // Validate status, list size and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                // Validate returned fields
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Title 2")))
                .andExpect(jsonPath("$[1].brief", is("Brief 2")))
                .andExpect(jsonPath("$[1].content", is("Content 2")));
    }


    @Test
    public void getNewsItemShouldReturnNewsItem() throws Exception {
        // Initialize mock news
        createMockNews(1);
        // Execute get request
        mockMvc.perform(get("/news/{id}", 1))
                // Validate status and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                // Validate returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Title 1")))
                .andExpect(jsonPath("$.brief", is("Brief 1")))
                .andExpect(jsonPath("$.content", is("Content 1")));
    }


    @Test
    public void createNewsItemShouldReturnStatusCreatedAndLocation() throws Exception {
        // Create mock news item
        News newsItem = new News("Title", null,"Brief", "Content");
        // Execute the post request
        mockMvc.perform(post("/news")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newsItem)))
                // Validate status
                .andExpect(status().isCreated())
                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/news/1"));
    }

    @Test
    public void updateNewsItemShouldReturnNoContent() throws Exception {
        // Initialize mock news
        createMockNews(2);
        // Create mock news item
        News newsItem = new News("Title", null,"Brief", "Content");
        // Execute the post request
        mockMvc.perform(put("/news/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newsItem)))
                // Validate status
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateNewsItemShouldReturnNotFound() throws Exception {
        // Create mock news item
        News newsItem = new News("Title", null,"Brief", "Content");
        // Execute the post request
        mockMvc.perform(put("/news/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newsItem)))
                // Validate status
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteNewsItemShouldReturnNoContent() throws Exception {
        // Setup mock news
        createMockNews(2);
        // Execute delete request
        mockMvc.perform(delete("/news/{id}", 1))
                // Validate status
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNewsItemShouldReturnNotFound() throws Exception {
        // Execute delete request
        mockMvc.perform(delete("/news/{id}", 1))
                // Validate status
                .andExpect(status().isNotFound());
    }

    // Helper methods
    private void createMockNews(int count) {
        for (int i = 1; i <= count; i++) {
            News newsItem = new News("Title " + i, null,"Brief " + i, "Content " + i);
            newsItem.setId((long) i);
            newsRepository.save(newsItem);
        }
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

