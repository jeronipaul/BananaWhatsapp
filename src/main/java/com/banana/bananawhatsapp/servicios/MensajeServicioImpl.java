package com.banana.bananawhatsapp.servicios;

import com.banana.bananawhatsapp.exceptions.MensajeException;
import com.banana.bananawhatsapp.exceptions.UsuarioException;
import com.banana.bananawhatsapp.modelos.Mensaje;
import com.banana.bananawhatsapp.modelos.Usuario;
import com.banana.bananawhatsapp.persistencia.IMensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class MensajeServicioImpl implements IServicioMensajeria  {
    @Autowired
    IMensajeRepository mensajeRepo;

    @Override
    public Mensaje enviarMensaje(Usuario remitente, Usuario destinatario, String texto) throws UsuarioException, MensajeException {
        Mensaje nuevoMensaje = new Mensaje(null, remitente, destinatario, texto, LocalDate.now());
        try {
            return mensajeRepo.crear(nuevoMensaje);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MensajeException("Error base de datos "+e.getMessage());
        }
    }

    @Override
    public List<Mensaje> mostrarChatConUsuario(Usuario remitente, Usuario destinatario) throws UsuarioException, MensajeException {
        //En obtener el destinatario es opcional, lo obligamos
        if (destinatario == null) {
            throw new UsuarioException("Falta destinatario");
        }

        try {
            return mensajeRepo.obtener(remitente, destinatario);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MensajeException("Error base de datos "+e.getMessage());
        }
    }

    @Override
    public boolean borrarChatConUsuario(Usuario remitente, Usuario destinatario) throws UsuarioException, MensajeException {
        //En borrarTodos el destinatario es opcional, lo obligamos
        if (destinatario == null) {
            throw new UsuarioException("Falta destinatario");
        }

        try {
            return mensajeRepo.borrarTodos(remitente, destinatario);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MensajeException("Error base de datos "+e.getMessage());
        }
    }
}
