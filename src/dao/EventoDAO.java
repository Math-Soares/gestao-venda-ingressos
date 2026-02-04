package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.Evento;

import util.Conexao;

public class EventoDAO {
    
    public void cadastrarEvento(Evento evento) {
        new Conexao(); 
        Connection con = Conexao.getConexao();
        
        String sql = "INSERT INTO eventos (nome_evento, data_evento, preco, capacidade) VALUES (?, ?, ?, ?)";
        
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getData());
            stmt.setDouble(3, evento.getPreco());
            stmt.setInt(4, evento.getCapacidade());
            
            stmt.execute();
            stmt.close();
            
            JOptionPane.showMessageDialog(null, "Evento cadastrado com sucesso!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar evento: " + e.getMessage());
        }
    }
    
    public ArrayList<Evento> listarEventos() {
        new Conexao();
        Connection con = Conexao.getConexao();
        
        ArrayList<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM eventos";
        
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()) {
                Evento e = new Evento();
                e.setId(rs.getInt("id_evento"));
                e.setNome(rs.getString("nome_evento"));
                e.setData(rs.getString("data_evento"));
                e.setPreco(rs.getDouble("preco"));
                e.setCapacidade(rs.getInt("capacidade"));
                
                lista.add(e);
            }
            stmt.close();
            rs.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar eventos: " + e.getMessage());
        }
        
        return lista;
    }
    
    public void removerEvento(int id) {
        new Conexao();
        Connection conn = Conexao.getConexao();

        String sql = "DELETE FROM eventos WHERE id_evento = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            stmt.close();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Evento removido com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Evento não encontrado para remoção.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao remover evento: " + e.getMessage());
        }
    }
}