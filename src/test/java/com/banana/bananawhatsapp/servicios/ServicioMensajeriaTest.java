package com.banana.bananawhatsapp.servicios;

import com.banana.bananawhatsapp.config.SpringConfig;
import com.banana.bananawhatsapp.exceptions.MensajeException;
import com.banana.bananawhatsapp.exceptions.UsuarioException;
import org.junit.jupiter.api.Test;
import com.banana.bananawhatsapp.modelos.Usuario;
import com.banana.bananawhatsapp.modelos.Mensaje;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfig.class})
@ActiveProfiles("prod")

class ServicioMensajeriaTest {

    @Autowired
    IServicioUsuarios usuarios;
    @Autowired
    IServicioMensajeria servicio;

    @Test
    void dadoRemitenteYDestinatarioYTextoValido_cuandoEnviarMensaje_entoncesMensajeValido() {
        Usuario remitente = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        usuarios.crearUsuario(remitente);
        Usuario destinatario = new Usuario(null, "Elisa", "e@e.com", LocalDate.now(), true);
        usuarios.crearUsuario(destinatario);

        Mensaje nuevoMensaje = servicio.enviarMensaje(remitente, destinatario, "Hola, buenos días");

        assertThat(nuevoMensaje, notNullValue());
        assertThat(nuevoMensaje.getId(), greaterThan(0));

        //Borramos para no llenar la base de datos "prod" de basura
        //Al borrar el remitente se borran sus mensajes
        usuarios.borrarUsuario(remitente);
        usuarios.borrarUsuario(destinatario);
    }

    @Test
    void dadoRemitenteYDestinatarioYTextoNOValido_cuandoEnviarMensaje_entoncesExcepcion() {
        Usuario remitente = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        usuarios.crearUsuario(remitente);
        Usuario destinatario = new Usuario(null, "Elisa", "e@e.com", LocalDate.now(), true);
        usuarios.crearUsuario(destinatario);

        assertThrows(MensajeException.class, () -> {
            servicio.enviarMensaje(remitente, destinatario, "");
        });

        //Borramos para no llenar la base de datos "prod" de basura
        //Al borrar el remitente se borran sus mensajes
        usuarios.borrarUsuario(remitente);
        usuarios.borrarUsuario(destinatario);
    }

    @Test
    void dadoRemitenteYDestinatarioValido_cuandoMostrarChatConUsuario_entoncesListaMensajes() {
        //Creamos usuarios para la prueba
        Usuario remitente = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        usuarios.crearUsuario(remitente);
        Usuario destinatario = new Usuario(null, "Elisa", "e@e.com", LocalDate.now(), true);
        usuarios.crearUsuario(destinatario);
        //Creamos mensajes para la prueba
        servicio.enviarMensaje(remitente, destinatario, "Buenos días");
        servicio.enviarMensaje(remitente, destinatario, "¡Feliz 2024!");
        servicio.enviarMensaje(remitente, destinatario, "¿Qué tal las fiestas?");

        // Prueba obtención mensajes
        List<Mensaje> mensajes = servicio.mostrarChatConUsuario(remitente, destinatario);

        assertThat(mensajes.size(), is(3));
        assertThat(mensajes.get(1).getCuerpo(), is("¡Feliz 2024!"));
        assertThat(mensajes.get(0).getRemitente().getNombre(), is("Jeroni"));
        assertThat(mensajes.get(2).getDestinatario().getEmail(), is("e@e.com"));

        //Borramos para no llenar la base de datos "prod" de basura
        //Al borrar el remitente se borran sus mensajes
        usuarios.borrarUsuario(remitente);
        usuarios.borrarUsuario(destinatario);
    }

    @Test
    void dadoRemitenteYDestinatarioNOValido_cuandoMostrarChatConUsuario_entoncesExcepcion() {
        Usuario remitente = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);

        assertThrows(UsuarioException.class, () -> {
            servicio.mostrarChatConUsuario(remitente, null);
        });
    }

    @Test
    void dadoRemitenteYDestinatarioValido_cuandoBorrarChatConUsuario_entoncesOK() {
        //Creamos usuario y mensaje para la prueba y luego lo borramos
        Usuario remitente = new Usuario(null, "Jeroni", "j@j.com", LocalDate.now(), true);
        usuarios.crearUsuario(remitente);
        Usuario destinatario = new Usuario(null, "Elisa", "e@e.com", LocalDate.now(), true);
        usuarios.crearUsuario(destinatario);

        Mensaje nuevoMensaje = servicio.enviarMensaje(remitente, destinatario, "Hola, buenos días");

        //Prueba borrado
        boolean borrado = servicio.borrarChatConUsuario(remitente, destinatario);

        assertThat(borrado, is(true));

        //Borramos para no llenar la base de datos "prod" de basura
        //Al borrar el remitente se borran sus mensajes
        usuarios.borrarUsuario(remitente);
        usuarios.borrarUsuario(destinatario);
    }

    @Test
    void dadoRemitenteYDestinatarioNOValido_cuandoBorrarChatConUsuario_entoncesExcepcion() {
        Usuario remitente = new Usuario(1, "Jeroni", "j@j.com", LocalDate.now(), true);

        //Prueba borrado con destinatario inexistente
        assertThrows(UsuarioException.class, () -> {
            boolean borrado = servicio.borrarChatConUsuario(remitente, null);
        });
    }
}