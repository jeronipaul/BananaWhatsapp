package com.banana.bananawhatsapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RepoConfig.class)
@ComponentScan(basePackages = {"com.banana.bananawhatsapp.servicios","com.banana.bananawhatsapp.controladores","com.banana.bananawhatsapp.persistencia"})
public class SpringConfig {
}
