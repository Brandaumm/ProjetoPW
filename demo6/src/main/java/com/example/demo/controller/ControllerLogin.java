package com.example.demo.controller;

import com.example.demo.persistencia.ClienteDAO;
import com.example.demo.persistencia.LogistaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.io.IOException;

@Controller
public class ControllerLogin {

    private final LogistaDAO logistaDAO = new LogistaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @RequestMapping(value = "/logar", method = RequestMethod.POST)

    public void doLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var email = request.getParameter("email");
        var senha = request.getParameter("senha");

        // Verifica se as credenciais correspondem a um logista
        if (logistaDAO.buscarLogista(email, senha)) {
            response.sendRedirect("admLogista.html");
            return;
        }

        // Verifica se as credenciais correspondem a um cliente
        if (clienteDAO.buscarCliente(email, senha)) {
            // Redireciona para a página clienteCarrinho.html
            response.sendRedirect("BemVindo.html");
            return;
        }

        // Se as credenciais não foram encontradas, redireciona para a página de cadastro
        response.sendRedirect("register.html");
    }


    @RequestMapping(value = "/semCadastro", method = RequestMethod.GET)
    public String semCadastro() {
        return "register.html"; // Página para redirecionamento quando não houver cadastro
    }

    @RequestMapping("/sair")
    public static void doLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        session.invalidate();
        response.sendRedirect("index.html");
    }
}
