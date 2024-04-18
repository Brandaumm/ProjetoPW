package com.example.demo.controller;

import com.example.demo.persistencia.ClienteDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@Controller
public class ControllerCadastro {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @RequestMapping("/cadastro")
    public String exibirFormularioCadastro() {
        return "cadastro.html";
    }

    @PostMapping("/cadastro")
    public void cadastrarCliente(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");


        // Verifica se as senhas coincidem
        if (!password.equals(confirmPassword)) {
            response.sendRedirect("cadastro.html?msg=Senhas não coincidem");
            return;
        }

        // Salva o cliente no banco de dados
        clienteDAO.inserirCliente(username, email, password);

        response.sendRedirect("index.html?msg=Cliente cadastrado com sucesso");
    }

    public Integer obterClienteId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Integer) session.getAttribute("clienteId");
    }

}
