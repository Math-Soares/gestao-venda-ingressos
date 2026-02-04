package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import util.Conexao;

public class DashboardDAO {

    private Connection pegarConexao() {
        Connection conn = Conexao.getConexao();
        if (conn == null) {
            new Conexao();
            conn = Conexao.getConexao();
        }
        return conn;
    }

    public double getTotalVendas() {
        double total = 0;
        String sql = "SELECT SUM(valor_total) as soma FROM ingressos";
        
        Connection conn = pegarConexao();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                total = rs.getDouble("soma");
            }
        } catch (SQLException e) {
            System.out.println("Erro dashboard (Vendas): " + e.getMessage());
        }
        return total;
    }

    public int getTotalIngressosVendidos() {
        int total = 0;
        String sql = "SELECT SUM(quantidade) as soma FROM ingressos";
        
        Connection conn = pegarConexao();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                total = rs.getInt("soma");
            }
        } catch (SQLException e) {
            System.out.println("Erro dashboard (Ingressos): " + e.getMessage());
        }
        return total;
    }

    public int getTotalEventos() {
        int total = 0;
        String sql = "SELECT COUNT(*) as contagem FROM eventos";
        
        Connection conn = pegarConexao();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                total = rs.getInt("contagem");
            }
        } catch (SQLException e) {
            System.out.println("Erro dashboard (Eventos): " + e.getMessage());
        }
        return total;
    }
    
    public int getTotalClientes() {
        int total = 0;
        String sql = "SELECT COUNT(*) as contagem FROM clientes WHERE cargo = 'Cliente'";
        
        Connection conn = pegarConexao();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                total = rs.getInt("contagem");
            }
        } catch (SQLException e) {
            System.out.println("Erro dashboard (Clientes): " + e.getMessage());
        }
        return total;
    }

    public ArrayList<Object[]> getUltimasVendas() {
        ArrayList<Object[]> lista = new ArrayList<>();
        
        String sql = "SELECT c.usuario, e.nome_evento, i.data_compra, i.valor_total " +
                     "FROM ingressos i " +
                     "JOIN clientes c ON i.id_cliente = c.id_cliente " +
                     "JOIN eventos e ON i.id_evento = e.id_evento " +
                     "ORDER BY i.data_compra DESC LIMIT 10";
        
        Connection conn = pegarConexao();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getString("usuario"),
                    rs.getString("nome_evento"),
                    rs.getString("data_compra"),
                    String.format("R$ %.2f", rs.getDouble("valor_total"))
                };
                lista.add(linha);
            }
        } catch (SQLException e) {
            System.out.println("Erro dashboard (Lista Recentes): " + e.getMessage());
        }
        return lista;
    }
}