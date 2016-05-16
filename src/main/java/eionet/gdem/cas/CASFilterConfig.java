package eionet.gdem.cas;

import eionet.gdem.Properties;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
public class CASFilterConfig extends Hashtable<String, String> implements FilterConfig {

    /** */
    private static CASFilterConfig instance;
    private static Object lock = new Object();

    /** */
    private String filterName;
    private ServletContext servletContext;

    /**
     *
     * @param defaultConfig
     */
    private CASFilterConfig(FilterConfig defaultConfig) {

        super();

        if (defaultConfig != null) {

            // load default configuration supplied by CAS
            for (Enumeration names = defaultConfig.getInitParameterNames(); names.hasMoreElements();) {

                String name = names.nextElement().toString();
                put(name, defaultConfig.getInitParameter(name));
            }

            // set filter name and servlet context as they came from default config
            filterName = defaultConfig.getFilterName();
            servletContext = defaultConfig.getServletContext();
        }

        // overwrite with DD's own values
        for (CASInitParam casInitParam : CASInitParam.values()) {

            String name = casInitParam.toString();
            String temp = Properties.getStringProperty(name);
            if (temp != null) {
                put(name, Properties.getStringProperty(name));
            }
        
    }
    }

    /**
     *
     * @param defaultConfig
     */
    public static void init(FilterConfig defaultConfig) {

        if (instance == null) {

            synchronized (lock) {

                // double-checked locking pattern
                // (http://www.ibm.com/developerworks/java/library/j-dcl.html)
                if (instance == null) {
                    instance = new CASFilterConfig(defaultConfig);
                }
            }
        }
    }

    /**
     *
     * @param defaultConfig
     * @return
     */
    public static CASFilterConfig getInstance() {

        if (instance == null) {
            throw new IllegalStateException(
                    CASFilterConfig.class.getSimpleName() + " not yet initialized");
        } else {
            return instance;
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.FilterConfig#getFilterName()
     */
    public String getFilterName() {

        return filterName;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.FilterConfig#getInitParameter(java.lang.String)
     */
    public String getInitParameter(String paramName) {

        return get(paramName);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.FilterConfig#getInitParameterNames()
     */
    public Enumeration<String> getInitParameterNames() {

        return keys();
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.FilterConfig#getServletContext()
     */
    public ServletContext getServletContext() {

        return servletContext;
    }
}