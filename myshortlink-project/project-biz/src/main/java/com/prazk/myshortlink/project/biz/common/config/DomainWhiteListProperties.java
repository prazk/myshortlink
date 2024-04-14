package com.prazk.myshortlink.project.biz.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "project.goto-domain.white-list")
public class DomainWhiteListProperties {
    private Boolean enabled;
    private String[] domains;
}
