package com.example.demo.controller;


import com.example.demo.classes.Produto;
import com.example.demo.persistencia.ProdutoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Controller
public class ControllerCliente {
    @Autowired
    private ProdutoDAO produtoDAO;

    @Autowired
    private ControllerCadastro cadastroController;

    @GetMapping("/todosprodutoscliente")
    @ResponseBody
    public String listarTodosProdutosCliente(HttpServletRequest request) {
        List<Produto> produtos = produtoDAO.listaExibir();

        StringBuilder html = new StringBuilder();
        html.append("<html> <head> <title> Todos os Produtos </title> </head> <body> <table>");

        // Adiciona a linha de cabeçalho com os nomes das colunas
        html.append("<tr>");
        html.append("<th>ID</th>");
        html.append("<th>Nome</th>");
        html.append("<th>Descrição</th>");
        html.append("<th>Preço</th>");
        html.append("<th>Estoque</th>");
        html.append("<th>Ação</th>"); // Coluna extra para o botão
        html.append("</tr>");

        for (Produto produto : produtos) {
            html.append("<tr>");
            html.append("<td>").append(produto.getId()).append("</td>");
            html.append("<td>").append(produto.getNome()).append("</td>");
            html.append("<td>").append(produto.getDescricao()).append("</td>");
            html.append("<td>").append(produto.getPreco()).append("</td>");
            html.append("<td>").append(produto.getEstoque()).append("</td>");
            html.append("<td>")
                    .append("<form action=\"/adicionarAoCarrinho/").append(produto.getId()).append("\" method=\"get\">")
                    .append("<button type=\"submit\">Adicionar ao Carrinho</button>")
                    .append("</form>")
                    .append("</td>"); // Formulário com botão para adicionar ao carrinho
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("</body> </html>");

        return html.toString();
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false); // Obtém a sessão atual, se existir

        if (session != null) {
            session.invalidate(); // Invalida a sessão atual, removendo-a
        }

        // Redireciona o cliente para a página de login ou outra página de sua escolha
        response.sendRedirect("index.html"); // Substitua "index.html" pela página desejada
    }



}
