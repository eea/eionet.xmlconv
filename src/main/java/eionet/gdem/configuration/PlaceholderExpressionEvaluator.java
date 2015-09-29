package eionet.gdem.configuration;

import eionet.gdem.configuration.util.ConfigurationLoadException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class PlaceholderExpressionEvaluator {
    
    static final Pattern PLACEHOLDER_PATTERN;
    
    static {
        PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}");
    }
    
    public String evaluate(String expression, ConfigurationPropertyResolver propertyResolver) 
            throws UnresolvedPropertyException, CircularReferenceException, ConfigurationLoadException {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(expression);
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        
        while (matcher.find()) {
            sb.append(expression.substring(offset, matcher.start()));
            String propertyName = matcher.group(1);
            String resolvedValue = propertyResolver.resolveValue(propertyName);
            sb.append(resolvedValue);
            String placeholder = matcher.group();
            offset += placeholder.length();
        }
        
        sb.append(expression.substring(offset, expression.length()));
        
        return sb.toString();
    }
    
}
