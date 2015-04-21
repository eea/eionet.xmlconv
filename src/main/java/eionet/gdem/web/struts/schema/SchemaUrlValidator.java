package eionet.gdem.web.struts.schema;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Nikolaos Nakas
 */
public final class SchemaUrlValidator {
    
    private final UrlValidator validator;
    
    public SchemaUrlValidator() {
        this.validator = new UrlValidator();
    }
    
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
    
    public boolean isValidUrl(String url) {
        return this.validator.isValid(url);
    }
}
