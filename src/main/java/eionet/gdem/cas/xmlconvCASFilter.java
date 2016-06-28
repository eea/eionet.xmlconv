package eionet.gdem.cas;

import edu.yale.its.tp.cas.client.filter.CASFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */

/**
 *
 * @author <a href="mailto:jaanus.heinlaid@tieto.com">Jaanus Heinlaid</a>
 *
 */
public class xmlconvCASFilter extends CASFilter {

    /** Static logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(xmlconvCASFilter.class);

    /** FQN of this class. */
    private static final String CLASS_NAME = xmlconvCASFilter.class.getName();

    /*
     * (non-Javadoc)
     *
     * @see edu.yale.its.tp.cas.client.filter.CASFilter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig config) throws ServletException {

        LOGGER.info("Initializing " + CLASS_NAME + " ...");

        CASFilterConfig.init(config);
        super.init(CASFilterConfig.getInstance());
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.yale.its.tp.cas.client.filter.CASFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
     * javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws ServletException, IOException {

        LOGGER.trace(CLASS_NAME + ".doFilter() invoked ...");
        super.doFilter(request, response, fc);
    }
}
