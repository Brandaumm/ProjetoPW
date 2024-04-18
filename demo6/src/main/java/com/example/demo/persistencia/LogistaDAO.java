package com.example.demo.persistencia;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LogistaDAO {

    private Conexao c;

    private final String BUS = "SELECT * FROM logista WHERE email = ? AND senha = ?";
    private final String INSERIR_PRODUTO = "INSERT INTO Produtos (nome, quantidade, preco) VALUES (?, ?, ?) ON CONFLICT (nome) DO UPDATE SET quantidade = produtos.quantidade + ?";

    public LogistaDAO() {
        c = new Conexao("jdbc:postgresql://localhost:5432/WPProject","postgres","123");
    }

    public boolean buscarLogista(String email, String senha) {
        try {
            c.conectar();
            try (PreparedStatement inst = c.getConexao().prepareStatement(BUS)) {
                inst.setString(1, email);
                inst.setString(2, senha);
                try (ResultSet rs = inst.executeQuery()) {
                    if (rs.next()) {
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