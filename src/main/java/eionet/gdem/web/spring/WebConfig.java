package eionet.gdem.web.spring;

import eionet.gdem.data.schemata.SchemaFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;

/**
 *
 *
 */
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurationSupport {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        /*super.addFormatters(registry);*/
        registry.addFormatter(new SchemaFormatter());
        registry.addFormatter(new Formatter<LocalDate>() {
            @Override
            public LocalDate parse(String s, Locale locale) throws ParseException {
                return null;
            }

            @Override
            public String print(LocalDate localDate, Locale locale) {
                return null;
            }
        });
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/projects/**");
    }
}
