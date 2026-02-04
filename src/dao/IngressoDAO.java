package dao;

import model.Evento;

import util.Conexao;

import java.sql.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class IngressoDAO {

    private Connection conn;

    public IngressoDAO() {
        if (Conexao.getConexao() == null) {
            new Conexao();
        }
        this.conn = Conexao.getConexao();
    }

    public ArrayList<Evento> listarEventosDisponiveis() {
        ArrayList<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE capacidade > 0 ORDER BY data_evento";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Evento e = new Evento(
                    rs.getInt("id_evento"),
                    rs.getString("nome_evento"),
                    rs.getString("data_evento"),
                    rs.getDouble("preco"),
                    rs.getInt("capacidade")
                );
                lista.add(e);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar eventos: " + e.getMessage());
        }
        return lista;
    }

    public boolean confirmarVenda(int idCliente, int idEvento, int quantidade, double valorTotal) {
        String sqlVenda = "INSERT INTO ingressos (id_cliente, id_evento, quantidade, valor_total, data_compra) VALUES (?, ?, ?, ?, NOW())";
        
        String sqlEstoque = "UPDATE eventos SET capacidade = capacidade - ? WHERE id_evento = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda)) {
                stmtVenda.setInt(1, idCliente);
                stmtVenda.setInt(2, idEvento);
                stmtVenda.setInt(3, quantidade);
                stmtVenda.setDouble(4, valorTotal);
                stmtVenda.executeUpdate();
            }

            try (PreparedStatement stmtEstoque = conn.prepareStatement(sqlEstoque)) {
                stmtEstoque.setInt(1, quantidade);
                stmtEstoque.setInt(2, idEvento);
                stmtEstoque.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Erro ao realizar venda: " + e.getMessage());
            return false;
        }
    }
    
    public ArrayList<Object[]> listarIngressosDoCliente(int idCliente) {
        ArrayList<Object[]> lista = new ArrayList<>();

        String sql = "SELECT i.id_ingresso, e.nome_evento, e.data_evento, i.quantidade, i.valor_total, i.data_compra "
                + "FROM ingressos i "
                + "JOIN eventos e ON i.id_evento = e.id_evento "
                + "WHERE i.id_cliente = ? "
                + "ORDER BY i.data_compra DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id_ingresso"),
                        rs.getString("nome_evento"),
                        rs.getString("data_evento"),
                        rs.getInt("quantidade"),
                        String.format("R$ %.2f", rs.getDouble("valor_total")),
                        rs.getTimestamp("data_compra")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar seus ingressos: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean cancelarIngresso(int idIngresso) {
        String sqlSelect = "SELECT id_evento, quantidade FROM ingressos WHERE id_ingresso = ?";
        String sqlUpdate = "UPDATE eventos SET capacidade = capacidade + ? WHERE id_evento = ?";
        String sqlDelete = "DELETE FROM ingressos WHERE id_ingresso = ?";

        try {
            conn.setAutoCommit(false);

            int idEvento = 0;
            int quantidade = 0;

            try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
                stmtSelect.setInt(1, idIngresso);
                ResultSet rs = stmtSelect.executeQuery();
                if (rs.next()) {
                    idEvento = rs.getInt("id_evento");
                    quantidade = rs.getInt("quantidade");
                } else {
                    return false;
                }
            }

            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                stmtUpdate.setInt(1, quantidade);
                stmtUpdate.setInt(2, idEvento);
                stmtUpdate.executeUpdate();
            }

            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {
                stmtDelete.setInt(1, idIngresso);
                stmtDelete.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Erro ao cancelar ingresso: " + e.getMessage());
            return false;
        }
    }
    
    public void apagarTodosIngressosDoCliente(int idCliente) {
        String sql = "SELECT id_ingresso FROM ingressos WHERE id_cliente = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idIngresso = rs.getInt("id_ingresso");
                    cancelarIngresso(idIngresso); 
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao limpar ingressos: " + e.getMessage());
        }
    }
}