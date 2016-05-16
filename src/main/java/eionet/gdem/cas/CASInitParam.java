package eionet.gdem.cas;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
import edu.yale.its.tp.cas.client.filter.CASFilter;

/**
 *
 * @author <a href="mailto:jaanus.heinlaid@tieto.com">Jaanus Heinlaid</a>
 *
 */
public enum CASInitParam {

    CAS_LOGIN_URL(CASFilter.LOGIN_INIT_PARAM),
    CAS_VALIDATE_URL(CASFilter.VALIDATE_INIT_PARAM),
    CAS_SERVER_NAME(CASFilter.SERVERNAME_INIT_PARAM),
    CAS_WRAP_REQUEST(CASFilter.WRAP_REQUESTS_INIT_PARAM);

    /** */
    private String propertyName;

    /**
     *
     * @param propertyName
     */
    private CASInitParam(String propertyName) {
        this.propertyName = propertyName;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    public String toString() {
        return propertyName;
    }
}