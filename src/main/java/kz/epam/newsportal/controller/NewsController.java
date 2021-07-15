package kz.epam.newsportal.controller;

import kz.epam.newsportal.exception.*;
import kz.epam.newsportal.model.News;
import kz.epam.newsportal.service.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@Controller
public class NewsController {

    private INewsService newsService;

    @Autowired
    public void setNewsService(INewsService newsService) {
        this.newsService = newsService;
    }

    // Get news list
    @RequestMapping(value="/news", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<News>> getNewsList() {
        List<News> newsList = newsService.getNewsList();
        HttpStatus httpStatus = newsList.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        return new ResponseEntity<>(newsList, httpStatus);
    }

    // Add news item
    @RequestMapping(value="/news", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> createNewsItem(@RequestBody News newsItem, UriComponentsBuilder uriBuilder) {
        long newsItemId = newsService.addNewsItem(newsItem);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriBuilder.path("/news/{id}").buildAndExpand(newsItemId).toUri());
        HttpStatus httpStatus = newsItemId > 0 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(headers, httpStatus);
    }

    // Get news item
    @RequestMapping(value="/news/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<News> getNewsItem(@PathVariable long id) {
        News newsItem = newsService.getNewsItemById(id);
        HttpStatus httpStatus = (newsItem == null) ? HttpStatus.NOT_FOUND: HttpStatus.OK;
        return new ResponseEntity<>(newsItem, httpStatus);
    }

    // Update news item
    @RequestMapping(value="/news/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateNewsItem(@RequestBody News updatedNews, @PathVariable long id) {
        News storedNews = newsService.getNewsItemById(id);
        if(storedNews != null) {
            updatedNews.setId(id);
            newsService.updateNewsItem(updatedNews);
        }
        HttpStatus httpStatus = storedNews == null ? HttpStatus.NOT_FOUND : HttpStatus.NO_CONTENT;
        return new ResponseEntity<>(httpStatus);
    }

    // Delete news item
    @RequestMapping(value="/news/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteNewsItem(@PathVariable long id) {
        News storedNews = newsService.getNewsItemById(id);
        if(storedNews != null) {
            newsService.deleteNewsItem(storedNews);
        }
        HttpStatus httpStatus = storedNews == null ? HttpStatus.NOT_FOUND : HttpStatus.NO_CONTENT;
        return new ResponseEntity<>(httpStatus);
    }
}
