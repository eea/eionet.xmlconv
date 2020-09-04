package org.basex.build.xml;

import org.basex.util.Reflect;
import org.xml.sax.EntityResolver;

import javax.xml.transform.URIResolver;
import java.lang.reflect.Constructor;

import static org.basex.util.Reflect.*;

public final class CatalogWrapper {
    /** Package declaration for CatalogManager. */
    private static final Class<?> MANAGER;
    /** Package declaration for CatalogResolver constructor. */
    private static final Constructor<?> RESOLVER;

    static {
        // try to locate catalog manager from xml-resolver-1.2.jar library
        Class<?> manager = find("org.apache.xml.resolver.CatalogManager"), resolver;
        if(manager != null) {
            resolver = find("org.apache.xml.resolver.tools.CatalogResolver");
        } else {
            // try to resort to internal catalog manager
            manager = find("com.sun.org.apache.xml.internal.resolver.CatalogManager");
            resolver = find("com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver");
        }
        MANAGER = manager;
        RESOLVER = find(resolver, manager);
    }

    /** Instance of catalog manager. */
    private final Object cm = Reflect.get(MANAGER);

    /**
     * Hidden constructor.
     * @param paths semicolon-separated list of catalog files
     */
    private CatalogWrapper(final String paths) {
        if(System.getProperty("xml.catalog.ignoreMissing") == null) {
            invoke(method(MANAGER, "setIgnoreMissingProperties", boolean.class), cm, true);
        }
        invoke(method(MANAGER, "setCatalogFiles", String.class), cm, paths);
    }

    /**
     * Returns an instance of the catalog wrapper.
     * @param paths semicolon-separated list of catalog files
     * @return instance, or {@code null} if no catalog manager is available or if the list is empty
     */
    public static CatalogWrapper get(final String paths) {
        return available() && !paths.isEmpty() ? new CatalogWrapper(paths) : null;
    }

    /**
     * Checks if the catalog manager is available.
     * @return result of check
     */
    public static boolean available() {
        return MANAGER != null;
    }

    /**
     * Returns a URI resolver.
     * @return URI resolver
     */
    public URIResolver getURIResolver() {
        return (URIResolver) Reflect.get(RESOLVER, cm);
    }

    /**
     * Returns an entity resolver.
     * @return entity resolver
     */
    public EntityResolver getEntityResolver() {
        return (EntityResolver) Reflect.get(RESOLVER, cm);
    }
}
