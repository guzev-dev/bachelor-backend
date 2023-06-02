package bachelor.proj.charity.shared.cfg;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean("cacheManager")
    @Primary
    public CacheManager cacheManager() {
        final Caffeine caffeine = Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .maximumSize(8);

        final CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        cacheManager.setAllowNullValues(false);

        return cacheManager;
    }

    @Bean("userCacheManager")
    public CacheManager userCacheManager() {
        final Caffeine caffeine = Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .maximumSize(10);

        final CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        cacheManager.setAllowNullValues(false);

        return cacheManager;
    }

}
