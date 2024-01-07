package com.banana.bananawhatsapp.persistencia;

import com.banana.bananawhatsapp.exceptions.MensajeException;
import com.banana.bananawhatsapp.exceptions.UsuarioException;
import com.banana.bananawhatsapp.modelos.Mensaje;
import com.banana.bananawhatsapp.modelos.Usuario;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Set;
import java.sql.*;
import java.util.List;

@Getter
@Setter
public class MensajeJDBCRepo implements IMensajeRepository {
    private String db_url;

    @Override
    public Mensaje crear(Mensaje mensaje) throws SQLException {
        String sql = "INSERT INTO mensaje values (NULL,?,?,?,?)";

        try (
                Connection conn = DriverManager.getConnection(db_url);
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            mensaje.valido();
            //mensaje.getRemitente().valido();
            //mensaje.getDestinatario().valido();

            stmt.setString(1, mensaje.getCuerpo());
            stmt.setDate(2, Date.valueOf(mensaje.getFecha()));
            stmt.setInt(3, mensaje.getRemitente().getId());
            stmt.setInt(4, mensaje.getDestinatario().getId());

            int rows = stmt.executeUpdate();

            ResultSet genKeys = stmt.getGeneratedKeys();
            if (genKeys.next()) {
                mensaje.setId(genKeys.getInt(1));
            } else {
                throw new SQLException("Mensaje creado erroneamente!!!");
            }

        } catch (MensajeException e) {
            e.printStackTrace();
            throw new MensajeException("Datos mensaje incorrectos");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }

        return mensaje;

    }

    @Override
    public List<Mensaje> obtener(Usuario remitente, Usuario destinatario) throws SQLException {
        List<Mensaje> mensajes = new ArrayList<>();
        String sqlWhere;
        if (destinatario == null) {
            sqlWhere = "WHERE m.from_user = ?";
        } else {
            sqlWhere = "WHERE m.from_user = ? AND m.to_user = ?";
        }

        try (
                Connection conn = DriverManager.getConnection(db_url);
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM mensaje m "+
                        "JOIN usuario r ON r.id = m.from_user "+
                        "JOIN usuario d ON d.id = m.to_user "+
                        sqlWhere);
        ) {
            //usuario.valido();
            if (remitente == null || remitente.getId() <= 0) {
                throw new UsuarioException("Remitente no válido");
            }
            if (destinatario != null) {
                if (destinatario.getId() <= 0) {
                    throw new UsuarioException("Destinatario no válido");
                }
                stmt.setInt(2, destinatario.getId());
            }
            stmt.setInt(1, remitente.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                mensajes.add(
                        new Mensaje(
                                rs.getInt("m.id"),
                                new Usuario(rs.getInt("r.id"),
                                        rs.getString("r.nombre"),
                                        rs.getString("r.email"),
                                        rs.getDate("r.alta").toLocalDate(),
                                        rs.getBoolean("r.activo")),
                                new Usuario(rs.getInt("d.id"),
                                        rs.getString("d.nombre"),
                                        rs.getString("d.email"),
                                        rs.getDate("d.alta").toLocalDate(),
                                        rs.getBoolean("d.activo")),
                                rs.getString("m.cuerpo"),
                                rs.getDate("m.fecha").toLocalDate()
                        )
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return mensajes;
    }

    @Override
    public boolean borrarTodos(Usuario remitente, Usuario destinatario) throws SQLException {
        String sql;
        if (destinatario != null) {
            sql = "DELETE FROM mensaje WHERE from_user=? AND to_user=?";
        } else {
            sql = "DELETE FROM mensaje WHERE from_user=?";
        }

        try (
                Connection conn = DriverManager.getConnection(db_url);
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            //remitente.valido();
            if (remitente.getId() <= 0) {
                throw new UsuarioException("Remitente no válido");
            }
            //if (destinatario != null) destinatario.valido();
            if (destinatario != null) {
                if (destinatario.getId() <= 0) {
                    throw new UsuarioException("Destinatario no válido");
                }
            }

            stmt.setInt(1, remitente.getId());
            if (destinatario != null)  stmt.setInt(2, destinatario.getId());

            int rows = stmt.executeUpdate();
            System.out.println(rows);

            if(rows<=0) {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }
}
