package eionet.gdem.services;

import eionet.gdem.XMLConvException;

import java.util.Vector;

public interface RunScriptAutomaticService {
    Vector runQAScript(String sourceUrl, String scriptId) throws XMLConvException;
}
