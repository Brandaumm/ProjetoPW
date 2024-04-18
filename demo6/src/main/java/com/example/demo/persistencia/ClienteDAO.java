package com.example.demo.persistencia;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClienteDAO {
    private Conexao c;

    private final String BUS = "SELECT * FROM cliente WHERE email = ? AND senha = ?";
    public ClienteDAO() {
        c = new Conexao("jdbc:postgresql://localhost:5432/WPProject","postgres","123");
    }

    public void inserirCliente(String nome, String email, String senha) {
        try {
            c.conectar();
            String SQL = "INSERT INTO cliente (nome, email, senha) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = c.getConexao().prepareStatement(SQL)) {
                stmt.setString(1, nome);
                stmt.setString(2, email);
                stmt.setString(3, senha);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao inserir cliente: " + e.getMessage());
        } finally {
            c.desconectar();
        }
    }


    public boolean buscarCliente(String email, String senha) {
        try {
            c.conectar();
            try (PreparedStatement inst = c.getConexao().prepareStatement(BUS)) {
                inst.setString(1, email);
                inst.setString(2, senha);
                System.out.println("buscarCliente buscou o cliente");
                try (ResultSet rs = inst.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("retornou buscarCliente TRUE");
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro na busca: " + e.getMessage());
        } finally {
            c.desconectar();
        }
        return false;
    }


}