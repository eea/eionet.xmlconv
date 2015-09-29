package eionet.gdem.configuration.spring;

import eionet.gdem.configuration.CircularReferenceException;
import eionet.gdem.configuration.ConfigurationPropertyResolver;
import eionet.gdem.configuration.UnresolvedPropertyException;
import eionet.gdem.configuration.util.ConfigurationLoadException;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringValueResolver;

/**
 *
 * @author Ervis Zyka <ez@eworx.gr>
 */
public class EionetPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private final ConfigurationPropertyResolver propertyResolver;
    
    public EionetPlaceholderConfigurer(ConfigurationPropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }
    
    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {
        StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
        this.doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final PropertyPlaceholderHelper helper;

        private final PropertyPlaceholderHelper.PlaceholderResolver resolver;

        public PlaceholderResolvingStringValueResolver(Properties props) {
            this.helper = new PropertyPlaceholderHelper(
                    placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
            this.resolver = new PropertyPlaceholderConfigurerResolver();
        }

        @Override
        public String resolveStringValue(String strVal) throws BeansException {
            String value = this.helper.replacePlaceholders(strVal, this.resolver);
            return (value.equals(nullValue) ? null : value);
        }
        
    }

    private class PropertyPlaceholderConfigurerResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        @Override
        public String resolvePlaceholder(String placeholderName) {
            try {
                return propertyResolver.resolveValue(placeholderName);
            }
            catch (UnresolvedPropertyException ex) {
                throw new FatalBeanException(ex.getMessage(), ex);
            }
            catch (CircularReferenceException ex) {
                throw new FatalBeanException(ex.getMessage(), ex);
            }
            catch (ConfigurationLoadException ex) {
                throw new FatalBeanException(ex.getMessage(), ex);
            }
        }
        
    }
    
}
