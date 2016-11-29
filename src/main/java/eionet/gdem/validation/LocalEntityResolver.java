/*
 * Created on 18.04.2008
 */
package eionet.gdem.validation;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import eionet.gdem.utils.Utils;

/**
 * The class replaces the remote DTD location to locally stored DTD, if the SYSTEM id matches.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS LocalEntityResolver
 * @author George Sofianos
 */

public class LocalEntityResolver implements EntityResolver {

    private String localSystemId = null;
    private String localId = null;

    /**
     * Constructor
     * @param localSystemId System Id
     * @param localId Local Id
     */
    public LocalEntityResolver(String localSystemId, String localId) {
        this.localSystemId = localSystemId;
        this.localId = localId;
    }

    /**
     * Resolves entity
     * @param publicId Public Id
     * @param systemId System Id
     * @return True if entity is resolved
     */
    public InputSource resolveEntity(String publicId, String systemId) {

        if (!Utils.isNullStr(getLocalId()) && !Utils.isNullStr(getLocalSystemId()) && systemId.equals(getLocalSystemId())) {
            return new InputSource(getLocalId());
        } else {
            // use the default behaviour
            return null;
        }

    }

    public String getLocalSystemId() {
        return localSystemId;
    }

    public void setLocalSystemId(String localSystemId) {
        this.localSystemId = localSystemId;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

}
