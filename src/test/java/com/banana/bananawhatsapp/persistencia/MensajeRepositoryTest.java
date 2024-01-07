package com.banana.bananawhatsapp.persistencia;

import com.banana.bananawhatsapp.config.SpringConfig;
import com.banana.bananawhatsapp.exceptions.MensajeException;
import com.banana.bananawhatsapp.exceptions.UsuarioException;
import com.banana.bananawhatsapp.modelos.Mensaje;
import com.banana.bananawhatsapp.modelos.Usuario;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfig.class})
@ActiveProfiles("prod")

class MensajeRepositoryTest {
    @Autowired
    IUsuarioRepository userRepo;
    @Autowired
    IMensajeRepository mensRepo;

    @Test
    void dadoUnMensajeValido_cuandoCrear_entoncesMensajeValido() throws SQLException {
        //Creamos remitente y destinatario para la prueba y luego lo borramos
        Usuario remitente = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        userRepo.crear(remitente);
        Usuario destinatario = new Usuario(null, "Elisa", "e@e.com", LocalDate.now(), true);
        userRepo.crear(destinatario);

        Mensaje nuevoMensaje = new Mensaje(null,remitente,destinatario,"Buenos días", LocalDate.now());
        mensRepo.crear(nuevoMensaje);

        assertThat(nuevoMensaje, notNullValue());
        assertThat(nuevoMensaje.getId(), greaterThan(0));

        //Borramos para no llenar la base de datos "prod" de basura
        //Al borrar el remitente se borran sus mensajes
        userRepo.borrar(remitente);
        userRepo.borrar(destinatario);
    }

    @Test
    void dadoUnMensajeNOValido_cuandoCrear_entoncesExcepcion() throws SQLException {
        Usuario remitente = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        userRepo.crear(remitente);

        Mensaje nuevoMensaje = new Mensaje(null, remitente, null,"Buenos días", LocalDate.now());

        assertThrows(MensajeException.class, () -> {
            mensRepo.crear(nuevoMensaje);
        });

        //Borramos para no llenar la base de datos "prod" de basura
        //Al borrar el remitente se borran sus mensajes
        userRepo.borrar(remitente);
    }

    @Test
    void dadoUnUsuarioValido_cuandoObtener_entoncesListaMensajes() throws SQLException {
        //Creamos usuarios para la prueba
        Usuario remitente = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        userRepo.crear(remitente);
        Usuario destinatario = new Usuario(null, "Elisa", "e@e.com", LocalDate.now(), true);
        userRepo.crear(destinatario);
        //Creamos mensajes para la prueba
        Mensaje nuevoMensaje1 = new Mensaje(null,remitente,destinatario,"Buenos días", LocalDate.now());
        mensRepo.crear(nuevoMensaje1);
        Mensaje nuevoMensaje2 = new Mensaje(null,remitente,destinatario,"¡Feliz 2024!", LocalDate.now());
        mensRepo.crear(nuevoMensaje2);
        Mensaje nuevoMensaje3 = new Mensaje(null,remitente,destinatario,"¿Qué tal las fiestas?", LocalDate.now());
        mensRepo.crear(nuevoMensaje3);

        // Prueba obtención mensajes
        List<Mensaje> mensajes = mensRepo.obtener(remitente, destinatario);

        assertThat(mensajes.size(), is(3));
        assertThat(mensajes.get(1).getCuerpo(), is("¡Feliz 2024!"));
        assertThat(mensajes.get(0).getRemitente().getNombre(), is("Jeroni"));
        assertThat(mensajes.get(2).getDestinatario().getEmail(), is("e@e.com"));

        //Borramos para no llenar la base de datos "prod" de basura
        //Al borrar el remitente se borran sus mensajes
        userRepo.borrar(remitente);
        userRepo.borrar(destinatario);
    }

    @Test
    void dadoUnUsuarioNOValido_cuandoObtener_entoncesExcepcion() throws SQLException {
        Usuario remitente = new Usuario(0, "Jeroni", "j@j.com", LocalDate.now(), true);

        assertThrows(UsuarioException.class, () -> {
            List<Mensaje> mensajes = mensRepo.obtener(remitente, null);
        });
    }

    @Test
    void dadoUnUsuarioValido_cuandoBorrarTodos_entoncesOK() throws SQLException {
        //Creamos remitente y destinatario para la prueba y luego lo borramos
        Usuario remitente = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        userRepo.crear(remitente);
        Usuario destinatario = new Usuario(null, "Elisa", "e@e.com", LocalDate.now(), true);
        userRepo.crear(destinatario);

        Mensaje nuevoMensaje = new Mensaje(null,remitente,destinatario,"Buenos días", LocalDate.now());
        mensRepo.crear(nuevoMensaje);

        boolean borrado = mensRepo.borrarTodos(remitente, destinatario);
        assertThat(borrado, is(true));

        //Borramos para no llenar la base de datos "prod" de basura
        userRepo.borrar(remitente);
        userRepo.borrar(destinatario);
    }

    @Test
    void dadoUnUsuarioNOValido_cuandoBorrarTodos_entoncesExcepcion() {
        Usuario remitente = new Usuario(0, "Jeroni", "j@j.com", LocalDate.now(), true);

        assertThrows(UsuarioException.class, () -> {
            mensRepo.borrarTodos(remitente, null);
        });
    }

}