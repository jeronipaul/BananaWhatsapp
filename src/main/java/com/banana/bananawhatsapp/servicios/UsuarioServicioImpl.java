package com.banana.bananawhatsapp.servicios;

import com.banana.bananawhatsapp.exceptions.UsuarioException;
import com.banana.bananawhatsapp.modelos.Usuario;
import com.banana.bananawhatsapp.persistencia.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Set;

@Service
public class UsuarioServicioImpl implements IServicioUsuarios {
    @Autowired
    IUsuarioRepository usuarioRepo;

    @Override
    public Usuario crearUsuario(Usuario usuario) throws UsuarioException {
        try {
            usuario.valido();
            usuarioRepo.crear(usuario);
        } catch(UsuarioException e) {
            e.printStackTrace();
            throw e;
        } catch(SQLException e) {
            e.printStackTrace();
            throw new UsuarioException("Error en base de datos " + e.getMessage());
        }

        return usuario;
    }

    @Override
    public boolean borrarUsuario(Usuario usuario) throws UsuarioException {
        try {
            usuario.valido();
            usuarioRepo.borrar(usuario);
        } catch(UsuarioException e) {
            e.printStackTrace();
            throw e;
        } catch(SQLException e) {
            e.printStackTrace();
            throw new UsuarioException("Error en base de datos " + e.getMessage());
        }

        return true;
    }

    @Override
    public Usuario actualizarUsuario(Usuario usuario) throws UsuarioException {
        try {
            usuario.valido();
            usuarioRepo.actualizar(usuario);
        } catch(UsuarioException e) {
            e.printStackTrace();
            throw e;
        } catch(SQLException e) {
            e.printStackTrace();
            throw new UsuarioException("Error en base de datos " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public Set<Usuario> obtenerPosiblesDesinatarios(Usuario usuario, int max) throws UsuarioException {
        return null;
    }
}
