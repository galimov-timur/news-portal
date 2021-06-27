package kz.epam.newsportal.resource;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;
import java.util.Properties;

public class NewsMessageSource extends ReloadableResourceBundleMessageSource {
    public Properties getAllProperties(Locale locale) {
        // Clear the resource bundle caches of this MessageSource and all its ancestors.
        clearCacheIncludingAncestors();
        // Get a PropertiesHolder that contains the actually visible properties for a Locale,
        // after merging all specified resource bundles.
        PropertiesHolder propertiesHolder = getMergedProperties(locale);
        Properties properties = propertiesHolder.getProperties();
        return properties;
    }
}
