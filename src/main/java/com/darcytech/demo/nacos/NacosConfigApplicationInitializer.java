package com.darcytech.demo.nacos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration;
import org.springframework.cloud.bootstrap.encrypt.EnvironmentDecryptApplicationInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NacosConfigApplicationInitializer extends EnvironmentDecryptApplicationInitializer {

    public NacosConfigApplicationInitializer(TextEncryptor encryptor) {
        super(encryptor);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        for (PropertySource<?> propertySource : mutablePropertySources) {
            if (PropertySourceBootstrapConfiguration.BOOTSTRAP_PROPERTY_SOURCE_NAME.equals(propertySource.getName())) {
                CompositePropertySource compositePropertySource = (CompositePropertySource) propertySource;
                compositePropertySource.getPropertySources().forEach(cp -> {
                    Collection<PropertySource<?>> nacosPropertySources = ((CompositePropertySource) cp).getPropertySources();
                    nacosPropertySources.forEach(ncp -> {
                        Map<String, String> propsMap = (Map<String, String>) ncp.getSource();
                        Map<String, String> decryptPropsMap = new HashMap<>();
                        propsMap.forEach((k, v) -> {
                            if (NacosDecryptUtils.isEncrypt(v)) {
                                decryptPropsMap.put(k, NacosDecryptUtils.decrypt(v));
                            }
                        });
                        decryptPropsMap.forEach(propsMap::put);
                    });
                });
            }

        }
    }

    @Override
    public int getOrder() {
        return super.getOrder() - 1;
    }

}