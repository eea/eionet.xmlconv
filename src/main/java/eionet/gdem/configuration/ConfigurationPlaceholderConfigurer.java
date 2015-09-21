package eionet.gdem.configuration;

import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringValueResolver;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class ConfigurationPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationPlaceholderConfigurer(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {

        StringValueResolver valueResolver = new ConfigurationPlaceholderConfigurer.PlaceholderResolvingStringValueResolver(props);

        this.doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    String get(String key) {
        try {
            return configurationService.get(key);
        } catch (UnResolvedPropertyException e) {
            return null;
        }
    }

    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final PropertyPlaceholderHelper helper;

        private final PropertyPlaceholderHelper.PlaceholderResolver resolver;

        public PlaceholderResolvingStringValueResolver(Properties props) {
            this.helper = new PropertyPlaceholderHelper(
                    placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
            this.resolver = new PropertyPlaceholderConfigurerResolver();
        }

        public String resolveStringValue(String strVal) throws BeansException {
            String value = this.helper.replacePlaceholders(strVal, this.resolver);
            return (value.equals(nullValue) ? null : value);
        }
    }

    private class PropertyPlaceholderConfigurerResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        public String resolvePlaceholder(String placeholderName) {
            String value = ConfigurationPlaceholderConfigurer.this.get(placeholderName);
            return value;
        }
    }
}
