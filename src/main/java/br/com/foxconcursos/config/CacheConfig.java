package br.com.foxconcursos.config;

import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    @Primary
    public javax.cache.CacheManager myCacheManager() {
        
        CachingProvider provider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = provider.getCacheManager();
        
        MutableConfiguration<Object, Object> configuration = new MutableConfiguration<>();
        configuration.setStoreByValue(false);
        configuration.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(
                new Duration(java.util.concurrent.TimeUnit.HOURS, 6)));

        cacheManager.createCache("simuladoCache", configuration);
        
        return cacheManager;
    }

}
