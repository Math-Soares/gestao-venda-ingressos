package dao;

import model.Cliente;

import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import javax.swing.JOptionPane;

public class UsuarioDAO {

    public void registrar(String usuario, String email, String senha) {
        new Conexao(); 
        Connection conn = Conexao.getConexao();
        
        String sql = "INSERT INTO clientes (usuario, email, senha, cargo) VALUES (?, ?, ?, ?)";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, "Cliente");
            
            stmt.execute();
            stmt.close();
            
            JOptionPane.showMessageDialog(null, "Cadastrado com sucesso!");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar: " + e.getMessage());
        }
    }
    
    public boolean recuperarSenha(String usuario, String email, String novaSenha) {
        new Conexao();
        Connection conn = Conexao.getConexao();
        boolean sucesso = false;

        String sqlVerificacao = "SELECT * FROM clientes WHERE usuario = ? AND email = ?";
        String sqlUpdate = "UPDATE clientes SET senha = ? WHERE usuario = ? AND email = ?";

        try {
            PreparedStatement stmtCheck = conn.prepareStatement(sqlVerificacao);
            stmtCheck.setString(1, usuario);
            stmtCheck.setString(2, email);
            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next()) {
                PreparedStatement stmtUp = conn.prepareStatement(sqlUpdate);
                stmtUp.setString(1, novaSenha);
                stmtUp.setString(2, usuario);
                stmtUp.setString(3, email);

                int linhas = stmtUp.executeUpdate();
                if(linhas > 0) {
                    sucesso = true;
                }
                stmtUp.close();
            }

            stmtCheck.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao redefinir senha: " + e.getMessage());
        }

        return sucesso;
    }

    public Cliente checkLogin(String login, String senha) {
        new Conexao();
        Connection conn = Conexao.getConexao();
        Cliente clienteLogado = null;

        String sql = "SELECT * FROM clientes WHERE (email = ? or usuario = ?) AND senha = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, login);
            stmt.setString(3, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                clienteLogado = new Cliente();
                clienteLogado.setId(rs.getInt("id_cliente"));
                clienteLogado.setUsuario(rs.getString("usuario"));
                clienteLogado.setEmail(rs.getString("email"));
                clienteLogado.setCargo(rs.getString("cargo"));
            }
            stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro no login: " + e.getMessage());
        }

        return clienteLogado;
    }
    
    public void atualizarPerfil(Cliente usuario, String novoUsuario, String novoEmail, String novaSenha) {
        new Conexao();
        Connection conn = Conexao.getConexao();
        
        String id = String.valueOf(usuario.getId());
        
        String sql = "UPDATE clientes SET usuario = ?, email = ?, senha = ? WHERE id_cliente = ?";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, novoUsuario);
            stmt.setString(2, novoEmail);
            stmt.setString(3, novaSenha);
            stmt.setString(4, id);
            
            int linhasAfetadas = stmt.executeUpdate();
            stmt.close();
            
            if(linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Dados atualizados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao atualizar senha.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro SQL: " + e.getMessage());
        }
    }
    
    public ArrayList<Cliente> listarUsuarios() {
        new Conexao();
        Connection conn = Conexao.getConexao();
        
        ArrayList<Cliente> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM clientes";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id_cliente"));
                c.setUsuario(rs.getString("usuario"));
                c.setEmail(rs.getString("email"));
                c.setCargo(rs.getString("cargo"));
                
                lista.add(c);
            }
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar usuários: " + e.getMessage());
        }
        
        return lista;
    }
    
    public void removerCliente(int id) {
        new Conexao();
        Connection conn = Conexao.getConexao();

        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            stmt.close();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Cliente removido com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Cliente não encontrado para remoção.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao remover cliente: " + e.getMessage());
        }
    }
}