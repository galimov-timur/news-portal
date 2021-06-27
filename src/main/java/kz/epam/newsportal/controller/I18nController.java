package kz.epam.newsportal.controller;

import kz.epam.newsportal.resource.NewsMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Properties;

@Controller
public class I18nController {

    private NewsMessageSource messageSource;

    @Autowired
    public void setMessageSource(NewsMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RequestMapping(value="/language", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Properties getProperties(Locale locale) {
        return messageSource.getAllProperties(locale);
    }
}
