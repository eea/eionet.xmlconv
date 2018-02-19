package eionet.gdem.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public String getMessage(String key, Object... strings) {
        String value = messageSource.getMessage(key, strings, LocaleContextHolder.getLocale());
        return StringUtils.defaultIfEmpty(value, key);
    }

    public String getMessageInt(String key, Object... strings) {
        List<String> params = new ArrayList<>();
        for (Object s: strings) {
            String val = messageSource.getMessage((String) s, null, LocaleContextHolder.getLocale());
            params.add(val);
        }
        return getMessage(key, params.toArray());
    }
}
