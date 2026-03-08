package com.chimera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point for the Chimera Control Plane backend.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ChimeraApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChimeraApplication.class, args);
    }
}
