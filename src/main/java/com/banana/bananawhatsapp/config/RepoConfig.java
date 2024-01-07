package com.banana.bananawhatsapp.config;

import com.banana.bananawhatsapp.persistencia.*;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@Configuration
//@ComponentScan(basePackages = {"com.banana.bananawhatsapp.persistencia"})
@PropertySource("classpath:application.properties")
public class RepoConfig {
    @Value("${db_url}")
    private String db_url;

    @Bean
    @Profile("prod")
    IUsuarioRepository crearUsuarioRepoJDBC() {
        UsuarioJDBCRepo repo = new UsuarioJDBCRepo();
        repo.setDb_url(db_url);
        return repo;
    }
    @Bean
    @Profile("prod")
    IMensajeRepository crearMensajeRepoJDBC() {
        MensajeJDBCRepo repo = new MensajeJDBCRepo();
        repo.setDb_url(db_url);
        return repo;
    }

    @Bean
    @Profile("dev")
    IUsuarioRepository crearUsuarioRepoInMemo() {
        UsuarioInMemoryRepo repo = new UsuarioInMemoryRepo();
        return repo;
    }
    @Bean
    @Profile("dev")
    IMensajeRepository crearMensajeRepoInMemo() {
        MensajeInMemoryRepo repo = new MensajeInMemoryRepo();
        return repo;
    }

}
