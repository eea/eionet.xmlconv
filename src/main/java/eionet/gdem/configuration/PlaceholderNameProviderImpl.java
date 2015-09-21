package eionet.gdem.configuration;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public final class PlaceholderNameProviderImpl implements PlaceholderNameProvider {

    /**
     * Searches a string an returns a {@link java.util.Set} of placeholder names.
     * @param value The string that contains placeholders.
     * @return  {@link java.util.Set} A set of placeholder names
     */
    @Override
    public Set<String> extract(String value) {
        Set<String> placeholders = new HashSet<String>();
        if (value != null) {
            final String regex = "\\$\\{([^\\}])+\\}";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(value);
            while (matcher.find()) {
                final String placeholder = matcher.group();
                placeholders.add(StringUtils.substring(placeholder, 2, StringUtils.length(placeholder)-1));
            }
        }
        return placeholders;
    }

}
