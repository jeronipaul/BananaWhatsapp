package com.banana.bananawhatsapp.persistencia;

import com.banana.bananawhatsapp.exceptions.UsuarioException;
import com.banana.bananawhatsapp.modelos.Mensaje;
import com.banana.bananawhatsapp.modelos.Usuario;

import java.sql.SQLException;
import java.util.*;

public class MensajeInMemoryRepo implements IMensajeRepository {
    //LinkedHashSet -> conserva el orden de inserción
    Set<Mensaje> mensajes = new LinkedHashSet<>();
    private Integer num = 0;

    @Override
    public Mensaje crear(Mensaje mensaje) throws SQLException {
        mensaje.valido();
        mensaje.setId(++num);
        mensajes.add(mensaje);

        return mensaje;
    }

    @Override
    public List<Mensaje> obtener(Usuario remitente, Usuario destinatario) throws SQLException {
        //remitente.valido();
        //if (destinatario != null) destinatario.valido();
        //El Controlador solo informa el id
        if (remitente == null || remitente.getId() <= 0) {
            throw new UsuarioException("Remitente no válido");
        }
        if (destinatario != null) {
            if (destinatario.getId() <= 0) {
                throw new UsuarioException("Destinatario no válido");
            }
        }

        List<Mensaje> resultado = new ArrayList<>();

        for (Mensaje m : mensajes) {
            if (m.getRemitente().getId() == remitente.getId()
             && (destinatario == null || m.getDestinatario().getId() == destinatario.getId())) {
                resultado.add(m);
            }
        }

        return resultado;
    }

    @Override
    public boolean borrarTodos(Usuario remitente, Usuario destinatario) throws SQLException {
        //Si destinatario es nulo buscamos solo por remitente
        //remitente.valido();
        //if (destinatario != null) destinatario.valido();
        //El Controlador solo informa el id
        if (remitente == null || remitente.getId() <= 0) {
            throw new UsuarioException("Remitente no válido");
        }
        if (destinatario != null) {
            if (destinatario.getId() <= 0) {
                throw new UsuarioException("Destinatario no válido");
            }
        }

        boolean existe = false;
        for (Mensaje m : mensajes) {
            if (m.getRemitente().getId() == remitente.getId()
             && (destinatario == null || m.getDestinatario().getId() == destinatario.getId())) {
                existe = true;
                //Eliminar
                mensajes.remove(m);
            }
        }

        return existe;
    }

}
