package com.chimera.persistence.postgres;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA configuration scanning domain models and repository interfaces.
 */
@Configuration
@EntityScan(basePackages = "com.chimera.domain.model")
@EnableJpaRepositories(basePackages = "com.chimera.domain.repository")
public class PostgresConfig {
}
