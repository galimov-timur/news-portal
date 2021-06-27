package kz.epam.newsportal.controller;

import kz.epam.newsportal.exception.*;
import kz.epam.newsportal.model.News;
import kz.epam.newsportal.service.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;

@Controller
public class NewsController {

    private static final String DELETED_MSG = "Deleted successfully";

    private INewsService newsService;

    @Autowired
    public void setNewsService(INewsService newsService) {
        this.newsService = newsService;
    }

    // Get news list
    @RequestMapping(value="/news", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getNewsList() {
        try {
            List<News> newsList = newsService.getNewsList();
            return new ResponseEntity<>(newsList, HttpStatus.OK);
        } catch(NotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Add news item
    @RequestMapping(value="/news", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> createNewsItem(@RequestBody News newsItem, UriComponentsBuilder uriBuilder) {
        try {
            long newsItemId = newsService.addNewsItem(newsItem);
            URI location = uriBuilder.path("/news/{id}").buildAndExpand(newsItemId).toUri();
            return new ResponseEntity<>(location, HttpStatus.CREATED);
        } catch(InvalidPropertyFormatException propertyFormatException) {
            return new ResponseEntity<>(propertyFormatException.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Get news item
    @RequestMapping(value="/news/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getNewsItem(@PathVariable long id) {
        try {
            News newsItem = newsService.getNewsItemById(id);
            return new ResponseEntity<>(newsItem, HttpStatus.OK);
        } catch(NotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.OK);
        }
    }

    // Update news item
    @RequestMapping(value="/news/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateNewsItem(@RequestBody News newsItem, @PathVariable long id) {
        try {
            newsItem.setId(id);
            newsService.updateNewsItem(newsItem);
            News updatedNewsItem = newsService.getNewsItemById(id);
            return new ResponseEntity<>(updatedNewsItem, HttpStatus.OK);
        } catch(NotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Delete news item
    @RequestMapping(value="/news/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteNewsItem(@PathVariable long id) {
        try {
            newsService.deleteNewsItem(id);
            return new ResponseEntity<>(DELETED_MSG, HttpStatus.NO_CONTENT);
        } catch(NotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
