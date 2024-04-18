package com.example.demo.controller;

import com.example.demo.classes.Produto;
import com.example.demo.persistencia.ProdutoDAO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.PrintWriter;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class VerCarrinhoController {

    private final HttpServletRequest httpServletRequest;

    public VerCarrinhoController(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @GetMapping("/verCarrinho")
    public void verCarrinho(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int total = 0;
        boolean carrinhoVazio = true; // Variável para verificar se o carrinho está vazio
        boolean estoqueSuficiente = true;

        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        // Obtém todos os cookies do pedido
        Cookie[] cookies = request.getCookies();

        writer.println("<html><head><title>Carrinho</title></head><body style='display: flex; flex-direction: column; align-items: center; background-color: lightblue'>");
        writer.println("<h1>Carrinho de Compras</h1>");
        writer.println("<table border='1' style='background: white'>");
        writer.println("<tr><th>Nome</th><th>Preço</th><th>Quantidade</th><th>Remover</th></tr>");

        // Criando uma instância de ProdutoDAO
        ProdutoDAO produtoDAO = new ProdutoDAO();

        // Obtendo todos os produtos do banco de dados
        ArrayList<Produto> produtos = produtoDAO.getTodosProdutos();

        for (Produto produto : produtos) {
            String cookieName = "produto_" + produto.getId();
            // Verificando se existe um cookie correspondente para este produto
            Cookie produtoCookie = procurarCookie(cookies, cookieName);
            if (produtoCookie != null) {
                carrinhoVazio = false; // Se há pelo menos um item no carrinho, ele não está vazio
                int quantidade = Integer.parseInt(produtoCookie.getValue());
                // Exibindo o produto na tabela
                writer.println("<tr>");
                writer.println("<td>" + produto.getNome() + "</td>");
                writer.println("<td>" + produto.getPreco() + "</td>");
                writer.println("<td>" + quantidade + "</td>");
                writer.println("<td><a href='/removerCarrinho/" + produto.getId() + "'>Remover</a></td>\n");
                writer.println("</tr>");
                total += quantidade * produto.getPreco();
                // Verificando se há estoque suficiente
                if (!ProdutoDAO.estoqueMaiorOuIgualQueQuantidade(quantidade, produto.getId())) {
                    estoqueSuficiente = false;
                }
            }
        }

        writer.println("</table>");

        if (!carrinhoVazio && estoqueSuficiente) {
            // Se o carrinho não estiver vazio e houver estoque suficiente, o botão "Finalizar compra" deve estar habilitado
            writer.println("<button><a href='/finalizarCompra' style='text-decoration: none; color: black'>Finalizar compra</a></button><br>");
        } else {
            // Se o carrinho estiver vazio ou não houver estoque suficiente, desativa o botão "Finalizar compra"
            writer.println("<button disabled style='color: grey'>Finalizar compra</button><br>");
        }
        writer.println("<button><a href='/verCarrinho' style='text-decoration: none; color: black'>Atualizar carrinho</a></button>");
        writer.println("<br>");
        // Botão para voltar para a lista de produtos do cliente
        writer.println("<button><a href='bemVindo.html' style='text-decoration: none; color: black'>Voltar</a></button>");

        writer.println("</body></html>");
    }



    @GetMapping("/removerCarrinho/{produtoId}")
    @ResponseBody
    public String removerDoCarrinho(@PathVariable int produtoId, HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("produto_" + produtoId)) {
                    int quantidadeAtual = Integer.parseInt(cookie.getValue());
                    if (quantidadeAtual > 1) {
                        // Se a quantidade for maior que 1, decrementa a quantidade
                        quantidadeAtual--; // Decrementa a quantidade
                        System.out.println("Quantidade atualizada para o produto " + produtoId + ": " + quantidadeAtual);
                        cookie.setValue(String.valueOf(quantidadeAtual)); // Atualiza o valor do cookie
                        // Configura o caminho e o tempo de expiração do cookie
                        cookie.setPath("/"); // Define o caminho do cookie como raiz para que seja acessível em todo o site
                        cookie.setMaxAge(24 * 60 * 60); // Define o tempo de expiração do cookie (1 dia)
                        response.addCookie(cookie); // Adiciona o cookie atualizado à resposta
                        System.out.println("Cookie atualizado: " + cookie);
                    } else {
                        // Se a quantidade for igual a 1, remove o cookie
                        cookie.setMaxAge(0); // Define o tempo de expiração como zero para remover o cookie
                        // Configura o caminho do cookie
                        cookie.setPath("/"); // Define o caminho do cookie como raiz para que seja acessível em todo o site
                        response.addCookie(cookie); // Adiciona o cookie atualizado à resposta
                        System.out.println("Cookie removido: " + cookie);
                    }
                    return "Produto removido do carrinho com sucesso.";
                }
            }
        }
        return "Produto não encontrado no carrinho.";
    }






    // Método para procurar um cookie pelo nome
    private Cookie procurarCookie(Cookie[] cookies, String nomeCookie) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(nomeCookie)) {
                    return cookie;
                }
            }
        }
        return null;
    }



}