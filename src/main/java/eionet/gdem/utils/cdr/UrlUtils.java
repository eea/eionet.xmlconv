package eionet.gdem.utils.cdr;

import eionet.gdem.http.CustomURI;
import eionet.gdem.utils.Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author George Sofianos
 */
public final class UrlUtils {

    private UrlUtils() {
        // do nothing
    }

    public static String getFolderName(String strUri) {
        URI uri = null;
        String fileName = null;
        String folderName = null;
        try {
            uri = new CustomURI(strUri).getUri();
            strUri = uri.getPath();
            if (Utils.isNullStr(strUri)) {
                return "";
            }

            if (strUri.endsWith("/")) {
                strUri = strUri.substring(0, strUri.length() - 1);
            }

            int lastSlash = strUri.lastIndexOf("/");

            if (lastSlash > -1) {
                fileName = strUri.substring(lastSlash + 1);
                folderName = strUri.substring(0, lastSlash);
            } else {
                fileName = strUri;
                folderName = "";
            }
        } catch (URISyntaxException e) {
            //
        }
        return folderName;
    }

    public static String getFileName(String strUri) {
        URI uri = null;
        String fileName = null;
        String folderName = null;
        try {
            uri = new CustomURI(strUri).getUri();
            strUri = uri.getPath();
            if (Utils.isNullStr(strUri)) {
                return "";
            }

            if (strUri.endsWith("/")) {
                strUri = strUri.substring(0, strUri.length() - 1);
            }

            int lastSlash = strUri.lastIndexOf("/");

            if (lastSlash > -1) {
                fileName = strUri.substring(lastSlash + 1);
                folderName = strUri.substring(0, lastSlash);
            } else {
                fileName = strUri;
                folderName = "";
            }
        } catch (URISyntaxException e) {
            //
        }
        return fileName;
    }

    public static String getHostName(String sourceUrl) {
        try {
            URI uri = new CustomURI(sourceUrl).getUri();
            return uri.getScheme() + "://" + uri.getAuthority();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static Map<String, String> getCdrParams(String sourceUrl) {
        Map<String, String> h = new HashMap<>();
        String hostname = getHostName(sourceUrl);
        String filename = getFileName(sourceUrl);
        String folderName = getFolderName(sourceUrl);
        String strEnvelopeUrl = null;
        String strInstance = null;
        if (hostname != null && folderName != null) {
            strEnvelopeUrl = hostname + folderName;
        }
        if (hostname != null && folderName != null &&  filename != null) {
            strInstance = hostname + folderName + (folderName.endsWith("/") ? "" : "/").concat(filename);
        }
        h.put("filename", filename);
        h.put("envelopeurl", strEnvelopeUrl);
        h.put("envelopepath", folderName);
        h.put("instance", strInstance);
        return h;
    }

    public static String getFileNameNoExtension(String strFileName) {
        URI uri = null;
        String name = null;
        try {
            uri = new CustomURI(strFileName).getUri();
            strFileName = uri.getPath();
            strFileName = getFileName(strFileName);

            if (Utils.isNullStr(strFileName)) {
                return "";
            }

            int lastDot = strFileName.lastIndexOf(".");

            if (lastDot > -1) {
                name = strFileName.substring(0, lastDot);
            } else {
                name = strFileName;
            }
        } catch (URISyntaxException e) {
            //
        }
        return name;
    }
}