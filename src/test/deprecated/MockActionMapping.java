/*
 * Created on 18.03.2008
 */
package eionet.gdem.test.mocks;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * The class mocks ActionMapping class and overwrites some emthods to be able to test struts actions.
 * 
 * @author Enriko Käsper, TietoEnator Estonia AS MockActionMapping
 */

public class MockActionMapping extends ActionMapping {

    public MockActionMapping() {

    }

    public ActionForward findForward(String forwardName) {
        return null;
    }

    public String getPath() {
        return "";
    }
}
