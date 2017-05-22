package eionet.gdem.web.struts.schema;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.commons.lang3.StringUtils;

/**
 * Schema URL validator.
 * @author Nikolaos Nakas
 * @author George Sofianos
 */
public final class SchemaUrlValidator {
    
    private final UrlValidator validator;

    /**
     * Default Constructor
     */
    public SchemaUrlValidator() {
        this.validator = new UrlValidator();
    }

    /**
     * Is valid URL Set
     * @param urlSet URL set
     * @return True if valid URL set
     */
    public boolean isValidUrlSet(String urlSet) {
        String[] urls = urlSet.split(" ");
        
        for (String url : urls) {
            if (StringUtils.isBlank(url)) {
                continue;
            }
            
            if (!this.validator.isValid(url)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Is Valid URL
     * @param url URL
     * @return True if valid URL
     */
    public boolean isValidUrl(String url) {
        return this.validator.isValid(url);
    }
}
