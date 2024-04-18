package com.example.demo.controller;

import com.example.demo.classes.Carrinho;
import com.example.demo.classes.Produto;
import com.example.demo.persistencia.ProdutoDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Controller
public class CarrinhoController {

    @GetMapping("/addCarrinho/{produtoId}")
    @ResponseBody
    public String adicionarAoCarrinho(@PathVariable int produtoId, HttpServletResponse response, HttpServletRequest request) {
        ProdutoDAO produtoDAO = new ProdutoDAO();
        Produto produto = produtoDAO.getProdutoById(produtoId);

        if (produto != null) {
            // Adiciona o produto como um cookie individual
            adicionarProdutoNosCookies(produto, response, request);
            return "Produto adicionado ao carrinho com sucesso.";
        } else {
            return "Produto não encontrado.";
        }
    }

    private void adicionarProdutoNosCookies(Produto produto, HttpServletResponse response, HttpServletRequest request) {
        String cookieName = "produto_" + produto.getId();

        Cookie[] cookies = request.getCookies();
        int quantidade = 1; // Quantidade padrão

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    try {
                        quantidade = Integer.parseInt(cookie.getValue());
                        quantidade++; // Incrementa a quantidade
                    } catch (NumberFormatException e) {
                        // Em caso de erro na conversão, mantém a quantidade como 1
                    }
                    break;
                }
            }
        }

        Cookie produtoCookie = new Cookie(cookieName, String.valueOf(quantidade));
        produtoCookie.setMaxAge(24 * 60 * 60); // Define o tempo de expiração do cookie (1 dia)
        produtoCookie.setPath("/"); // Define o caminho do cookie como raiz para que seja acessível em todo o site
        response.addCookie(produtoCookie);
    }


    private static void salvarCarrinhoNosCookies(HttpServletResponse response) {
        System.out.println("Entrou Para salvar o carrinho nos cookies");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String carrinhoJson = objectMapper.writeValueAsString(Carrinho.getProdutosNoCarrinho());
            String carrinhoCookieValue = URLEncoder.encode(carrinhoJson, "UTF-8");
            Cookie carrinhoCookie = new Cookie("carrinho", carrinhoCookieValue);
            carrinhoCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(carrinhoCookie);
            System.out.println("Salvou carrinho nos cookies (na teoria)");
        } catch (com.fasterxml.jackson.core.JsonProcessingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public Map<Integer, Integer> lerCarrinhoDosCookies(Integer clienteId, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("carrinho".equals(cookie.getName())) {
                    String carrinhoCookieValue = cookie.getValue();
                    String[] idQuantidadeProdutos = carrinhoCookieValue.split(",");
                    for (String idQuantidadeProduto : idQuantidadeProdutos) {
                        try {
                            String[] parts = idQuantidadeProduto.split(":");
                            int id = Integer.parseInt(parts[0]);
                            int quantidade = Integer.parseInt(parts[1]);
                            Produto produto = ProdutoDAO.buscarProdutoPorId(id);
                            if (produto != null) {
                                for (int i = 0; i < quantidade; i++) {
                                    Carrinho.adicionarProduto(produto);
                                }
                            }
                        } catch (NumberFormatException e) {
                            // Tratar possíveis exceções ao converter o ID do produto ou a quantidade para inteiro
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            }
        }
        return null;
    }

    @GetMapping("/finalizarCompra")
    public void finalizarCompra(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Integer clienteId = (Integer) session.getAttribute("clienteId");

        // Obtém todos os cookies do pedido
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            // Inicializa o valor total da compra
            int totalCompra = 0;

            // Criando uma instância de ProdutoDAO
            ProdutoDAO produtoDAO = new ProdutoDAO();

            // Itera sobre os cookies para calcular o valor total da compra
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if (cookieName.startsWith("produto_")) {
                    int quantidade = Integer.parseInt(cookie.getValue());

                    // Obtém o ID do produto do nome do cookie
                    int produtoId = Integer.parseInt(cookieName.substring(8));

                    // Obtém o preço do produto pelo ID
                    Produto produto = produtoDAO.getProdutoById(produtoId);
                    if (produto != null) {
                        totalCompra += produto.getPreco() * quantidade;

                        // Decrementa a quantidade do produto no banco de dados
                        produtoDAO.subtrairEstoque(quantidade, produtoId);

                        // Remove o cookie do carrinho
                        cookie.setMaxAge(0); // Define o tempo de vida do cookie como zero para removê-lo
                        cookie.setPath("/"); // Define o mesmo caminho do cookie que deseja excluir
                        response.addCookie(cookie); // Adiciona o cookie à resposta para que seja enviado ao navegador e remova o cookie existente
                    }
                }
            }

            // Configura a resposta para texto simples
            response.setContentType("text/plain");

            // Obtém o PrintWriter para escrever a resposta na página
            PrintWriter out = response.getWriter();

            // Escreve o total da compra na página
            out.println("O total da sua compra é: R$ " + totalCompra);
        }
    }





}