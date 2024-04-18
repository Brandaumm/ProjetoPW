package com.example.demo.classes;

import java.util.*;
import java.util.ArrayList;
public class Carrinho {
    private static Map<Produto, Integer> produtosNoCarrinho; // Mapeia Produto para sua quantidade
    private List<Produto> produtos; // Lista de produtos

    static {
        produtosNoCarrinho = new HashMap<>();
    }

    public Carrinho() {
        this.produtos = new ArrayList<>();
    }

    public static void adicionarProduto(Produto produto) {
        if (produtosNoCarrinho.containsKey(produto)) {
            // Se o produto já está no carrinho, incrementa a quantidade
            int quantidadeAtual = produtosNoCarrinho.get(produto);
            produtosNoCarrinho.put(produto, quantidadeAtual + 1);
        } else {
            System.out.println("2");
            // Se o produto não está no carrinho, adiciona com quantidade 1
            produtosNoCarrinho.put(produto, 1);
        }
    }


    public static void incrementaQuantidadeProduto(Produto produto) {
        Integer quantidadeAtual = produtosNoCarrinho.get(produto);
        produtosNoCarrinho.put(produto, quantidadeAtual + 1);
    }

    public static boolean contemProduto(Produto produto) {
        return produtosNoCarrinho.containsKey(produto);
    }



    public void setProdutos(ArrayList<Produto> produtos) {
        this.produtos = produtos;
    }
    public Produto getProduto (int id){
        Produto mp = null;
        for (Produto p : produtos){
            if (p.getId() == id){
                return p;
            }
        }
        return mp;
    }
    public void removeProduto(int id) {
        Produto p = getProduto(id);
        if (p != null && produtos != null) {
            produtos.remove(p);
        }
    }


    public void addProduto (Produto p){
        produtos.add(p);
    }

    public static Map<Produto, Integer> getProdutosNoCarrinho() {
        return produtosNoCarrinho;
    }

    public void diminuirQuantidadeProduto(Produto produto) {
        Integer quantidadeAtual = produtosNoCarrinho.get(produto);
        if (quantidadeAtual != null && quantidadeAtual > 0) {
            // Se a quantidade atual for maior que 0, diminui a quantidade
            quantidadeAtual--;
            if (quantidadeAtual == 0) {
                // Se a quantidade atual for igual a 0, remove o produto completamente do carrinho
                produtosNoCarrinho.remove(produto);
            } else {
                // Atualiza a quantidade do produto no carrinho
                produtosNoCarrinho.put(produto, quantidadeAtual);
            }
        }
    }
}