package eionet.gdem.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 *
 */
@Service
public class MessageService {

    @Autowired
    MessageSource messageSource;

    public String getMessage(String key) {
        String value = messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        return StringUtils.defaultIfEmpty(value, key);
    }
}
