package com.glassait.equipment_tanks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.datasource")
public record DataSourceConfigProperties(String url, String username, String password) {
}
