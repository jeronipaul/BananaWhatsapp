package com.banana.bananawhatsapp.persistencia;

import com.banana.bananawhatsapp.config.SpringConfig;
import com.banana.bananawhatsapp.exceptions.UsuarioException;
import com.banana.bananawhatsapp.modelos.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfig.class})
@ActiveProfiles("prod")

class UsuarioRepositoryTest {
    @Autowired
    IUsuarioRepository repo;
    @Autowired
    UsuarioRepoJDBCTest repoTest;
    @Autowired
    Environment environment;

    @Test
    void dadoUnUsuarioValido_cuandoCrear_entoncesUsuarioValido() throws Exception {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        repo.crear(nuevoUsuario);

        assertThat(nuevoUsuario, notNullValue());
        assertThat(nuevoUsuario.getId(), greaterThan(0));

        //Ojo, solo para perfil "prod"
        if (environment.matchesProfiles("prod")) {
            Usuario verificarBD = repoTest.getUsuarioById(nuevoUsuario.getId());
            assertThat(nuevoUsuario.toString(), equalTo(verificarBD.toString()));
        }

        //Borramos para no llenar la base de datos "prod" de basura
        repo.borrar(nuevoUsuario);
    }

    @Test
    void dadoUnUsuarioNOValido_cuandoCrear_entoncesExcepcion() {
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "jj.com", LocalDate.now(), true);

        assertThrows(UsuarioException.class, () -> {
            repo.crear(nuevoUsuario);
        });
    }

    @Test
    void dadoUnUsuarioValido_cuandoActualizar_entoncesUsuarioValido() throws SQLException {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        repo.crear(nuevoUsuario);

        //Prueba modificación
        nuevoUsuario.setNombre("Jeronimo");
        nuevoUsuario.setEmail("jero@gmail.com");

        Usuario usuarioActualizado = repo.actualizar(nuevoUsuario);

        assertThat(usuarioActualizado, notNullValue());
        assertThat(usuarioActualizado.getNombre(), is("Jeronimo"));
        assertThat(usuarioActualizado.getEmail(), is("jero@gmail.com"));

        //Ojo, solo para perfil "prod"
        if (environment.matchesProfiles("prod")) {
            Usuario verificarBD = repoTest.getUsuarioById(nuevoUsuario.getId());
            assertThat(nuevoUsuario.getNombre(), equalTo(verificarBD.getNombre()));
            assertThat(nuevoUsuario.getEmail(), equalTo(verificarBD.getEmail()));
            assertThat(nuevoUsuario.isActivo(), is(verificarBD.isActivo()));
        }

        //Borramos para no llenar la base de datos "prod" de basura
        repo.borrar(usuarioActualizado);
    }

    @Test
    void dadoUnUsuarioNOValido_cuandoActualizar_entoncesExcepcion() throws SQLException {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        repo.crear(nuevoUsuario);

        //Prueba modificación
        nuevoUsuario.setNombre("Jeronimo");
        nuevoUsuario.setEmail("jerogmail.com");

        assertThrows(UsuarioException.class, () -> {
            repo.actualizar(nuevoUsuario);
        });

        //Borramos para no llenar la base de datos "prod" de basura
        repo.borrar(nuevoUsuario);
    }

    @Test
    void dadoUnUsuarioValido_cuandoBorrar_entoncesOK() throws SQLException {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        repo.crear(nuevoUsuario);

        assertThat(nuevoUsuario, notNullValue());
        assertThat(nuevoUsuario.getId(), greaterThan(0));

        //Prueba de borrado
        repo.borrar(nuevoUsuario);

        //Ojo, solo para perfil "prod"
        if (environment.matchesProfiles("prod")) {
            assertThrows(SQLException.class, () -> {
                repoTest.getUsuarioById(nuevoUsuario.getId());
            });
        }
    }

    @Test
    void dadoUnUsuarioNOValido_cuandoBorrar_entoncesExcepcion() throws SQLException {
        //Creamos usuario para la prueba y luego lo borramos
        Usuario nuevoUsuario = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);

        nuevoUsuario.setId(-1);

        //Prueba de borrado con Id inexistente
        assertThrows(UsuarioException.class, () -> {
            repo.borrar(nuevoUsuario);
        });
    }

    @Test
    void dadoUnUsuarioValido_cuandoObtenerPosiblesDestinatarios_entoncesLista() {
    }

    @Test
    void dadoUnUsuarioNOValido_cuandoObtenerPosiblesDestinatarios_entoncesExcepcion() {
    }

}
