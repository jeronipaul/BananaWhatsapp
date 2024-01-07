package com.banana.bananawhatsapp.controladores;

import com.banana.bananawhatsapp.config.SpringConfig;
import com.banana.bananawhatsapp.exceptions.MensajeException;
import com.banana.bananawhatsapp.exceptions.UsuarioException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import com.banana.bananawhatsapp.modelos.Usuario;
import com.banana.bananawhatsapp.modelos.Mensaje;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.sql.SQLException;
import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringConfig.class})
@ActiveProfiles("prod")

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class ControladorMensajesTest {
    @Autowired
    ControladorUsuarios controladorUsuarios;
    @Autowired
    ControladorMensajes controladorMensajes;

    @Test
    @Order(1)
    void dadoRemitenteYDestinatarioYTextoValidos_cuandoEnviarMensaje_entoncesOK() {

        boolean envio = controladorMensajes.enviarMensaje(1, 2, "Hola, buenos días");

        assertThat(envio, is(true));

    }

    @Test
    @Order(2)
    void dadoRemitenteYDestinatarioYTextoNOValidos_cuandoEnviarMensaje_entoncesExcepcion() {

        assertThrows(MensajeException.class, () -> {
            controladorMensajes.enviarMensaje(1, 2, "");
        });

    }

    @Test
    @Order(3)
    void dadoRemitenteYDestinatarioValidos_cuandoMostrarChat_entoncesOK() {
        boolean isOk = controladorMensajes.mostrarChat(1,2);
        assertThat(isOk, is(true));
    }

    @Test
    @Order(4)
    void dadoRemitenteYDestinatarioNOValidos_cuandoMostrarChat_entoncesExcepcion() {
        assertThrows(UsuarioException.class, () -> {
            boolean isOk = controladorMensajes.mostrarChat(1,0);
        });
    }

    @Test
    @Order(5)
    void dadoRemitenteYDestinatarioValidos_cuandoEliminarChatConUsuario_entoncesOK() {
        boolean envio = controladorMensajes.eliminarChatConUsuario(1, 2);

        assertThat(envio, is(true));
    }

    @Test
    @Order(6)
    void dadoRemitenteYDestinatarioNOValidos_cuandoEliminarChatConUsuario_entoncesExcepcion() {
        assertThrows(UsuarioException.class, () -> {
            controladorMensajes.eliminarChatConUsuario(0, 2);
        });
    }
}