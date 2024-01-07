package com.banana.bananawhatsapp.persistencia;

import com.banana.bananawhatsapp.exceptions.UsuarioException;
import com.banana.bananawhatsapp.modelos.Usuario;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UsuarioJDBCRepo implements IUsuarioRepository {
    private String db_url;

    @Override
    public Usuario crear(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario values (NULL,?,?,?,?)";

        try (
                Connection conn = DriverManager.getConnection(db_url);
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            usuario.valido();

            stmt.setInt(1, usuario.isActivo() ? 1 : 0);
            stmt.setDate(2, Date.valueOf(usuario.getAlta()));
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getNombre());

            int rows = stmt.executeUpdate();

            ResultSet genKeys = stmt.getGeneratedKeys();
            if (genKeys.next()) {
                usuario.setId(genKeys.getInt(1));
            } else {
                throw new SQLException("Usuario creado erroneamente!!!");
            }

        } catch (UsuarioException e) {
            e.printStackTrace();
            throw new UsuarioException("Datos usuario incorrectos");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }

        return usuario;

    }

    @Override
    public Usuario actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario set activo=?, email=?, nombre=? WHERE id=?";

        try (
                Connection conn = DriverManager.getConnection(db_url);
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            usuario.valido();

            stmt.setInt(1, usuario.isActivo() ? 1 : 0);
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getNombre());
            stmt.setInt(4, usuario.getId());

            int rows = stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return usuario;
    }

    @Override
    public boolean borrar(Usuario usuario) throws SQLException {
        Connection conn = null;
        String sqlMensajes = "DELETE FROM mensaje WHERE from_user=?";
        String sqlUsuario = "DELETE FROM usuario WHERE id=?";

        try {
            if (usuario == null || usuario.getId() <= 0) {
                throw new UsuarioException("Usuario no válido");
            }

            conn = DriverManager.getConnection(db_url);
            conn.setAutoCommit(false);

            //Borrado de mensajes del usuario
            PreparedStatement stmt = conn.prepareStatement(sqlMensajes);
            stmt.setInt(1, usuario.getId());
            stmt.executeUpdate();
            stmt.close();

            //Borrado del usuario
            stmt = conn.prepareStatement(sqlUsuario);
            stmt.setInt(1, usuario.getId());
            int rows = stmt.executeUpdate();

            System.out.println(rows);
            if(rows<=0) {
                throw new SQLException();
            }
            stmt.close();

            System.out.println("Transaccion exitosa!!");
            conn.commit();

        } catch (SQLException e) {
            System.out.println("Transaccion rollback!!");
            conn.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }

        return true;
    }

    @Override
    public Set<Usuario> obtenerPosiblesDestinatarios(Integer id, Integer max) throws SQLException {
        return null;
    }

}
