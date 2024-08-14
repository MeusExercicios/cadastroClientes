package program;

import entities.Cliente;
import entities.Produto;

import javax.swing.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class App {
    public static void main(String[] args) throws ParseException, ClassNotFoundException, SQLException {
        String url = "jdbc:postgresql://localhost:5432/cadastroClientes";
        String user = "postgres";
        String password = "admin";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Carrega o driver JDBC para PostgreSQL
            Class.forName("org.postgresql.Driver");

            // Estabelece a conexão com o banco de dados
            connection = DriverManager.getConnection(url, user, password);

            // Cria um Statement para executar as consultas
            statement = connection.createStatement();

            String opcaoCrud = JOptionPane.showInputDialog(null, "Escolha uma opção:" +
                    "\n1. Consultar dados" +
                    "\n2. Inserir dados" +
                    "\n3. Atualizar dados" +
                    "\n4. Deletar dados");

            if (opcaoCrud.equals("1")) {
                // Consulta os dados no banco de dados
                String sqlClientes = "SELECT * FROM clientes";
                resultSet = statement.executeQuery(sqlClientes);

                StringBuilder resultado = new StringBuilder();

                while (resultSet.next()) {
                    Integer id_cliente = resultSet.getInt("id_cliente");
                    String nome = resultSet.getString("nome");
                    Integer idade = resultSet.getInt("idade");
                    String genero = resultSet.getString("genero");
                    String telefone = resultSet.getString("telefone");
                    Date data_compra = resultSet.getDate("data_compra");

                    resultado.append("ID Cliente: ").append(id_cliente).append("\n")
                            .append("Nome: ").append(nome).append("\n")
                            .append("Idade: ").append(idade).append("\n")
                            .append("Gênero: ").append(genero).append("\n")
                            .append("Telefone: ").append(telefone).append("\n")
                            .append("Data da Compra: ").append(data_compra).append("\n");

                    // Consulta os produtos comprados por este cliente
                    String sqlProdutos = "SELECT * FROM produtos WHERE id_cliente = " + id_cliente;
                    Statement statementProdutos = connection.createStatement();
                    ResultSet resultSetProdutos = statementProdutos.executeQuery(sqlProdutos);

                    resultado.append("Produtos Comprados:\n");
                    while (resultSetProdutos.next()) {
                        String nomeProduto = resultSetProdutos.getString("nome");
                        Double preco = resultSetProdutos.getDouble("preco");
                        Integer quantidade = resultSetProdutos.getInt("quantidade");

                        resultado.append(" - Produto: ").append(nomeProduto)
                                .append(", Preço: ").append(preco)
                                .append(", Quantidade: ").append(quantidade).append("\n");
                    }

                    resultado.append("\n");

                    // Fecha o ResultSet e Statement para produtos
                    resultSetProdutos.close();
                    statementProdutos.close();
                }

                JOptionPane.showMessageDialog(null, resultado.toString());

            } else if (opcaoCrud.equals("2")) {
                // Inserção de dados no banco de dados
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                int resposta;
                List<Cliente> clientes = new ArrayList<>();
                do {
                    List<Produto> produtos = new ArrayList<>();

                    String nomeCliente = JOptionPane.showInputDialog(null, "Nome do cliente: ").toUpperCase();
                    String generoCliente = JOptionPane.showInputDialog(null, "Qual o gênero do cliente ?").toUpperCase();
                    String idadeCliente = JOptionPane.showInputDialog(null, "Qual a idade do cliente ?");
                    String telefoneCliente = JOptionPane.showInputDialog(null, "Qual o contato do cliente ?");
                    String quantosProdutos = JOptionPane.showInputDialog(null, "Quantos produtos o cliente comprou ?");

                    if (Integer.parseInt(quantosProdutos) == 0) {
                        Cliente cliente = new Cliente(nomeCliente, Integer.parseInt(idadeCliente),
                                generoCliente, telefoneCliente, null, null);
                        clientes.add(cliente);

                    } else {
                        for (int i = 0; i < Integer.parseInt(quantosProdutos); i++) {
                            String nomeProduto = JOptionPane.showInputDialog
                                    (null, "Qual produto o cliente comprou ?(Lasanha/Coxinha/Pizza)");
                            String precoProduto = JOptionPane.showInputDialog
                                    (null, "Qual o valor do produto ?");
                            String quantidadeProduto = JOptionPane.showInputDialog
                                    (null, "Qual a quantidade ?");

                            Produto produto = new Produto(nomeProduto, Double.parseDouble(precoProduto), Integer.parseInt(quantosProdutos));
                            produtos.add(produto);
                        }

                        String dataCompra = JOptionPane.showInputDialog(null, "Qual a data da compra ?(dd/MM/yyyy)");
                        Date dataConvertida = simpleDateFormat.parse(dataCompra);

                        Cliente cliente = new Cliente(nomeCliente, Integer.parseInt(idadeCliente),
                                generoCliente, telefoneCliente, dataConvertida, produtos);
                        clientes.add(cliente);
                    }

                    resposta = JOptionPane.showConfirmDialog(
                            null,
                            "Deseja continuar o cadastro de clientes?",
                            "Confirmação",
                            JOptionPane.YES_NO_OPTION
                    );
                } while (resposta == JOptionPane.YES_OPTION);

                // Inserção dos dados no banco de dados
                for (Cliente comprador : clientes) {
                    String insertClienteSQL = "INSERT INTO clientes (nome, idade, genero, telefone, data_compra) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(insertClienteSQL, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, comprador.getNome());
                    preparedStatement.setInt(2, comprador.getIdade());
                    preparedStatement.setString(3, comprador.getGenero());
                    preparedStatement.setString(4, comprador.getTelefone());
                    preparedStatement.setDate(5, new java.sql.Date(comprador.getDate().getTime()));
                    preparedStatement.executeUpdate();

                    // Obtém o ID gerado para o cliente
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int clienteId = generatedKeys.getInt(1);

                        // Inserção dos produtos comprados
                        for (Produto produto : comprador.getProdutoList()) {
                            String insertProdutoSQL = "INSERT INTO produtos (nome, preco, quantidade, id_cliente) VALUES (?, ?, ?, ?)";
                            PreparedStatement preparedStatementProduto = connection.prepareStatement(insertProdutoSQL);
                            preparedStatementProduto.setString(1, produto.getNome());
                            preparedStatementProduto.setDouble(2, produto.getPreco());
                            preparedStatementProduto.setInt(3, produto.getQuantidade());
                            preparedStatementProduto.setInt(4, clienteId);
                            preparedStatementProduto.executeUpdate();
                        }
                    }
                }

                JOptionPane.showMessageDialog(null, "Dados inseridos com sucesso!");

            } else if (opcaoCrud.equals("3")) {
                // Atualização de dados no banco de dados
                String idCliente = JOptionPane.showInputDialog(null, "Informe o ID do cliente que deseja atualizar:");
                String novoNome = JOptionPane.showInputDialog(null, "Novo nome:");
                String novaIdade = JOptionPane.showInputDialog(null, "Nova idade:");
                String novoGenero = JOptionPane.showInputDialog(null, "Novo gênero:");
                String novoTelefone = JOptionPane.showInputDialog(null, "Novo telefone:");

                String updateSQL = "UPDATE clientes SET nome = ?, idade = ?, genero = ?, telefone = ? WHERE id_cliente = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
                preparedStatement.setString(1, novoNome);
                preparedStatement.setInt(2, Integer.parseInt(novaIdade));
                preparedStatement.setString(3, novoGenero);
                preparedStatement.setString(4, novoTelefone);
                preparedStatement.setInt(5, Integer.parseInt(idCliente));
                preparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(null, "Dados atualizados com sucesso!");

            } else if (opcaoCrud.equals("4")) {
                // Exclusão de dados no banco de dados
                String idCliente = JOptionPane.showInputDialog(null, "Informe o ID do cliente que deseja excluir:");

                String deleteSQL = "DELETE FROM clientes WHERE id_cliente = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
                preparedStatement.setInt(1, Integer.parseInt(idCliente));
                preparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(null, "Dados excluídos com sucesso!");
            }
        } finally {
            // Fechamento dos recursos
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }
}
