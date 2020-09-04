package org.basex.util.http;

import org.basex.io.in.NewlineInput;
import org.basex.util.Strings;
import org.basex.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

public final class MediaType {
    /** Multipart type. */
    private static final String MULTIPART = "multipart";
    /** Text type. */
    private static final String TEXT = "text";
    /** XQuery sub type. */
    private static final String XQUERY = "xquery";
    /** CSV sub type. */
    private static final String CSV = "csv";
    /** CSV sub type. */
    private static final String COMMA_SEPARATED_VALUES = "comma-separated-values";
    /** XML media type suffix. */
    private static final String XML_SUFFIX = "+xml";
    /** JSON media type suffix. */
    private static final String JSON_SUFFIX = "+json";

    /** Media type: wildcards. */
    public static final org.basex.util.http.MediaType ALL_ALL = new org.basex.util.http.MediaType("*/*");

    /** Media type: application/x-www-form-urlencoded. */
    public static final org.basex.util.http.MediaType APPLICATION_X_WWW_FORM_URLENCODED =
            new org.basex.util.http.MediaType("application/x-www-form-urlencoded");
    /** Media type: application/html+xml. */
    public static final org.basex.util.http.MediaType APPLICATION_HTML_XML = new org.basex.util.http.MediaType("application/html+xml");
    /** Text/plain. */
    public static final org.basex.util.http.MediaType APPLICATION_JSON = new org.basex.util.http.MediaType("application/json");
    /** Media type: text/plain. */
    public static final org.basex.util.http.MediaType APPLICATION_OCTET_STREAM =
            new org.basex.util.http.MediaType("application/octet-stream");
    /** Media type: application/xml. */
    public static final org.basex.util.http.MediaType APPLICATION_XML = new org.basex.util.http.MediaType("application/xml");
    /** Media type: application/xml-external-parsed-entity. */
    public static final org.basex.util.http.MediaType APPLICATION_XML_EPE =
            new org.basex.util.http.MediaType("application/xml-external-parsed-entity");

    /** Media type: multipart/form-data. */
    public static final org.basex.util.http.MediaType MULTIPART_FORM_DATA = new org.basex.util.http.MediaType("multipart/form-data");

    /** Media type: text/comma-separated-values. */
    public static final org.basex.util.http.MediaType TEXT_CSV = new org.basex.util.http.MediaType("text/csv");
    /** Media type: text/html. */
    public static final org.basex.util.http.MediaType TEXT_HTML = new org.basex.util.http.MediaType("text/html");
    /** Media type: text/plain. */
    public static final org.basex.util.http.MediaType TEXT_PLAIN = new org.basex.util.http.MediaType("text/plain");
    /** Media type: text/xml. */
    public static final org.basex.util.http.MediaType TEXT_XML = new org.basex.util.http.MediaType("text/xml");
    /** XML media type. */
    public static final org.basex.util.http.MediaType TEXT_XML_EPE = new org.basex.util.http.MediaType("text/xml-external-parsed-entity");

    /** Main type. */
    private final String main;
    /** Sub type. */
    private final String sub;
    /** Parameters. */
    private final HashMap<String, String> params = new HashMap<>();

    /**
     * Constructor.
     * @param string media type string
     */
    public MediaType(final String string) {
        final int p = string.indexOf(';');
        final String type = p == -1 ? string : string.substring(0, p);

        // set main and sub type
        final int s = type.indexOf('/');
        main = s == -1 ? type : type.substring(0, s);
        sub  = s == -1 ? "" : type.substring(s + 1);

        // parse parameters (simplified version of RFC 2045; no support for comments, etc.)
        if(p != -1) {
            for(final String param : Strings.split(string.substring(p + 1), ';')) {
                final String[] kv = Strings.split(param, '=', 2);
                // attribute: trim whitespaces, convert to lower case
                final String k = kv[0].trim().toLowerCase(Locale.ENGLISH);
                // value: trim whitespaces, remove quotes and backslashed characters
                String v = kv.length < 2 ? "" : kv[1].trim();
                if(Strings.startsWith(v, '"')) v = v.replaceAll("^\"|\"$", "").replaceAll("\\\\(.)", "$1");
                params.put(k, v);
            }
        }
    }

    /**
     * Returns the main type.
     * @return type
     */
    public String main() {
        return main;
    }

    /**
     * Returns the sub type.
     * @return type
     */
    public String sub() {
        return sub;
    }

    /**
     * Returns the media type, composed from the main and sub type.
     * @return type
     */
    public String type() {
        return sub.isEmpty() ? main : (main + '/' + sub);
    }

    /**
     * Returns the parameters.
     * @return parameters
     */
    public HashMap<String, String> parameters() {
        return params;
    }

    /**
     * Checks if this is a multipart type.
     * @return result of check
     */
    public boolean isMultipart() {
        return main.equals(MULTIPART);
    }

    /**
     * Checks if this is a text type.
     * @return result of check
     */
    public boolean isText() {
        return main.equals(TEXT);
    }

    /**
     * Checks if this is an XQuery type.
     * @return result of check
     */
    public boolean isXQuery() {
        return sub.equals(XQUERY);
    }

    /**
     * Checks if this is a CSV type.
     * @return result of check
     */
    public boolean isCSV() {
        return sub.equals(CSV) || sub.equals(COMMA_SEPARATED_VALUES);
    }

    /**
     * Checks if this is an XML type.
     * @return result of check
     */
    public boolean isXML() {
        return is(TEXT_XML) || is(TEXT_XML_EPE) || is(APPLICATION_XML) || is(APPLICATION_XML_EPE) ||
                sub.endsWith(XML_SUFFIX);
    }

    /**
     * Checks if this is a JSON type.
     * @return result of check
     */
    public boolean isJSON() {
        return is(APPLICATION_JSON) || sub.endsWith(JSON_SUFFIX);
    }

    /**
     * Checks if the pattern is matching.
     * @param pattern pattern
     * @return result of check
     */
    public boolean matches(final org.basex.util.http.MediaType pattern) {
        return Strings.eq(pattern.main(), main, "*") && Strings.eq(pattern.sub(), sub, "*");
    }

    /**
     * Checks if the main and sub type of this and the specified type are equal.
     * @param type type
     * @return result of check
     */
    public boolean is(final org.basex.util.http.MediaType type) {
        return main.equals(type.main) && sub.equals(type.sub);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(type());
        params.forEach((key, value) -> sb.append("; ").append(key).append('=').append(value));
        return sb.toString();
    }

    /**
     * Returns the media type for the suffix of the specified file path.
     * {@code application/octet-stream} is returned if no type is found.
     * @param path path to be checked
     * @return media type
     */
    public static org.basex.util.http.MediaType get(final String path) {
        final int s = path.lastIndexOf('/'), d = path.lastIndexOf('.');
        final String suffix = d <= s ? "" : path.substring(d + 1).toLowerCase(Locale.ENGLISH);
        return TYPES.getOrDefault(suffix, APPLICATION_OCTET_STREAM);
    }

    /** Hash map containing all assignments. */
    private static final HashMap<String, org.basex.util.http.MediaType> TYPES = new HashMap<>();

    /* Reads in the media-types. */
    static {
        final HashMap<String, org.basex.util.http.MediaType> cache = new HashMap<>();
        try {
            final String file = "/media-types.properties";
            final InputStream is = org.basex.util.http.MediaType.class.getResourceAsStream(file);
            if(is == null) {
                Util.errln(file + " not found.");
            } else {
                try(NewlineInput nli = new NewlineInput(is)) {
                    for(String line; (line = nli.readLine()) != null;) {
                        final int i = line.indexOf('=');
                        if(i == -1 || Strings.startsWith(line, '#')) continue;
                        final String suffix = line.substring(0, i), type = line.substring(i + 1);
                        final org.basex.util.http.MediaType mt = cache.computeIfAbsent(type, org.basex.util.http.MediaType::new);
                        TYPES.put(suffix, mt);
                    }
                }
            }
        } catch(final IOException ex) {
            Util.errln(ex);
        } catch(final Throwable ex) {
            Util.stack(ex);
        }
    }
}
