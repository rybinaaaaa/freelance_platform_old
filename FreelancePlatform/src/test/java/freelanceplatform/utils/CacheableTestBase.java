package freelanceplatform.utils;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.util.Objects;

public abstract class CacheableTestBase extends IntegrationTestBase{

    @Autowired
    protected CacheManager cacheManager;

    protected abstract String getCacheName();

    protected String cacheName = getCacheName();

    @AfterEach
    void clearCache() {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
    }
}
