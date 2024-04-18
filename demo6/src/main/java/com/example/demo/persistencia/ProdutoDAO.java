package com.example.demo.persistencia;

import com.example.demo.classes.Produto;
import org.springframework.stereotype.Repository;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


@Repository
public class ProdutoDAO {

    private static Conexao c;

    private static String ID = "SELECT * FROM produtos WHERE id = ?";
    private String LIST = "select * from \"produtos\"";
    private String INS = "INSERT INTO \"produtos\" (nome, descricao, preco, quantidade) VALUES (?, ?, ?, ?)";
    private static String ATU = "UPDATE produtos SET quantidade = ? WHERE nome=?";
    private String BUSCA_POR_ID = "SELECT * FROM produtos WHERE id = ?";
    private static String INSERIR = "INSERT INTO produtos (nome, descricao, preco, quantidade) VALUES (?, ?, ?, ?)";
    private static String BUSCA = "SELECT * FROM produtos WHERE nome = ?";
    private String SUBTRAIR = "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ?";

    private String ATUALIZAR = "UPDATE produtos SET quantidade = quantidade + ? WHERE nome = ?";


    public ProdutoDAO() {
        c = new Conexao("jdbc:postgresql://localhost:5432/WPProject","postgres","123");
    }


    public static void cadastrar(Produto p) {
        try {
            c.conectar();
            if (produtoExiste(p.getNome())) {
                PreparedStatement atualizarStmt = c.getConexao().prepareStatement(ATU);
                atualizarStmt.setInt(1, p.getEstoque());
                atualizarStmt.setString(2, p.getNome());
                atualizarStmt.executeUpdate();
            } else {
                // Se o produto não existe, insere um novo
                PreparedStatement inserirStmt = c.getConexao().prepareStatement(INSERIR);
                inserirStmt.setString(1, p.getNome());
                inserirStmt.setString(2, p.getDescricao());
                inserirStmt.setInt(3, p.getPreco());
                inserirStmt.setInt(4, p.getEstoque());
                inserirStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Erro na inclusão: " + e.getMessage());
            e.printStackTrace();
        } finally {
            c.desconectar();
        }
    }

    private static boolean produtoExiste(String nome) throws SQLException {

        PreparedStatement buscaStmt = c.getConexao().prepareStatement(BUSCA);
        buscaStmt.setString(1, nome);
        ResultSet rs = buscaStmt.executeQuery();
        return rs.next();
    }

    public ArrayList<Produto> listaProdutos() {
        ArrayList<Produto> produtos = new ArrayList<Produto>();
        Produto p;
        try {
            c.conectar();
            Statement st = c.getConexao().createStatement();
            ResultSet rs = st.executeQuery(LIST);
            while (rs.next()) {
                p = new Produto(rs.getInt("id"), rs.getInt("preco"), rs.getString("nome"), rs.getString("descricao"), rs.getInt("quantidade"));
                produtos.add(p);
            }
            c.desconectar();
        } catch (Exception e) {
            System.out.println("Erro na busca: " + e.getMessage());
        }
        return produtos;
    }

    public static Produto getProdutoById(int produtoId) {
        Produto p = null;
        try {
            c.conectar();
            PreparedStatement ps = c.getConexao().prepareStatement(ID);
            ps.setInt(1, produtoId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                p = new Produto(rs.getInt("id"), rs.getInt("preco"), rs.getString("nome"), rs.getString("descricao"), rs.getInt("quantidade"));

            }
            c.desconectar();
        } catch (Exception e) {
            System.out.println("Erro na busca: " + e.getMessage());
        }
        return p;
    }

    public void subtrairEstoque(int quantidade, int produtoId) {
        try {
            c.conectar();
            PreparedStatement ps = c.getConexao().prepareStatement(SUBTRAIR);
            ps.setInt(1, quantidade); // quantidade a ser decrementada
            ps.setInt(2, produtoId); // ID do produto
            ps.executeUpdate(); // executeUpdate em vez de execute
            c.desconectar();
        } catch (Exception e) {
            System.out.println("Erro na alteração: " + e.getMessage());
        }
    }

    public void decrementarEstoque(int quantidade, int produtoId) {
        try {
            c.conectar();
            PreparedStatement ps = c.getConexao().prepareStatement(ATUALIZAR);
            ps.setInt(1, quantidade); // quantidade a ser decrementada
            ps.setInt(2, produtoId); // ID do produto
            ps.executeUpdate(); // executeUpdate em vez de execute
            c.desconectar();
        } catch (Exception e) {
            System.out.println("Erro na alteração: " + e.getMessage());
        }
    }


    public static boolean estoqueMaiorOuIgualQueQuantidade(int qtd, int id){
        boolean check = false;
        try {
            Produto p = getProdutoById(id);
            if(p.getEstoque() >= qtd){
                return check=true;
            }
        } catch (Exception e) {
            System.out.println("Erro na verificação: " + e.getMessage());
        }
        return check;
    }

    public ArrayList<Produto> listaExibir() { //Relatorio de todos os funcionarios
        Produto pro;
        ArrayList<Produto> lista = new ArrayList<Produto>();
        try {
            c.conectar();
            Statement inst = c.getConexao().createStatement();
            ResultSet rs = inst.executeQuery(LIST);
            while (rs.next()) {
                pro = new Produto(rs.getInt("id"), rs.getInt("preco"), rs.getString("nome"), rs.getString("descricao"), rs.getInt("quantidade"));
                lista.add(pro);
            }
            c.desconectar();
        } catch (Exception e) {
            System.out.println("Erro na busca" + e);
        }
        return lista;
    }
    public static Produto buscarProdutoPorId(int id) {
        Produto produto = null;
        try {
            c.conectar();
            PreparedStatement stmt = c.getConexao().prepareStatement(ID);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int preco = rs.getInt("preco");
                produto = new Produto(
                        rs.getInt("id"),
                        preco,
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getInt("quantidade")
                );
            }
            c.desconectar();
        } catch (SQLException e) {
            System.out.println("Erro na busca por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return produto;
    }

    public ArrayList<Produto> getTodosProdutos() {
        ArrayList<Produto> produtos = new ArrayList<>();
        try {
            c.conectar();
            Statement st = c.getConexao().createStatement();
            ResultSet rs = st.executeQuery(LIST);
            while (rs.next()) {
                Produto p = new Produto(
                        rs.getInt("id"),
                        rs.getInt("preco"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getInt("quantidade")
                );
                produtos.add(p);
            }
            c.desconectar();
        } catch (Exception e) {
            System.out.println("Erro na busca: " + e.getMessage());
            e.printStackTrace();
        }
        return produtos;
    }


}


