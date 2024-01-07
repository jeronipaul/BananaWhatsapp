package com.banana.bananawhatsapp.controladores;

import com.banana.bananawhatsapp.exceptions.UsuarioException;
import com.banana.bananawhatsapp.modelos.Usuario;

import com.banana.bananawhatsapp.config.SpringConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfig.class})
@ActiveProfiles("prod")

class ControladorUsuariosTest {
    @Autowired
    ControladorUsuarios controladorUsuarios;

    @Test
    void dadoUsuarioValido_cuandoAlta_entoncesUsuarioValido() {
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        controladorUsuarios.alta(nuevoUsuario);

        assertThat(nuevoUsuario, notNullValue());
        assertThat(nuevoUsuario.getId(), greaterThan(0));

        //Borramos para no llenar la base de datos "prod" de basura
        controladorUsuarios.baja(nuevoUsuario);
    }

    @Test
    void dadoUsuarioNOValido_cuandoAlta_entoncesExcepcion() {
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "jj.com", LocalDate.now(), true);

        assertThrows(UsuarioException.class, () -> {
            controladorUsuarios.alta(nuevoUsuario);
        });
    }

    @Test
    void dadoUsuarioValido_cuandoActualizar_entoncesUsuarioValido() {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        controladorUsuarios.alta(nuevoUsuario);

        //Prueba modificación
        nuevoUsuario.setNombre("Jeronimo");
        nuevoUsuario.setEmail("jero@gmail.com");

        Usuario usuarioActualizado = controladorUsuarios.actualizar(nuevoUsuario);

        assertThat(usuarioActualizado, notNullValue());
        assertThat(usuarioActualizado.getNombre(), is("Jeronimo"));

        //Borramos para no llenar la base de datos "prod" de basura
        controladorUsuarios.baja(usuarioActualizado);
    }

    @Test
    void dadoUsuarioNOValido_cuandoActualizar_entoncesExcepcion() {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        controladorUsuarios.alta(nuevoUsuario);

        //Prueba modificación
        nuevoUsuario.setNombre("Jeronimo");
        nuevoUsuario.setEmail("jerogmail.com");

        assertThrows(UsuarioException.class, () -> {
            controladorUsuarios.actualizar(nuevoUsuario);
        });

        //Borramos para no llenar la base de datos "prod" de basura
        nuevoUsuario.setEmail("jero@gmail.com");
        controladorUsuarios.baja(nuevoUsuario);
    }

    @Test
    void dadoUsuarioValido_cuandoBaja_entoncesUsuarioValido() {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        controladorUsuarios.alta(nuevoUsuario);

        assertThat(nuevoUsuario, notNullValue());
        assertThat(nuevoUsuario.getId(), greaterThan(0));

        //Prueba de borrado
        controladorUsuarios.baja(nuevoUsuario);
    }

    @Test
    void dadoUsuarioNOValido_cuandoBaja_entoncesExcepcion() {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);

        nuevoUsuario.setId(-1);

        //Prueba de borrado con Id inexistente
        assertThrows(UsuarioException.class, () -> {
            controladorUsuarios.baja(nuevoUsuario);
        });
    }
}