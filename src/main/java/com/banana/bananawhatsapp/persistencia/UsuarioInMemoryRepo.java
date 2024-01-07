package com.banana.bananawhatsapp.persistencia;

import com.banana.bananawhatsapp.exceptions.UsuarioException;
import com.banana.bananawhatsapp.modelos.Usuario;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class UsuarioInMemoryRepo implements IUsuarioRepository {
    Set<Usuario> usuarios = new HashSet<>();
    private Integer num = 0;

    @Override
    public Usuario crear(Usuario usuario) throws SQLException {
        usuario.valido();
        usuario.setId(++num);
        usuarios.add(usuario);

        return usuario;
    }

    @Override
    public Usuario actualizar(Usuario usuario) throws SQLException {
        //Validación usuario recibido
        usuario.valido();
        if (usuario.getId() <= 0 || usuario.getId() > num) {
            throw new SQLException();
        }

        boolean existe = false;
        for (Usuario u : usuarios) {
            if (u.getId() == usuario.getId()) {
                existe = true;
                //Conservar fecha de alta original
                usuario.setAlta(u.getAlta());
                //Eliminar
                usuarios.remove(u);
                //Añadir el nuevo
                usuarios.add(usuario);
                break;
            }
        }

        if(existe == false) {
            throw new SQLException();
        }

        return usuario;
    }

    @Override
    public boolean borrar(Usuario usuario) throws SQLException {
        if (usuario == null || usuario.getId() <= 0) {
            throw new UsuarioException("Usuario no válido");
        }
        if (usuario.getId() > num) {
            throw new SQLException();
        }

        boolean existe = false;
        for (Usuario u : usuarios) {
            if (u.getId() == usuario.getId()) {
                existe = true;
                //Eliminar
                usuarios.remove(u);
                break;
            }
        }

        if(existe == false) {
            throw new SQLException();
        }

        return existe;
    }

    @Override
    public Set<Usuario> obtenerPosiblesDestinatarios(Integer id, Integer max) throws SQLException {
        return null;
    }
}
