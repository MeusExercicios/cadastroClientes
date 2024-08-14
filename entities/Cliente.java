package entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Cliente {
    private String nome;
    private int idade;
    private String genero;
    private String telefone;
    private Date date;
    List<Produto>produtoList = new ArrayList<>();

    public Cliente(String nome, int idade, String genero, String telefone, Date date, List<Produto> produtoList) {
        this.nome = nome;
        this.idade = idade;
        this.genero = genero;
        this.telefone = telefone;
        this.date = date;
        this.produtoList = produtoList;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Produto> getProdutoList() {
        return produtoList;
    }

    public void setProdutoList(List<Produto> produtoList) {
        this.produtoList = produtoList;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nome='" + nome + '\'' +
                ", idade=" + idade +
                ", genero='" + genero + '\'' +
                ", telefone=" + telefone +
                ", date=" + date +
                ", produtoList=" + produtoList +
                '}';
    }
}
