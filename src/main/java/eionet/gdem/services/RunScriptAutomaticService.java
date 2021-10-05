package eionet.gdem.services;

import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;

import java.util.Vector;

public interface RunScriptAutomaticService {


    Vector runQAScript(String sourceUrl, String scriptId, HttpMethodResponseWrapper methodResponse) throws XMLConvException;

    Vector runQAScript(String sourceUrl, String scriptId) throws XMLConvException;
}
