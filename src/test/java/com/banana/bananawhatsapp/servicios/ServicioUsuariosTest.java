package com.banana.bananawhatsapp.servicios;

import com.banana.bananawhatsapp.config.SpringConfig;
import com.banana.bananawhatsapp.exceptions.UsuarioException;
import com.banana.bananawhatsapp.modelos.Usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.*;

import java.sql.SQLException;
import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfig.class})
@ActiveProfiles("prod")

class ServicioUsuariosTest {

    @Autowired
    IServicioUsuarios servicio;

    @Test
    void dadoUnUsuarioValido_cuandoCrearUsuario_entoncesUsuarioValido() {
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        servicio.crearUsuario(nuevoUsuario);

        assertThat(nuevoUsuario, notNullValue());
        assertThat(nuevoUsuario.getId(), greaterThan(0));

        //Borramos para no llenar la base de datos "prod" de basura
        servicio.borrarUsuario(nuevoUsuario);
    }

    @Test
    void dadoUnUsuarioNOValido_cuandoCrearUsuario_entoncesExcepcion() {
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "jj.com", LocalDate.now(), true);

        assertThrows(UsuarioException.class, () -> {
            servicio.crearUsuario(nuevoUsuario);
        });
    }

    @Test
    void dadoUnUsuarioValido_cuandoBorrarUsuario_entoncesUsuarioValido() {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        servicio.crearUsuario(nuevoUsuario);

        assertThat(nuevoUsuario, notNullValue());
        assertThat(nuevoUsuario.getId(), greaterThan(0));

        //Prueba de borrado
        servicio.borrarUsuario(nuevoUsuario);
    }

    @Test
    void dadoUnUsuarioNOValido_cuandoBorrarUsuario_entoncesExcepcion() {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);

        nuevoUsuario.setId(-1);

        //Prueba de borrado con Id inexistente
        assertThrows(UsuarioException.class, () -> {
            servicio.borrarUsuario(nuevoUsuario);
        });
    }

    @Test
    void dadoUnUsuarioValido_cuandoActualizarUsuario_entoncesUsuarioValido() {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        servicio.crearUsuario(nuevoUsuario);

        //Prueba modificación
        nuevoUsuario.setNombre("Jeronimo");
        nuevoUsuario.setEmail("jero@gmail.com");

        Usuario usuarioActualizado = servicio.actualizarUsuario(nuevoUsuario);

        assertThat(usuarioActualizado, notNullValue());
        assertThat(usuarioActualizado.getNombre(), is("Jeronimo"));

        //Borramos para no llenar la base de datos "prod" de basura
        servicio.borrarUsuario(usuarioActualizado);
    }

    @Test
    void dadoUnUsuarioNOValido_cuandoActualizarUsuario_entoncesExcepcion() {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        servicio.crearUsuario(nuevoUsuario);

        //Prueba modificación
        nuevoUsuario.setNombre("Jeronimo");
        nuevoUsuario.setEmail("jerogmail.com");

        assertThrows(UsuarioException.class, () -> {
            servicio.actualizarUsuario(nuevoUsuario);
        });

        //Borramos para no llenar la base de datos "prod" de basura
        nuevoUsuario.setEmail("jero@gmail.com");
        servicio.borrarUsuario(nuevoUsuario);
    }

    @Test
    void dadoUnUsuarioValido_cuandoObtenerPosiblesDesinatarios_entoncesUsuariosValidos() {
    }

    @Test
    void dadoUnUsuarioNOValido_cuandoObtenerPosiblesDesinatarios_entoncesExcepcion() {
    }
}