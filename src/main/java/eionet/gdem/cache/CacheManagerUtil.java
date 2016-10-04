package eionet.gdem.cache;

import eionet.gdem.Properties;
import eionet.gdem.dto.DDDatasetTable;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.*;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author George Sofianos
 */
public final class CacheManagerUtil {

    private CacheManagerUtil() {
        // do nothing
    }
    /**
     * Application (main) cache name.
     */
    public static final String APPLICATION_CACHE = "ApplicationCache";

    /** Data Dictionary tables data cache name. */
    private static final String DD_TABLES_CACHE = "ddTables";

    private static CacheManager cacheManager;

    public static CacheManager getCacheManager() {
        return cacheManager;
    }

    public static void updateDDTablesCache(final List<DDDatasetTable> ddTables) {
        // XXX: This fills the cache without reason.
        cacheManager.getCache(APPLICATION_CACHE).put(new Element(DD_TABLES_CACHE, ddTables));
    }

    public static List<DDDatasetTable> getDDTables() {
        Element element = cacheManager.getCache(APPLICATION_CACHE) != null ? cacheManager.getCache(APPLICATION_CACHE).get(DD_TABLES_CACHE) : null;
        return element == null || element.getValue() == null ? Collections.EMPTY_LIST : (List<DDDatasetTable>) element.getValue();
    }

    public static Cache getHttpCache() {
        return cacheManager.getCache("http-cache");
    }

    private static void initializeCacheManager() {
        Configuration cacheManagerConfig = new Configuration()
                .diskStore(new DiskStoreConfiguration()
                .path(Properties.appRootFolder + "/tmp/"));
        cacheManager = new CacheManager(cacheManagerConfig);
        Cache appCache = new Cache(new CacheConfiguration(APPLICATION_CACHE, 10000)
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                .eternal(true));
        cacheManager.addCache(appCache);

        Cache httpCache = new Cache(new CacheConfiguration()
                .name("http-cache")
                .maxBytesLocalDisk(1000, MemoryUnit.MEGABYTES)
                .maxEntriesLocalDisk(100)
                .maxEntriesLocalHeap(1)
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                .eternal(false)
                .diskExpiryThreadIntervalSeconds(120)
                .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP)));
        cacheManager.addCache(httpCache);
    }

    public static void create() {
        initializeCacheManager();
    }
    public static void shutdown() {
        cacheManager.shutdown();
    }
}
