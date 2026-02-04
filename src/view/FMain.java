package view;

import dao.DashboardDAO;
import dao.IngressoDAO;
import dao.UsuarioDAO;
import dao.EventoDAO;

import model.Cliente;
import model.Evento;

import java.awt.CardLayout;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JTextField;
import javax.swing.RowFilter;

public class FMain extends javax.swing.JFrame {
    boolean sidebarAberta = true;

    int larguraAberta = 260;
    int larguraFechada = 54;
    
    ArrayList<Evento> eventosNaTabela;
    
    private Cliente usuarioLogado;
    
    private void atualizarDashboard() {
        DashboardDAO dao = new DashboardDAO();

        double vendas = dao.getTotalVendas();
        int ingressos = dao.getTotalIngressosVendidos();
        int eventos = dao.getTotalEventos();
        int clientes = dao.getTotalClientes();

        dbvValor.setText(String.format("R$ %.2f", vendas));
        dviQuant.setText(String.valueOf(ingressos));
        dveQuant.setText(String.valueOf(eventos));
        
        dvqClientes.setText("Clientes Cadastrados");
        dvqQClientes.setText(String.valueOf(clientes));
        
        
        DefaultTableModel model = (DefaultTableModel) tblEventosDisponiveis1.getModel();
        
        model.setRowCount(0);
        
        model.setColumnIdentifiers(new Object[] {"Cliente", "Evento Comprado", "Data Compra", "Total Pago"});
        
        ArrayList<Object[]> recentes = dao.getUltimasVendas();
        
        for (Object[] linha : recentes) {
            model.addRow(linha);
        }
    }
    
    private void alterarTextoBotoes(boolean mostrar) {
        if (mostrar) {
            txtComprar.setText("Comprar");
            txtMIngressos.setText("Meus ingressos");
            txtPerfil.setText("Perfil");
            txtDashboard.setText("Dashboard");
            txtEventos.setText("Eventos");
            txtUsuarios.setText("Usuarios");
            txtSair.setText("Sair");
        } else {
            txtComprar.setText("");
            txtMIngressos.setText("");
            txtPerfil.setText("");
            txtDashboard.setText("");
            txtEventos.setText("");
            txtUsuarios.setText("");
            txtSair.setText("");
        }
    }
    
    private void resetarCores(javax.swing.JPanel painel, javax.swing.JLabel texto) {
        painel.setBackground(new java.awt.Color(30, 41, 59));
        texto.setForeground(new java.awt.Color(148, 163, 184));
    }
    
    private void alterarSelecao(javax.swing.JPanel painelSelecionado, javax.swing.JLabel textoSelecionado, String nomeIconeAtivo) {
        resetarCores(pnlComprar, txtComprar);
        resetarCores(pnlMIngressos, txtMIngressos);
        resetarCores(pnlPerfil, txtPerfil);
        resetarCores(pnlDashboard, txtDashboard);
        resetarCores(pnlEventos, txtEventos);
        resetarCores(pnlUsuarios, txtUsuarios);
        
        txtComprar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconComprar.png")));
        txtMIngressos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconMIngressos.png")));
        txtPerfil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconPerfil.png")));
        txtDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconDashboard.png")));
        txtEventos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconEventos.png")));
        txtUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconUsuarios.png")));

        painelSelecionado.setBackground(new java.awt.Color(51, 65, 85));
        textoSelecionado.setForeground(new java.awt.Color(255, 255, 255));
        
        textoSelecionado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/" + nomeIconeAtivo)));
    }
    
    private void carregarDadosVenda() {
        IngressoDAO dao = new IngressoDAO();
        
        if(this.usuarioLogado != null) {
            txtClienteAtivo.setText("Comprando como: " + this.usuarioLogado.getUsuario());
        }
        
        this.eventosNaTabela = dao.listarEventosDisponiveis();
        
        DefaultTableModel model = (DefaultTableModel) tblEventosDisponiveis.getModel();
        model.setRowCount(0);

        for (Evento e : eventosNaTabela) {
            model.addRow(new Object[]{ e.getId(), e.getNome(), e.getData(), String.format("R$ %.2f", e.getPreco()) });
        }
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tblEventosDisponiveis.setRowSorter(sorter);
    }
    
    private void atualizarPainelResumo() {
        int linhaVisual = tblEventosDisponiveis.getSelectedRow();
    
        if (linhaVisual != -1 && eventosNaTabela != null) {
            int linhaReal = tblEventosDisponiveis.convertRowIndexToModel(linhaVisual);

            if (linhaReal < eventosNaTabela.size()) {
                Evento e = eventosNaTabela.get(linhaReal);

                int qtd = (int) cprVQuantS.getValue();
                if (qtd < 1) { 
                    qtd = 1; 
                    cprVQuantS.setValue(1); 
                }

                double total = e.getPreco() * qtd;

                cprREvento.setText(e.getNome());
                cprRData.setText(e.getData());
                cprRPUnit.setText(String.format("R$ %.2f", e.getPreco()));
                cprRVTotal.setText(String.format("R$ %.2f", total));
            }
        }
    }
    
    private void configurarPermissoes(String cargo) {
        boolean isAdmin = cargo.equalsIgnoreCase("Administrador");
        
        if(isAdmin) {
            pnlDashboard.setVisible(true);
            pnlEventos.setVisible(true);
            pnlUsuarios.setVisible(true);
            
            pnlComprar.setVisible(false);
            pnlMIngressos.setVisible(false);
            pnlPerfil.setVisible(false);
            
            btnDashboard.doClick();
        } else {
            pnlComprar.setVisible(true);
            pnlMIngressos.setVisible(true);
            pnlPerfil.setVisible(true);
            
            pnlDashboard.setVisible(false);
            pnlEventos.setVisible(false);
            pnlUsuarios.setVisible(false);
            
            btnComprar.doClick();
        }
    }
    
    private void carregarDadosPerfil() {
        if (this.usuarioLogado != null) {
            fieldPUsuario.setText(this.usuarioLogado.getUsuario());
            fieldPEmail.setText(this.usuarioLogado.getEmail()); 
            fieldPNSenha.setText("");
        }
    }
    
    private void limparTelaCompra() {
        cprVQuantS.setValue(1);
        cprREvento.setText("...");
        cprRData.setText("...");
        cprRPUnit.setText("...");
        cprRVTotal.setText("R$ 0,00");
        tblEventosDisponiveis.clearSelection();
        fieldBuscaEvento.setText("");

        DefaultTableModel model = (DefaultTableModel) tblEventosDisponiveis.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tblEventosDisponiveis.setRowSorter(sorter);
    }
    
    private void carregarDadosUsuarios() {
        UsuarioDAO dao = new UsuarioDAO();
        ArrayList<Cliente> listaUsuarios = dao.listarUsuarios();
        
        fieldFUsuario.setText("");
        
        DefaultTableModel model = (DefaultTableModel) tableLUsuario.getModel();
        model.setRowCount(0);
        
        for (Cliente c : listaUsuarios) {
            model.addRow(new Object[]{ c.getId(), c.getUsuario(), c.getEmail(), c.getCargo() });
        }
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tableLUsuario.setRowSorter(sorter);
    }
    
    private void carregarTabelaEventos() {
        EventoDAO dao = new EventoDAO();
        ArrayList<Evento> lista = dao.listarEventos();

        DefaultTableModel model = (DefaultTableModel) tableLEvento.getModel();
        model.setRowCount(0);

        for (Evento e : lista) {
            String statusTexto = (e.getCapacidade() > 0) ? "Disponível" : "Esgotado";
            
            model.addRow(new Object[]{ e.getId(), e.getNome(), e.getData(), String.format("R$ %.2f", e.getPreco()), statusTexto });
        }

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tableLEvento.setRowSorter(sorter);
    }
    
    private void carregarMeusIngressos() {
        if (this.usuarioLogado == null) return;

        IngressoDAO dao = new IngressoDAO();
        
        ArrayList<Object[]> meusIngressos = dao.listarIngressosDoCliente(this.usuarioLogado.getId());

        DefaultTableModel model = (DefaultTableModel) tblMeusIngressos.getModel();
        model.setRowCount(0);

        for (Object[] linha : meusIngressos) {
            model.addRow(linha);
        }
    }
    
    public FMain() {
        initComponents();

        tblEventosDisponiveis.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) atualizarPainelResumo();
        });
        cprVQuantS.addChangeListener(e -> atualizarPainelResumo());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBackground = new javax.swing.JPanel();
        pnlLogin = new javax.swing.JPanel();
        bgLogin = new javax.swing.JPanel();
        bgItensL = new javax.swing.JPanel();
        fLoginL = new javax.swing.JTextField();
        txtSenhaL = new javax.swing.JLabel();
        txtLoginL = new javax.swing.JLabel();
        fSenhaL = new javax.swing.JPasswordField();
        pnlBLogin = new javax.swing.JPanel();
        btnLogin = new javax.swing.JButton();
        txtLogin = new javax.swing.JLabel();
        msgRegistrar = new javax.swing.JPanel();
        btnR = new javax.swing.JToggleButton();
        msgR = new javax.swing.JLabel();
        btnSSenha = new javax.swing.JToggleButton();
        txtTitleL = new javax.swing.JLabel();
        txtL = new javax.swing.JLabel();
        pnlRegistrar = new javax.swing.JPanel();
        bgRegistrar = new javax.swing.JPanel();
        bgItensR = new javax.swing.JPanel();
        fEmailR = new javax.swing.JTextField();
        txtSenhaR = new javax.swing.JLabel();
        txtEmailR = new javax.swing.JLabel();
        fUserR = new javax.swing.JTextField();
        txtUserR = new javax.swing.JLabel();
        fSenhaR = new javax.swing.JPasswordField();
        pnlBRegistrar = new javax.swing.JPanel();
        btnRegistrar = new javax.swing.JButton();
        txtRegistrar = new javax.swing.JLabel();
        msgLogin = new javax.swing.JPanel();
        btnL = new javax.swing.JToggleButton();
        msgL = new javax.swing.JLabel();
        txtTitleR = new javax.swing.JLabel();
        txtR = new javax.swing.JLabel();
        pnlMain = new javax.swing.JPanel();
        pnlSidebar = new javax.swing.JPanel();
        txtTitle = new javax.swing.JLabel();
        pnlComprar = new javax.swing.JPanel();
        txtComprar = new javax.swing.JLabel();
        btnComprar = new javax.swing.JButton();
        pnlMIngressos = new javax.swing.JPanel();
        txtMIngressos = new javax.swing.JLabel();
        btnMIngressos = new javax.swing.JButton();
        pnlPerfil = new javax.swing.JPanel();
        txtPerfil = new javax.swing.JLabel();
        btnPerfil = new javax.swing.JButton();
        pnlDashboard = new javax.swing.JPanel();
        txtDashboard = new javax.swing.JLabel();
        btnDashboard = new javax.swing.JButton();
        pnlEventos = new javax.swing.JPanel();
        txtEventos = new javax.swing.JLabel();
        btnEventos = new javax.swing.JButton();
        pnlUsuarios = new javax.swing.JPanel();
        txtUsuarios = new javax.swing.JLabel();
        btnUsuarios = new javax.swing.JButton();
        pnlSair = new javax.swing.JPanel();
        btnSair = new javax.swing.JButton();
        txtSair = new javax.swing.JLabel();
        pnlToggleMenu = new javax.swing.JPanel();
        txtToggleMenu = new javax.swing.JLabel();
        btnToggleMenu = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        cardComprar = new javax.swing.JPanel();
        ctComprar = new javax.swing.JPanel();
        titleComprar = new javax.swing.JLabel();
        cprVenda = new javax.swing.JPanel();
        cprVQuantS = new javax.swing.JSpinner();
        cprVEventoS = new javax.swing.JScrollPane();
        tblEventosDisponiveis = new javax.swing.JTable();
        cprVQuant = new javax.swing.JLabel();
        fieldBuscaEvento = new javax.swing.JTextField();
        txtClienteAtivo = new javax.swing.JLabel();
        cprResumo = new javax.swing.JPanel();
        cprRTitle = new javax.swing.JLabel();
        cprREText = new javax.swing.JLabel();
        cprREvento = new javax.swing.JLabel();
        cprRDText = new javax.swing.JLabel();
        cprRData = new javax.swing.JLabel();
        cprRPUText = new javax.swing.JLabel();
        cprRPUnit = new javax.swing.JLabel();
        cprRVTText = new javax.swing.JLabel();
        cprRVTotal = new javax.swing.JLabel();
        cprRConfirmar = new javax.swing.JPanel();
        txtConfirmarVenda = new javax.swing.JLabel();
        btnConfirmarVenda = new javax.swing.JButton();
        cprRCancelar = new javax.swing.JPanel();
        txtCancelarVenda = new javax.swing.JLabel();
        btnCancelarVenda = new javax.swing.JButton();
        cprRSeparator = new javax.swing.JSeparator();
        cardMIngressos = new javax.swing.JPanel();
        ctMIngressos = new javax.swing.JPanel();
        titleMIngressos = new javax.swing.JLabel();
        igVenda = new javax.swing.JPanel();
        scrollMeusIngressos = new javax.swing.JScrollPane();
        tblMeusIngressos = new javax.swing.JTable();
        evCIngresso = new javax.swing.JPanel();
        btnCIngresso = new javax.swing.JButton();
        txtCIngresso = new javax.swing.JLabel();
        cartPerfil = new javax.swing.JPanel();
        ctPerfil = new javax.swing.JPanel();
        titlePerfil = new javax.swing.JLabel();
        pfContent = new javax.swing.JPanel();
        txtPNSenha = new javax.swing.JLabel();
        txtPTitle = new javax.swing.JLabel();
        txtPUsuario = new javax.swing.JLabel();
        txtPEmail = new javax.swing.JLabel();
        fieldPUsuario = new javax.swing.JTextField();
        fieldPEmail = new javax.swing.JTextField();
        pnlPSConfigs = new javax.swing.JPanel();
        btnPSConfigs = new javax.swing.JButton();
        txtPSConfigs = new javax.swing.JLabel();
        fieldPNSenha = new javax.swing.JPasswordField();
        evEUsuario = new javax.swing.JPanel();
        btnEUsuario = new javax.swing.JButton();
        txtEUsuario = new javax.swing.JLabel();
        cardDashboard = new javax.swing.JPanel();
        ctDashboard = new javax.swing.JPanel();
        titleDashboard = new javax.swing.JLabel();
        dbVendas = new javax.swing.JPanel();
        dbvVTotais = new javax.swing.JLabel();
        dbvValor = new javax.swing.JLabel();
        dbIngressos = new javax.swing.JPanel();
        dviIVendidos = new javax.swing.JLabel();
        dviQuant = new javax.swing.JLabel();
        dbEventos = new javax.swing.JPanel();
        dveEAtivos = new javax.swing.JLabel();
        dveQuant = new javax.swing.JLabel();
        dbQClientes = new javax.swing.JPanel();
        dvqClientes = new javax.swing.JLabel();
        dvqQClientes = new javax.swing.JLabel();
        dbVRecentes = new javax.swing.JPanel();
        dvVRecentes = new javax.swing.JLabel();
        cprVEventoS1 = new javax.swing.JScrollPane();
        tblEventosDisponiveis1 = new javax.swing.JTable();
        cardEventos = new javax.swing.JPanel();
        ctEventos = new javax.swing.JPanel();
        titleEventos = new javax.swing.JLabel();
        evNEvento = new javax.swing.JPanel();
        btnNEvento = new javax.swing.JButton();
        txtNEvento = new javax.swing.JLabel();
        fieldFEvento = new javax.swing.JTextField();
        evLEvento = new javax.swing.JPanel();
        scrollLEvento = new javax.swing.JScrollPane();
        tableLEvento = new javax.swing.JTable();
        evREvento = new javax.swing.JPanel();
        btnREvento = new javax.swing.JButton();
        txtREvento = new javax.swing.JLabel();
        cardUsuarios = new javax.swing.JPanel();
        ctUsuarios = new javax.swing.JPanel();
        titleUsuarios = new javax.swing.JLabel();
        usNUsuario = new javax.swing.JPanel();
        btnNUsuario = new javax.swing.JButton();
        txtNUsuario = new javax.swing.JLabel();
        fieldFUsuario = new javax.swing.JTextField();
        usLUsuario = new javax.swing.JPanel();
        scrollLUsuario = new javax.swing.JScrollPane();
        tableLUsuario = new javax.swing.JTable();
        usRUsuario = new javax.swing.JPanel();
        btnRUsuario = new javax.swing.JButton();
        txtRUsuario = new javax.swing.JLabel();
        pnlSSenha = new javax.swing.JPanel();
        bgSSenha = new javax.swing.JPanel();
        bgItensSS = new javax.swing.JPanel();
        fEmailSS = new javax.swing.JTextField();
        txtNSenhaSS = new javax.swing.JLabel();
        txtEmailSS = new javax.swing.JLabel();
        fUserSS = new javax.swing.JTextField();
        txtUserSS = new javax.swing.JLabel();
        fNSenhaSS = new javax.swing.JPasswordField();
        pnlBRSenha = new javax.swing.JPanel();
        btnRSEnha = new javax.swing.JButton();
        txtRSenha = new javax.swing.JLabel();
        txtTitleSS = new javax.swing.JLabel();
        msgSSCL = new javax.swing.JPanel();
        btnSSR = new javax.swing.JToggleButton();
        msgSSR = new javax.swing.JLabel();
        msgSSL = new javax.swing.JLabel();
        btnSSL = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gerenciamento de Ingressos");
        setMinimumSize(new java.awt.Dimension(960, 540));
        setResizable(false);

        pnlBackground.setBackground(new java.awt.Color(243, 244, 246));
        pnlBackground.setLayout(new java.awt.CardLayout());

        pnlLogin.setBackground(new java.awt.Color(243, 244, 246));
        pnlLogin.setMinimumSize(new java.awt.Dimension(960, 540));
        pnlLogin.setLayout(new java.awt.GridBagLayout());

        bgLogin.setBackground(new java.awt.Color(255, 255, 255));
        bgLogin.setMinimumSize(new java.awt.Dimension(640, 480));
        bgLogin.setPreferredSize(new java.awt.Dimension(400, 473));
        bgLogin.setLayout(null);

        bgItensL.setBackground(new java.awt.Color(255, 255, 255));
        bgItensL.setLayout(null);

        fLoginL.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        fLoginL.setToolTipText("");
        bgItensL.add(fLoginL);
        fLoginL.setBounds(0, 25, 320, 46);

        txtSenhaL.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtSenhaL.setText("Senha:");
        bgItensL.add(txtSenhaL);
        txtSenhaL.setBounds(0, 91, 320, 17);

        txtLoginL.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtLoginL.setForeground(new java.awt.Color(31, 41, 55));
        txtLoginL.setText("Usuário/E-mail:");
        bgItensL.add(txtLoginL);
        txtLoginL.setBounds(0, 0, 320, 17);

        fSenhaL.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        fSenhaL.setMaximumSize(new java.awt.Dimension(320, 46));
        fSenhaL.setMinimumSize(new java.awt.Dimension(320, 46));
        fSenhaL.setPreferredSize(new java.awt.Dimension(320, 46));
        bgItensL.add(fSenhaL);
        fSenhaL.setBounds(0, 116, 320, 46);

        pnlBLogin.setBackground(new java.awt.Color(79, 70, 229));
        pnlBLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlBLogin.setMaximumSize(new java.awt.Dimension(220, 44));
        pnlBLogin.setLayout(null);

        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnLogin.setFocusPainted(false);
        btnLogin.setMaximumSize(new java.awt.Dimension(220, 44));
        btnLogin.setMinimumSize(new java.awt.Dimension(220, 44));
        btnLogin.setPreferredSize(new java.awt.Dimension(220, 44));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        pnlBLogin.add(btnLogin);
        btnLogin.setBounds(0, 0, 320, 44);

        txtLogin.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtLogin.setForeground(new java.awt.Color(255, 255, 255));
        txtLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtLogin.setText("Entrar");
        txtLogin.setToolTipText("");
        txtLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtLogin.setMaximumSize(new java.awt.Dimension(190, 20));
        txtLogin.setMinimumSize(new java.awt.Dimension(190, 20));
        txtLogin.setPreferredSize(new java.awt.Dimension(190, 20));
        txtLogin.setRequestFocusEnabled(false);
        pnlBLogin.add(txtLogin);
        txtLogin.setBounds(15, 12, 290, 20);

        bgItensL.add(pnlBLogin);
        pnlBLogin.setBounds(0, 192, 320, 44);

        bgLogin.add(bgItensL);
        bgItensL.setBounds(40, 140, 320, 236);

        msgRegistrar.setBackground(new java.awt.Color(255, 255, 255));
        msgRegistrar.setLayout(null);

        btnR.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnR.setForeground(new java.awt.Color(79, 70, 229));
        btnR.setText("Cadastre-se");
        btnR.setBorderPainted(false);
        btnR.setContentAreaFilled(false);
        btnR.setFocusPainted(false);
        btnR.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRActionPerformed(evt);
            }
        });
        msgRegistrar.add(btnR);
        btnR.setBounds(158, 2, 98, 18);

        msgR.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        msgR.setForeground(new java.awt.Color(107, 114, 128));
        msgR.setText("Não tem conta?");
        msgRegistrar.add(msgR);
        msgR.setBounds(65, 0, 89, 23);

        btnSSenha.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnSSenha.setForeground(new java.awt.Color(79, 70, 229));
        btnSSenha.setText("Esqueceu a senha?");
        btnSSenha.setBorderPainted(false);
        btnSSenha.setContentAreaFilled(false);
        btnSSenha.setFocusPainted(false);
        btnSSenha.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnSSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSSenhaActionPerformed(evt);
            }
        });
        msgRegistrar.add(btnSSenha);
        btnSSenha.setBounds(91, 25, 139, 18);

        bgLogin.add(msgRegistrar);
        msgRegistrar.setBounds(40, 396, 320, 46);

        txtTitleL.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        txtTitleL.setForeground(new java.awt.Color(31, 41, 55));
        txtTitleL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconTitle.png"))); // NOI18N
        txtTitleL.setText("Ingressos");
        txtTitleL.setIconTextGap(10);
        bgLogin.add(txtTitleL);
        txtTitleL.setBounds(121, 40, 159, 32);

        txtL.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        txtL.setForeground(new java.awt.Color(107, 114, 128));
        txtL.setText("Bem-vindo de volta! Acesse a sua conta.");
        txtL.setMaximumSize(new java.awt.Dimension(228, 23));
        txtL.setMinimumSize(new java.awt.Dimension(228, 23));
        txtL.setPreferredSize(new java.awt.Dimension(228, 23));
        bgLogin.add(txtL);
        txtL.setBounds(86, 82, 228, 23);

        pnlLogin.add(bgLogin, new java.awt.GridBagConstraints());

        pnlBackground.add(pnlLogin, "login");

        pnlRegistrar.setBackground(new java.awt.Color(243, 244, 246));
        pnlRegistrar.setMinimumSize(new java.awt.Dimension(960, 540));
        pnlRegistrar.setPreferredSize(new java.awt.Dimension(960, 540));
        pnlRegistrar.setLayout(new java.awt.GridBagLayout());

        bgRegistrar.setBackground(new java.awt.Color(255, 255, 255));
        bgRegistrar.setMaximumSize(new java.awt.Dimension(400, 473));
        bgRegistrar.setMinimumSize(new java.awt.Dimension(400, 473));
        bgRegistrar.setPreferredSize(new java.awt.Dimension(400, 473));
        bgRegistrar.setLayout(null);

        bgItensR.setBackground(new java.awt.Color(255, 255, 255));
        bgItensR.setLayout(null);

        fEmailR.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        fEmailR.setToolTipText("");
        bgItensR.add(fEmailR);
        fEmailR.setBounds(0, 116, 320, 46);

        txtSenhaR.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtSenhaR.setText("Senha:");
        bgItensR.add(txtSenhaR);
        txtSenhaR.setBounds(0, 182, 320, 17);

        txtEmailR.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtEmailR.setForeground(new java.awt.Color(31, 41, 55));
        txtEmailR.setText("E-mail:");
        bgItensR.add(txtEmailR);
        txtEmailR.setBounds(0, 91, 320, 17);

        fUserR.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        fUserR.setToolTipText("");
        bgItensR.add(fUserR);
        fUserR.setBounds(0, 25, 320, 46);

        txtUserR.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtUserR.setText("Usuário:");
        bgItensR.add(txtUserR);
        txtUserR.setBounds(0, 0, 320, 17);

        fSenhaR.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        fSenhaR.setMaximumSize(new java.awt.Dimension(320, 46));
        fSenhaR.setMinimumSize(new java.awt.Dimension(320, 46));
        fSenhaR.setPreferredSize(new java.awt.Dimension(320, 46));
        bgItensR.add(fSenhaR);
        fSenhaR.setBounds(0, 207, 320, 46);

        pnlBRegistrar.setBackground(new java.awt.Color(79, 70, 229));
        pnlBRegistrar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlBRegistrar.setMaximumSize(new java.awt.Dimension(220, 44));
        pnlBRegistrar.setLayout(null);

        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setContentAreaFilled(false);
        btnRegistrar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setMaximumSize(new java.awt.Dimension(220, 44));
        btnRegistrar.setMinimumSize(new java.awt.Dimension(220, 44));
        btnRegistrar.setPreferredSize(new java.awt.Dimension(220, 44));
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });
        pnlBRegistrar.add(btnRegistrar);
        btnRegistrar.setBounds(0, 0, 320, 44);

        txtRegistrar.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        txtRegistrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtRegistrar.setText("Registrar");
        txtRegistrar.setToolTipText("");
        txtRegistrar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtRegistrar.setMaximumSize(new java.awt.Dimension(190, 20));
        txtRegistrar.setMinimumSize(new java.awt.Dimension(190, 20));
        txtRegistrar.setPreferredSize(new java.awt.Dimension(190, 20));
        txtRegistrar.setRequestFocusEnabled(false);
        pnlBRegistrar.add(txtRegistrar);
        txtRegistrar.setBounds(15, 12, 290, 20);

        bgItensR.add(pnlBRegistrar);
        pnlBRegistrar.setBounds(0, 277, 320, 44);

        bgRegistrar.add(bgItensR);
        bgItensR.setBounds(40, 96, 320, 321);

        msgLogin.setBackground(new java.awt.Color(255, 255, 255));
        msgLogin.setLayout(null);

        btnL.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnL.setForeground(new java.awt.Color(79, 70, 229));
        btnL.setText("Faça login");
        btnL.setBorderPainted(false);
        btnL.setContentAreaFilled(false);
        btnL.setFocusPainted(false);
        btnL.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLActionPerformed(evt);
            }
        });
        msgLogin.add(btnL);
        btnL.setBounds(174, 2, 85, 18);

        msgL.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        msgL.setForeground(new java.awt.Color(107, 114, 128));
        msgL.setText("Já tem uma conta?");
        msgL.setMaximumSize(new java.awt.Dimension(109, 23));
        msgL.setMinimumSize(new java.awt.Dimension(109, 23));
        msgL.setPreferredSize(new java.awt.Dimension(109, 23));
        msgLogin.add(msgL);
        msgL.setBounds(61, 0, 109, 23);

        bgRegistrar.add(msgLogin);
        msgLogin.setBounds(40, 430, 320, 23);

        txtTitleR.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        txtTitleR.setForeground(new java.awt.Color(31, 41, 55));
        txtTitleR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconTitle.png"))); // NOI18N
        txtTitleR.setText("Ingressos");
        txtTitleR.setIconTextGap(10);
        bgRegistrar.add(txtTitleR);
        txtTitleR.setBounds(121, 20, 159, 32);

        txtR.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        txtR.setForeground(new java.awt.Color(107, 114, 128));
        txtR.setText("Crie sua conta e comece a gerenciar.");
        txtR.setMaximumSize(new java.awt.Dimension(213, 23));
        txtR.setMinimumSize(new java.awt.Dimension(213, 23));
        txtR.setPreferredSize(new java.awt.Dimension(213, 23));
        bgRegistrar.add(txtR);
        txtR.setBounds(94, 62, 213, 23);

        pnlRegistrar.add(bgRegistrar, new java.awt.GridBagConstraints());

        pnlBackground.add(pnlRegistrar, "registrar");

        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlSidebar.setBackground(new java.awt.Color(30, 41, 59));
        pnlSidebar.setMinimumSize(new java.awt.Dimension(260, 540));
        pnlSidebar.setPreferredSize(new java.awt.Dimension(260, 540));
        pnlSidebar.setLayout(null);

        txtTitle.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        txtTitle.setForeground(new java.awt.Color(255, 255, 255));
        txtTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconTitle.png"))); // NOI18N
        txtTitle.setText("Ingressos");
        txtTitle.setIconTextGap(10);
        pnlSidebar.add(txtTitle);
        txtTitle.setBounds(20, 20, 159, 32);

        pnlComprar.setBackground(new java.awt.Color(51, 65, 85));
        pnlComprar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlComprar.setMaximumSize(new java.awt.Dimension(220, 44));
        pnlComprar.setMinimumSize(new java.awt.Dimension(220, 44));
        pnlComprar.setLayout(null);

        txtComprar.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtComprar.setForeground(new java.awt.Color(255, 255, 255));
        txtComprar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconADashboard.png"))); // NOI18N
        txtComprar.setText("Comprar");
        txtComprar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtComprar.setMaximumSize(new java.awt.Dimension(190, 20));
        txtComprar.setMinimumSize(new java.awt.Dimension(190, 20));
        txtComprar.setPreferredSize(new java.awt.Dimension(190, 20));
        pnlComprar.add(txtComprar);
        txtComprar.setBounds(15, 12, 190, 20);

        btnComprar.setBorderPainted(false);
        btnComprar.setContentAreaFilled(false);
        btnComprar.setFocusPainted(false);
        btnComprar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComprarActionPerformed(evt);
            }
        });
        pnlComprar.add(btnComprar);
        btnComprar.setBounds(0, 0, 220, 44);

        pnlSidebar.add(pnlComprar);
        pnlComprar.setBounds(20, 92, 220, 44);

        pnlMIngressos.setBackground(new java.awt.Color(30, 41, 59));
        pnlMIngressos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlMIngressos.setMaximumSize(new java.awt.Dimension(220, 44));
        pnlMIngressos.setMinimumSize(new java.awt.Dimension(220, 44));
        pnlMIngressos.setLayout(null);

        txtMIngressos.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtMIngressos.setForeground(new java.awt.Color(148, 163, 184));
        txtMIngressos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconMIngressos.png"))); // NOI18N
        txtMIngressos.setText("Meus ingressos");
        txtMIngressos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtMIngressos.setMaximumSize(new java.awt.Dimension(190, 20));
        txtMIngressos.setMinimumSize(new java.awt.Dimension(190, 20));
        txtMIngressos.setPreferredSize(new java.awt.Dimension(190, 20));
        pnlMIngressos.add(txtMIngressos);
        txtMIngressos.setBounds(15, 12, 190, 20);

        btnMIngressos.setBorderPainted(false);
        btnMIngressos.setContentAreaFilled(false);
        btnMIngressos.setFocusPainted(false);
        btnMIngressos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMIngressosActionPerformed(evt);
            }
        });
        pnlMIngressos.add(btnMIngressos);
        btnMIngressos.setBounds(0, 0, 220, 44);

        pnlSidebar.add(pnlMIngressos);
        pnlMIngressos.setBounds(20, 141, 220, 44);

        pnlPerfil.setBackground(new java.awt.Color(30, 41, 59));
        pnlPerfil.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlPerfil.setMaximumSize(new java.awt.Dimension(220, 44));
        pnlPerfil.setMinimumSize(new java.awt.Dimension(220, 44));
        pnlPerfil.setLayout(null);

        txtPerfil.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtPerfil.setForeground(new java.awt.Color(148, 163, 184));
        txtPerfil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconPerfil.png"))); // NOI18N
        txtPerfil.setText("Perfil");
        txtPerfil.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtPerfil.setMaximumSize(new java.awt.Dimension(190, 20));
        txtPerfil.setMinimumSize(new java.awt.Dimension(190, 20));
        txtPerfil.setPreferredSize(new java.awt.Dimension(190, 20));
        pnlPerfil.add(txtPerfil);
        txtPerfil.setBounds(15, 12, 190, 20);

        btnPerfil.setBorderPainted(false);
        btnPerfil.setContentAreaFilled(false);
        btnPerfil.setFocusPainted(false);
        btnPerfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerfilActionPerformed(evt);
            }
        });
        pnlPerfil.add(btnPerfil);
        btnPerfil.setBounds(0, 0, 220, 44);

        pnlSidebar.add(pnlPerfil);
        pnlPerfil.setBounds(20, 190, 220, 44);

        pnlDashboard.setBackground(new java.awt.Color(51, 65, 85));
        pnlDashboard.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlDashboard.setMaximumSize(new java.awt.Dimension(220, 44));
        pnlDashboard.setMinimumSize(new java.awt.Dimension(220, 44));
        pnlDashboard.setLayout(null);

        txtDashboard.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtDashboard.setForeground(new java.awt.Color(255, 255, 255));
        txtDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconADashboard.png"))); // NOI18N
        txtDashboard.setText("Dashboard");
        txtDashboard.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtDashboard.setMaximumSize(new java.awt.Dimension(190, 20));
        txtDashboard.setMinimumSize(new java.awt.Dimension(190, 20));
        txtDashboard.setPreferredSize(new java.awt.Dimension(190, 20));
        pnlDashboard.add(txtDashboard);
        txtDashboard.setBounds(15, 12, 190, 20);

        btnDashboard.setBorderPainted(false);
        btnDashboard.setContentAreaFilled(false);
        btnDashboard.setFocusPainted(false);
        btnDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashboardActionPerformed(evt);
            }
        });
        pnlDashboard.add(btnDashboard);
        btnDashboard.setBounds(0, 0, 220, 44);

        pnlSidebar.add(pnlDashboard);
        pnlDashboard.setBounds(20, 92, 220, 44);

        pnlEventos.setBackground(new java.awt.Color(30, 41, 59));
        pnlEventos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlEventos.setMaximumSize(new java.awt.Dimension(220, 44));
        pnlEventos.setMinimumSize(new java.awt.Dimension(220, 44));
        pnlEventos.setLayout(null);

        txtEventos.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtEventos.setForeground(new java.awt.Color(148, 163, 184));
        txtEventos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconEventos.png"))); // NOI18N
        txtEventos.setText("Eventos");
        txtEventos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtEventos.setMaximumSize(new java.awt.Dimension(190, 20));
        txtEventos.setMinimumSize(new java.awt.Dimension(190, 20));
        txtEventos.setPreferredSize(new java.awt.Dimension(190, 20));
        pnlEventos.add(txtEventos);
        txtEventos.setBounds(15, 12, 190, 20);

        btnEventos.setBorderPainted(false);
        btnEventos.setContentAreaFilled(false);
        btnEventos.setFocusPainted(false);
        btnEventos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEventosActionPerformed(evt);
            }
        });
        pnlEventos.add(btnEventos);
        btnEventos.setBounds(0, 0, 220, 44);

        pnlSidebar.add(pnlEventos);
        pnlEventos.setBounds(20, 141, 220, 44);

        pnlUsuarios.setBackground(new java.awt.Color(30, 41, 59));
        pnlUsuarios.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlUsuarios.setMaximumSize(new java.awt.Dimension(220, 44));
        pnlUsuarios.setMinimumSize(new java.awt.Dimension(220, 44));
        pnlUsuarios.setLayout(null);

        txtUsuarios.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtUsuarios.setForeground(new java.awt.Color(148, 163, 184));
        txtUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconUsuarios.png"))); // NOI18N
        txtUsuarios.setText("Usuários");
        txtUsuarios.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtUsuarios.setMaximumSize(new java.awt.Dimension(190, 20));
        txtUsuarios.setMinimumSize(new java.awt.Dimension(190, 20));
        txtUsuarios.setPreferredSize(new java.awt.Dimension(190, 20));
        pnlUsuarios.add(txtUsuarios);
        txtUsuarios.setBounds(15, 12, 190, 20);

        btnUsuarios.setBorderPainted(false);
        btnUsuarios.setContentAreaFilled(false);
        btnUsuarios.setFocusPainted(false);
        btnUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsuariosActionPerformed(evt);
            }
        });
        pnlUsuarios.add(btnUsuarios);
        btnUsuarios.setBounds(0, 0, 220, 44);

        pnlSidebar.add(pnlUsuarios);
        pnlUsuarios.setBounds(20, 190, 220, 44);

        pnlSair.setBackground(new java.awt.Color(79, 70, 229));
        pnlSair.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlSair.setMaximumSize(new java.awt.Dimension(220, 44));
        pnlSair.setLayout(null);

        btnSair.setBorderPainted(false);
        btnSair.setContentAreaFilled(false);
        btnSair.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnSair.setFocusPainted(false);
        btnSair.setMaximumSize(new java.awt.Dimension(220, 44));
        btnSair.setMinimumSize(new java.awt.Dimension(220, 44));
        btnSair.setPreferredSize(new java.awt.Dimension(220, 44));
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });
        pnlSair.add(btnSair);
        btnSair.setBounds(0, 0, 220, 44);

        txtSair.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtSair.setForeground(new java.awt.Color(255, 255, 255));
        txtSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconSair.png"))); // NOI18N
        txtSair.setText("Deslogar");
        txtSair.setToolTipText("");
        txtSair.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtSair.setMaximumSize(new java.awt.Dimension(190, 20));
        txtSair.setMinimumSize(new java.awt.Dimension(190, 20));
        txtSair.setPreferredSize(new java.awt.Dimension(190, 20));
        txtSair.setRequestFocusEnabled(false);
        pnlSair.add(txtSair);
        txtSair.setBounds(15, 12, 190, 20);

        pnlSidebar.add(pnlSair);
        pnlSair.setBounds(20, 480, 220, 44);

        pnlToggleMenu.setBackground(new java.awt.Color(51, 65, 85));
        pnlToggleMenu.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlToggleMenu.setMaximumSize(new java.awt.Dimension(44, 44));
        pnlToggleMenu.setMinimumSize(new java.awt.Dimension(44, 44));
        pnlToggleMenu.setLayout(null);

        txtToggleMenu.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtToggleMenu.setForeground(new java.awt.Color(255, 255, 255));
        txtToggleMenu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtToggleMenu.setText("<");
        txtToggleMenu.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtToggleMenu.setMaximumSize(new java.awt.Dimension(44, 44));
        txtToggleMenu.setMinimumSize(new java.awt.Dimension(44, 44));
        txtToggleMenu.setPreferredSize(new java.awt.Dimension(44, 44));
        txtToggleMenu.setRequestFocusEnabled(false);
        pnlToggleMenu.add(txtToggleMenu);
        txtToggleMenu.setBounds(0, 0, 44, 44);

        btnToggleMenu.setBorderPainted(false);
        btnToggleMenu.setContentAreaFilled(false);
        btnToggleMenu.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnToggleMenu.setFocusPainted(false);
        btnToggleMenu.setMaximumSize(new java.awt.Dimension(44, 44));
        btnToggleMenu.setMinimumSize(new java.awt.Dimension(44, 44));
        btnToggleMenu.setPreferredSize(new java.awt.Dimension(44, 44));
        btnToggleMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggleMenuActionPerformed(evt);
            }
        });
        pnlToggleMenu.add(btnToggleMenu);
        btnToggleMenu.setBounds(0, 0, 44, 44);

        pnlSidebar.add(pnlToggleMenu);
        pnlToggleMenu.setBounds(196, 14, 44, 44);

        pnlMain.add(pnlSidebar, java.awt.BorderLayout.LINE_START);

        pnlContent.setBackground(new java.awt.Color(243, 244, 246));
        pnlContent.setLayout(new java.awt.CardLayout());

        cardComprar.setBackground(new java.awt.Color(243, 244, 246));
        cardComprar.setMaximumSize(new java.awt.Dimension(640, 480));
        cardComprar.setMinimumSize(new java.awt.Dimension(640, 480));
        cardComprar.setPreferredSize(new java.awt.Dimension(640, 480));
        cardComprar.setLayout(new java.awt.GridBagLayout());

        ctComprar.setBackground(new java.awt.Color(243, 244, 246));
        ctComprar.setMaximumSize(new java.awt.Dimension(640, 480));
        ctComprar.setMinimumSize(new java.awt.Dimension(640, 480));
        ctComprar.setPreferredSize(new java.awt.Dimension(640, 480));
        ctComprar.setLayout(null);

        titleComprar.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        titleComprar.setForeground(new java.awt.Color(31, 41, 55));
        titleComprar.setText("Nova Venda");
        ctComprar.add(titleComprar);
        titleComprar.setBounds(0, 0, 137, 32);

        cprVenda.setBackground(new java.awt.Color(255, 255, 255));
        cprVenda.setMinimumSize(new java.awt.Dimension(310, 418));
        cprVenda.setLayout(null);

        cprVQuantS.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        cprVQuantS.setMaximumSize(new java.awt.Dimension(64, 30));
        cprVQuantS.setMinimumSize(new java.awt.Dimension(64, 30));
        cprVQuantS.setPreferredSize(new java.awt.Dimension(64, 30));
        cprVenda.add(cprVQuantS);
        cprVQuantS.setBounds(120, 365, 64, 30);
        cprVQuantS.getAccessibleContext().setAccessibleName("");

        cprVEventoS.setBackground(new java.awt.Color(243, 244, 246));
        cprVEventoS.setMinimumSize(new java.awt.Dimension(270, 170));
        cprVEventoS.setPreferredSize(new java.awt.Dimension(270, 170));

        tblEventosDisponiveis.setBackground(new java.awt.Color(243, 244, 246));
        tblEventosDisponiveis.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Evento", "Data", "Preço"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblEventosDisponiveis.setMinimumSize(new java.awt.Dimension(270, 170));
        tblEventosDisponiveis.setPreferredSize(new java.awt.Dimension(270, 170));
        cprVEventoS.setViewportView(tblEventosDisponiveis);

        cprVenda.add(cprVEventoS);
        cprVEventoS.setBounds(20, 100, 270, 220);

        cprVQuant.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cprVQuant.setForeground(new java.awt.Color(31, 41, 55));
        cprVQuant.setText("Quantidade:");
        cprVenda.add(cprVQuant);
        cprVQuant.setBounds(20, 370, 85, 19);

        fieldBuscaEvento.setMaximumSize(new java.awt.Dimension(270, 22));
        fieldBuscaEvento.setMinimumSize(new java.awt.Dimension(270, 22));
        fieldBuscaEvento.setPreferredSize(new java.awt.Dimension(270, 22));
        fieldBuscaEvento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldBuscaEventoActionPerformed(evt);
            }
        });
        cprVenda.add(fieldBuscaEvento);
        fieldBuscaEvento.setBounds(20, 59, 270, 22);

        txtClienteAtivo.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtClienteAtivo.setForeground(new java.awt.Color(79, 70, 229));
        txtClienteAtivo.setText("Comprando Como: ");
        txtClienteAtivo.setMaximumSize(new java.awt.Dimension(270, 19));
        txtClienteAtivo.setMinimumSize(new java.awt.Dimension(270, 19));
        txtClienteAtivo.setPreferredSize(new java.awt.Dimension(270, 19));
        cprVenda.add(txtClienteAtivo);
        txtClienteAtivo.setBounds(20, 20, 270, 19);

        ctComprar.add(cprVenda);
        cprVenda.setBounds(0, 62, 310, 418);

        cprResumo.setBackground(new java.awt.Color(255, 255, 255));
        cprResumo.setMinimumSize(new java.awt.Dimension(310, 418));
        cprResumo.setLayout(null);

        cprRTitle.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        cprRTitle.setForeground(new java.awt.Color(31, 41, 55));
        cprRTitle.setText("Resumo da compra:");
        cprResumo.add(cprRTitle);
        cprRTitle.setBounds(20, 20, 177, 21);

        cprREText.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cprREText.setForeground(new java.awt.Color(31, 41, 55));
        cprREText.setText("Evento selecionado:");
        cprResumo.add(cprREText);
        cprREText.setBounds(20, 59, 144, 19);

        cprREvento.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cprREvento.setForeground(new java.awt.Color(148, 163, 184));
        cprREvento.setText("...");
        cprREvento.setMaximumSize(new java.awt.Dimension(270, 19));
        cprREvento.setMinimumSize(new java.awt.Dimension(270, 19));
        cprREvento.setPreferredSize(new java.awt.Dimension(270, 19));
        cprResumo.add(cprREvento);
        cprREvento.setBounds(20, 89, 270, 19);

        cprRDText.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cprRDText.setForeground(new java.awt.Color(31, 41, 55));
        cprRDText.setText("Data:");
        cprResumo.add(cprRDText);
        cprRDText.setBounds(20, 119, 144, 19);

        cprRData.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cprRData.setForeground(new java.awt.Color(148, 163, 184));
        cprRData.setText("...");
        cprRData.setMaximumSize(new java.awt.Dimension(270, 19));
        cprRData.setMinimumSize(new java.awt.Dimension(270, 19));
        cprRData.setPreferredSize(new java.awt.Dimension(270, 19));
        cprResumo.add(cprRData);
        cprRData.setBounds(20, 139, 270, 19);

        cprRPUText.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cprRPUText.setForeground(new java.awt.Color(31, 41, 55));
        cprRPUText.setText("Preço Unitário:");
        cprResumo.add(cprRPUText);
        cprRPUText.setBounds(20, 169, 144, 19);

        cprRPUnit.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cprRPUnit.setForeground(new java.awt.Color(148, 163, 184));
        cprRPUnit.setText("...");
        cprRPUnit.setMaximumSize(new java.awt.Dimension(270, 19));
        cprRPUnit.setMinimumSize(new java.awt.Dimension(270, 19));
        cprRPUnit.setPreferredSize(new java.awt.Dimension(270, 19));
        cprResumo.add(cprRPUnit);
        cprRPUnit.setBounds(20, 189, 270, 19);

        cprRVTText.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cprRVTText.setForeground(new java.awt.Color(31, 41, 55));
        cprRVTText.setText("Valor total:");
        cprResumo.add(cprRVTText);
        cprRVTText.setBounds(20, 230, 144, 19);

        cprRVTotal.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        cprRVTotal.setForeground(new java.awt.Color(79, 70, 229));
        cprRVTotal.setText("R$ 0,00");
        cprRVTotal.setMaximumSize(new java.awt.Dimension(270, 19));
        cprRVTotal.setMinimumSize(new java.awt.Dimension(270, 19));
        cprRVTotal.setPreferredSize(new java.awt.Dimension(270, 19));
        cprResumo.add(cprRVTotal);
        cprRVTotal.setBounds(20, 255, 270, 19);

        cprRConfirmar.setBackground(new java.awt.Color(16, 185, 129));
        cprRConfirmar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        cprRConfirmar.setMaximumSize(new java.awt.Dimension(220, 44));
        cprRConfirmar.setMinimumSize(new java.awt.Dimension(220, 44));
        cprRConfirmar.setLayout(null);

        txtConfirmarVenda.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtConfirmarVenda.setForeground(new java.awt.Color(255, 255, 255));
        txtConfirmarVenda.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtConfirmarVenda.setText("Confirmar");
        txtConfirmarVenda.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtConfirmarVenda.setMaximumSize(new java.awt.Dimension(190, 20));
        txtConfirmarVenda.setMinimumSize(new java.awt.Dimension(190, 20));
        txtConfirmarVenda.setPreferredSize(new java.awt.Dimension(190, 20));
        cprRConfirmar.add(txtConfirmarVenda);
        txtConfirmarVenda.setBounds(15, 12, 190, 20);

        btnConfirmarVenda.setBorderPainted(false);
        btnConfirmarVenda.setContentAreaFilled(false);
        btnConfirmarVenda.setFocusPainted(false);
        btnConfirmarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarVendaActionPerformed(evt);
            }
        });
        cprRConfirmar.add(btnConfirmarVenda);
        btnConfirmarVenda.setBounds(0, 0, 220, 44);

        cprResumo.add(cprRConfirmar);
        cprRConfirmar.setBounds(42, 300, 220, 44);

        cprRCancelar.setBackground(new java.awt.Color(239, 68, 68));
        cprRCancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        cprRCancelar.setMaximumSize(new java.awt.Dimension(220, 44));
        cprRCancelar.setMinimumSize(new java.awt.Dimension(220, 44));
        cprRCancelar.setLayout(null);

        txtCancelarVenda.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtCancelarVenda.setForeground(new java.awt.Color(255, 255, 255));
        txtCancelarVenda.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtCancelarVenda.setText("Cancelar");
        txtCancelarVenda.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtCancelarVenda.setMaximumSize(new java.awt.Dimension(190, 20));
        txtCancelarVenda.setMinimumSize(new java.awt.Dimension(190, 20));
        txtCancelarVenda.setPreferredSize(new java.awt.Dimension(190, 20));
        cprRCancelar.add(txtCancelarVenda);
        txtCancelarVenda.setBounds(15, 12, 190, 20);

        btnCancelarVenda.setBorderPainted(false);
        btnCancelarVenda.setContentAreaFilled(false);
        btnCancelarVenda.setFocusPainted(false);
        btnCancelarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarVendaActionPerformed(evt);
            }
        });
        cprRCancelar.add(btnCancelarVenda);
        btnCancelarVenda.setBounds(0, 0, 220, 44);

        cprResumo.add(cprRCancelar);
        cprRCancelar.setBounds(42, 350, 220, 44);

        cprRSeparator.setForeground(new java.awt.Color(229, 231, 235));
        cprRSeparator.setMaximumSize(new java.awt.Dimension(310, 10));
        cprRSeparator.setMinimumSize(new java.awt.Dimension(310, 10));
        cprRSeparator.setPreferredSize(new java.awt.Dimension(310, 10));
        cprResumo.add(cprRSeparator);
        cprRSeparator.setBounds(20, 220, 270, 10);

        ctComprar.add(cprResumo);
        cprResumo.setBounds(330, 62, 310, 418);

        cardComprar.add(ctComprar, new java.awt.GridBagConstraints());

        pnlContent.add(cardComprar, "comprar");

        cardMIngressos.setBackground(new java.awt.Color(243, 244, 246));
        cardMIngressos.setMaximumSize(new java.awt.Dimension(640, 480));
        cardMIngressos.setMinimumSize(new java.awt.Dimension(640, 480));
        cardMIngressos.setLayout(new java.awt.GridBagLayout());

        ctMIngressos.setBackground(new java.awt.Color(243, 244, 246));
        ctMIngressos.setMinimumSize(new java.awt.Dimension(640, 480));
        ctMIngressos.setPreferredSize(new java.awt.Dimension(640, 480));
        ctMIngressos.setLayout(null);

        titleMIngressos.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        titleMIngressos.setForeground(new java.awt.Color(31, 41, 55));
        titleMIngressos.setText("Meus ingressos");
        ctMIngressos.add(titleMIngressos);
        titleMIngressos.setBounds(0, 0, 182, 32);

        igVenda.setBackground(new java.awt.Color(255, 255, 255));
        igVenda.setMinimumSize(new java.awt.Dimension(640, 418));
        igVenda.setPreferredSize(new java.awt.Dimension(640, 418));
        igVenda.setLayout(null);

        scrollMeusIngressos.setBackground(new java.awt.Color(243, 244, 246));
        scrollMeusIngressos.setMaximumSize(new java.awt.Dimension(640, 418));
        scrollMeusIngressos.setMinimumSize(new java.awt.Dimension(640, 418));
        scrollMeusIngressos.setPreferredSize(new java.awt.Dimension(640, 418));

        tblMeusIngressos.setBackground(new java.awt.Color(243, 244, 246));
        tblMeusIngressos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Evento", "Data do Evento", "Quantidade", "Total Pago", "Data da Compra"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollMeusIngressos.setViewportView(tblMeusIngressos);

        igVenda.add(scrollMeusIngressos);
        scrollMeusIngressos.setBounds(0, 0, 640, 418);

        ctMIngressos.add(igVenda);
        igVenda.setBounds(0, 62, 640, 418);

        evCIngresso.setBackground(new java.awt.Color(79, 70, 229));
        evCIngresso.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        evCIngresso.setMaximumSize(new java.awt.Dimension(100, 37));
        evCIngresso.setMinimumSize(new java.awt.Dimension(100, 37));
        evCIngresso.setLayout(null);

        btnCIngresso.setBorderPainted(false);
        btnCIngresso.setContentAreaFilled(false);
        btnCIngresso.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnCIngresso.setFocusPainted(false);
        btnCIngresso.setMaximumSize(new java.awt.Dimension(100, 37));
        btnCIngresso.setMinimumSize(new java.awt.Dimension(100, 37));
        btnCIngresso.setPreferredSize(new java.awt.Dimension(100, 37));
        btnCIngresso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCIngressoActionPerformed(evt);
            }
        });
        evCIngresso.add(btnCIngresso);
        btnCIngresso.setBounds(0, 0, 100, 37);

        txtCIngresso.setFont(new java.awt.Font("SansSerif", 1, 8)); // NOI18N
        txtCIngresso.setForeground(new java.awt.Color(255, 255, 255));
        txtCIngresso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtCIngresso.setText("Cancelar Ingresso");
        txtCIngresso.setToolTipText("");
        txtCIngresso.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtCIngresso.setMaximumSize(new java.awt.Dimension(70, 13));
        txtCIngresso.setMinimumSize(new java.awt.Dimension(70, 13));
        txtCIngresso.setPreferredSize(new java.awt.Dimension(70, 13));
        txtCIngresso.setRequestFocusEnabled(false);
        evCIngresso.add(txtCIngresso);
        txtCIngresso.setBounds(15, 12, 70, 13);

        ctMIngressos.add(evCIngresso);
        evCIngresso.setBounds(540, 0, 100, 37);

        cardMIngressos.add(ctMIngressos, new java.awt.GridBagConstraints());

        pnlContent.add(cardMIngressos, "mingressos");

        cartPerfil.setBackground(new java.awt.Color(243, 244, 246));
        cartPerfil.setMaximumSize(new java.awt.Dimension(640, 480));
        cartPerfil.setMinimumSize(new java.awt.Dimension(640, 480));
        cartPerfil.setPreferredSize(new java.awt.Dimension(640, 480));
        cartPerfil.setLayout(new java.awt.GridBagLayout());

        ctPerfil.setBackground(new java.awt.Color(243, 244, 246));
        ctPerfil.setMaximumSize(new java.awt.Dimension(640, 480));
        ctPerfil.setMinimumSize(new java.awt.Dimension(640, 480));
        ctPerfil.setPreferredSize(new java.awt.Dimension(640, 480));
        ctPerfil.setLayout(null);

        titlePerfil.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        titlePerfil.setForeground(new java.awt.Color(31, 41, 55));
        titlePerfil.setText("Perfil");
        ctPerfil.add(titlePerfil);
        titlePerfil.setBounds(0, 0, 60, 32);

        pfContent.setBackground(new java.awt.Color(255, 255, 255));
        pfContent.setMinimumSize(new java.awt.Dimension(640, 418));
        pfContent.setLayout(null);

        txtPNSenha.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtPNSenha.setForeground(new java.awt.Color(79, 70, 229));
        txtPNSenha.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPNSenha.setText("Senha atual ou nova:");
        txtPNSenha.setMaximumSize(new java.awt.Dimension(350, 32));
        txtPNSenha.setMinimumSize(new java.awt.Dimension(350, 32));
        txtPNSenha.setPreferredSize(new java.awt.Dimension(350, 32));
        pfContent.add(txtPNSenha);
        txtPNSenha.setBounds(50, 250, 350, 32);

        txtPTitle.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        txtPTitle.setText("Seus dados");
        pfContent.add(txtPTitle);
        txtPTitle.setBounds(170, 30, 111, 26);

        txtPUsuario.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtPUsuario.setForeground(new java.awt.Color(79, 70, 229));
        txtPUsuario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPUsuario.setText("Usuário: ");
        txtPUsuario.setMaximumSize(new java.awt.Dimension(350, 32));
        txtPUsuario.setMinimumSize(new java.awt.Dimension(350, 32));
        txtPUsuario.setPreferredSize(new java.awt.Dimension(350, 32));
        pfContent.add(txtPUsuario);
        txtPUsuario.setBounds(50, 90, 350, 32);

        txtPEmail.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtPEmail.setForeground(new java.awt.Color(79, 70, 229));
        txtPEmail.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPEmail.setText("Email: ");
        txtPEmail.setMaximumSize(new java.awt.Dimension(350, 32));
        txtPEmail.setMinimumSize(new java.awt.Dimension(350, 32));
        txtPEmail.setPreferredSize(new java.awt.Dimension(350, 32));
        pfContent.add(txtPEmail);
        txtPEmail.setBounds(50, 170, 350, 32);

        fieldPUsuario.setMaximumSize(new java.awt.Dimension(64, 32));
        fieldPUsuario.setMinimumSize(new java.awt.Dimension(64, 32));
        fieldPUsuario.setPreferredSize(new java.awt.Dimension(71, 32));
        pfContent.add(fieldPUsuario);
        fieldPUsuario.setBounds(50, 120, 350, 32);

        fieldPEmail.setMaximumSize(new java.awt.Dimension(64, 32));
        fieldPEmail.setMinimumSize(new java.awt.Dimension(64, 32));
        fieldPEmail.setPreferredSize(new java.awt.Dimension(71, 24));
        pfContent.add(fieldPEmail);
        fieldPEmail.setBounds(50, 200, 350, 32);

        pnlPSConfigs.setBackground(new java.awt.Color(79, 70, 229));
        pnlPSConfigs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlPSConfigs.setMaximumSize(new java.awt.Dimension(220, 44));
        pnlPSConfigs.setLayout(null);

        btnPSConfigs.setBorderPainted(false);
        btnPSConfigs.setContentAreaFilled(false);
        btnPSConfigs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnPSConfigs.setFocusPainted(false);
        btnPSConfigs.setMaximumSize(new java.awt.Dimension(220, 44));
        btnPSConfigs.setMinimumSize(new java.awt.Dimension(220, 44));
        btnPSConfigs.setPreferredSize(new java.awt.Dimension(220, 44));
        btnPSConfigs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPSConfigsActionPerformed(evt);
            }
        });
        pnlPSConfigs.add(btnPSConfigs);
        btnPSConfigs.setBounds(0, 0, 350, 44);

        txtPSConfigs.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtPSConfigs.setForeground(new java.awt.Color(255, 255, 255));
        txtPSConfigs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtPSConfigs.setText("Salvar Configurações");
        txtPSConfigs.setToolTipText("");
        txtPSConfigs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtPSConfigs.setMaximumSize(new java.awt.Dimension(190, 20));
        txtPSConfigs.setMinimumSize(new java.awt.Dimension(190, 20));
        txtPSConfigs.setPreferredSize(new java.awt.Dimension(190, 20));
        txtPSConfigs.setRequestFocusEnabled(false);
        pnlPSConfigs.add(txtPSConfigs);
        txtPSConfigs.setBounds(15, 12, 320, 20);

        pfContent.add(pnlPSConfigs);
        pnlPSConfigs.setBounds(50, 344, 350, 44);

        fieldPNSenha.setMaximumSize(new java.awt.Dimension(350, 32));
        fieldPNSenha.setMinimumSize(new java.awt.Dimension(350, 32));
        fieldPNSenha.setPreferredSize(new java.awt.Dimension(350, 32));
        pfContent.add(fieldPNSenha);
        fieldPNSenha.setBounds(50, 280, 350, 32);

        ctPerfil.add(pfContent);
        pfContent.setBounds(95, 62, 450, 418);

        evEUsuario.setBackground(new java.awt.Color(79, 70, 229));
        evEUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        evEUsuario.setMaximumSize(new java.awt.Dimension(100, 37));
        evEUsuario.setMinimumSize(new java.awt.Dimension(100, 37));
        evEUsuario.setLayout(null);

        btnEUsuario.setBorderPainted(false);
        btnEUsuario.setContentAreaFilled(false);
        btnEUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnEUsuario.setFocusPainted(false);
        btnEUsuario.setMaximumSize(new java.awt.Dimension(100, 37));
        btnEUsuario.setMinimumSize(new java.awt.Dimension(100, 37));
        btnEUsuario.setPreferredSize(new java.awt.Dimension(100, 37));
        btnEUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEUsuarioActionPerformed(evt);
            }
        });
        evEUsuario.add(btnEUsuario);
        btnEUsuario.setBounds(0, 0, 100, 37);

        txtEUsuario.setFont(new java.awt.Font("SansSerif", 1, 8)); // NOI18N
        txtEUsuario.setForeground(new java.awt.Color(255, 255, 255));
        txtEUsuario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtEUsuario.setText("Deletar conta");
        txtEUsuario.setToolTipText("");
        txtEUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtEUsuario.setMaximumSize(new java.awt.Dimension(70, 13));
        txtEUsuario.setMinimumSize(new java.awt.Dimension(70, 13));
        txtEUsuario.setPreferredSize(new java.awt.Dimension(70, 13));
        txtEUsuario.setRequestFocusEnabled(false);
        evEUsuario.add(txtEUsuario);
        txtEUsuario.setBounds(15, 12, 70, 13);

        ctPerfil.add(evEUsuario);
        evEUsuario.setBounds(540, 0, 100, 37);

        cartPerfil.add(ctPerfil, new java.awt.GridBagConstraints());

        pnlContent.add(cartPerfil, "perfil");

        cardDashboard.setBackground(new java.awt.Color(243, 244, 246));
        cardDashboard.setMaximumSize(new java.awt.Dimension(640, 480));
        cardDashboard.setMinimumSize(new java.awt.Dimension(640, 480));
        cardDashboard.setLayout(new java.awt.GridBagLayout());

        ctDashboard.setBackground(new java.awt.Color(243, 244, 246));
        ctDashboard.setMinimumSize(new java.awt.Dimension(640, 480));
        ctDashboard.setPreferredSize(new java.awt.Dimension(640, 480));
        ctDashboard.setLayout(null);

        titleDashboard.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        titleDashboard.setForeground(new java.awt.Color(31, 41, 55));
        titleDashboard.setText("Dashboard");
        ctDashboard.add(titleDashboard);
        titleDashboard.setBounds(0, 0, 125, 32);

        dbVendas.setBackground(new java.awt.Color(255, 255, 255));
        dbVendas.setMinimumSize(new java.awt.Dimension(310, 91));
        dbVendas.setLayout(null);

        dbvVTotais.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        dbvVTotais.setForeground(new java.awt.Color(100, 116, 139));
        dbvVTotais.setText("Vendas Totais");
        dbvVTotais.setMaximumSize(new java.awt.Dimension(270, 19));
        dbvVTotais.setMinimumSize(new java.awt.Dimension(270, 19));
        dbvVTotais.setPreferredSize(new java.awt.Dimension(270, 19));
        dbVendas.add(dbvVTotais);
        dbvVTotais.setBounds(20, 20, 270, 19);

        dbvValor.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        dbvValor.setForeground(new java.awt.Color(31, 41, 55));
        dbvValor.setText("R$ 0,00");
        dbvValor.setToolTipText("");
        dbvValor.setMaximumSize(new java.awt.Dimension(270, 32));
        dbvValor.setMinimumSize(new java.awt.Dimension(270, 32));
        dbvValor.setPreferredSize(new java.awt.Dimension(270, 32));
        dbVendas.add(dbvValor);
        dbvValor.setBounds(20, 44, 270, 32);

        ctDashboard.add(dbVendas);
        dbVendas.setBounds(0, 62, 310, 91);

        dbIngressos.setBackground(new java.awt.Color(255, 255, 255));
        dbIngressos.setMinimumSize(new java.awt.Dimension(310, 91));
        dbIngressos.setLayout(null);

        dviIVendidos.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        dviIVendidos.setForeground(new java.awt.Color(100, 116, 139));
        dviIVendidos.setText("Ingressos Vendidos");
        dviIVendidos.setMaximumSize(new java.awt.Dimension(270, 19));
        dviIVendidos.setMinimumSize(new java.awt.Dimension(270, 19));
        dviIVendidos.setPreferredSize(new java.awt.Dimension(270, 19));
        dbIngressos.add(dviIVendidos);
        dviIVendidos.setBounds(20, 20, 270, 19);

        dviQuant.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        dviQuant.setForeground(new java.awt.Color(31, 41, 55));
        dviQuant.setText("0");
        dviQuant.setMaximumSize(new java.awt.Dimension(270, 32));
        dviQuant.setMinimumSize(new java.awt.Dimension(270, 32));
        dviQuant.setPreferredSize(new java.awt.Dimension(270, 32));
        dbIngressos.add(dviQuant);
        dviQuant.setBounds(20, 44, 270, 32);

        ctDashboard.add(dbIngressos);
        dbIngressos.setBounds(330, 62, 310, 91);

        dbEventos.setBackground(new java.awt.Color(255, 255, 255));
        dbEventos.setMinimumSize(new java.awt.Dimension(310, 91));
        dbEventos.setLayout(null);

        dveEAtivos.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        dveEAtivos.setForeground(new java.awt.Color(100, 116, 139));
        dveEAtivos.setText("Eventos Ativos");
        dveEAtivos.setMaximumSize(new java.awt.Dimension(270, 19));
        dveEAtivos.setMinimumSize(new java.awt.Dimension(270, 19));
        dveEAtivos.setPreferredSize(new java.awt.Dimension(270, 19));
        dbEventos.add(dveEAtivos);
        dveEAtivos.setBounds(20, 20, 270, 19);

        dveQuant.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        dveQuant.setForeground(new java.awt.Color(31, 41, 55));
        dveQuant.setText("0");
        dveQuant.setToolTipText("");
        dveQuant.setMaximumSize(new java.awt.Dimension(270, 32));
        dveQuant.setMinimumSize(new java.awt.Dimension(270, 32));
        dveQuant.setPreferredSize(new java.awt.Dimension(270, 32));
        dbEventos.add(dveQuant);
        dveQuant.setBounds(20, 44, 270, 32);

        ctDashboard.add(dbEventos);
        dbEventos.setBounds(0, 173, 310, 91);

        dbQClientes.setBackground(new java.awt.Color(255, 255, 255));
        dbQClientes.setMinimumSize(new java.awt.Dimension(310, 91));
        dbQClientes.setLayout(null);

        dvqClientes.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        dvqClientes.setForeground(new java.awt.Color(100, 116, 139));
        dvqClientes.setText("Clientes");
        dvqClientes.setMaximumSize(new java.awt.Dimension(270, 19));
        dvqClientes.setMinimumSize(new java.awt.Dimension(270, 19));
        dvqClientes.setPreferredSize(new java.awt.Dimension(270, 19));
        dbQClientes.add(dvqClientes);
        dvqClientes.setBounds(20, 20, 270, 19);

        dvqQClientes.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        dvqQClientes.setForeground(new java.awt.Color(31, 41, 55));
        dvqQClientes.setText("0");
        dvqQClientes.setMaximumSize(new java.awt.Dimension(270, 32));
        dvqQClientes.setMinimumSize(new java.awt.Dimension(270, 32));
        dvqQClientes.setPreferredSize(new java.awt.Dimension(270, 32));
        dbQClientes.add(dvqQClientes);
        dvqQClientes.setBounds(20, 44, 270, 32);

        ctDashboard.add(dbQClientes);
        dbQClientes.setBounds(330, 173, 310, 91);

        dbVRecentes.setBackground(new java.awt.Color(255, 255, 255));
        dbVRecentes.setMinimumSize(new java.awt.Dimension(310, 91));
        dbVRecentes.setLayout(null);

        dvVRecentes.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        dvVRecentes.setForeground(new java.awt.Color(100, 116, 139));
        dvVRecentes.setText("Vendas Recentes");
        dbVRecentes.add(dvVRecentes);
        dvVRecentes.setBounds(20, 20, 123, 19);

        cprVEventoS1.setBackground(new java.awt.Color(243, 244, 246));
        cprVEventoS1.setMaximumSize(new java.awt.Dimension(600, 127));
        cprVEventoS1.setMinimumSize(new java.awt.Dimension(600, 127));
        cprVEventoS1.setPreferredSize(new java.awt.Dimension(600, 127));

        tblEventosDisponiveis1.setBackground(new java.awt.Color(243, 244, 246));
        tblEventosDisponiveis1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Evento", "Data", "Preço"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblEventosDisponiveis1.setMaximumSize(new java.awt.Dimension(600, 127));
        tblEventosDisponiveis1.setMinimumSize(new java.awt.Dimension(600, 127));
        tblEventosDisponiveis1.setPreferredSize(new java.awt.Dimension(600, 127));
        cprVEventoS1.setViewportView(tblEventosDisponiveis1);

        dbVRecentes.add(cprVEventoS1);
        cprVEventoS1.setBounds(20, 49, 600, 127);

        ctDashboard.add(dbVRecentes);
        dbVRecentes.setBounds(0, 284, 640, 196);

        cardDashboard.add(ctDashboard, new java.awt.GridBagConstraints());

        pnlContent.add(cardDashboard, "dashboard");

        cardEventos.setBackground(new java.awt.Color(243, 244, 246));
        cardEventos.setMaximumSize(new java.awt.Dimension(640, 480));
        cardEventos.setMinimumSize(new java.awt.Dimension(640, 480));
        cardEventos.setLayout(new java.awt.GridBagLayout());

        ctEventos.setBackground(new java.awt.Color(243, 244, 246));
        ctEventos.setMinimumSize(new java.awt.Dimension(640, 480));
        ctEventos.setPreferredSize(new java.awt.Dimension(640, 480));
        ctEventos.setLayout(null);

        titleEventos.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        titleEventos.setForeground(new java.awt.Color(31, 41, 55));
        titleEventos.setText("Eventos");
        titleEventos.setMaximumSize(new java.awt.Dimension(93, 37));
        titleEventos.setMinimumSize(new java.awt.Dimension(93, 37));
        titleEventos.setPreferredSize(new java.awt.Dimension(93, 37));
        ctEventos.add(titleEventos);
        titleEventos.setBounds(0, 0, 93, 37);

        evNEvento.setBackground(new java.awt.Color(79, 70, 229));
        evNEvento.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        evNEvento.setMaximumSize(new java.awt.Dimension(100, 37));
        evNEvento.setMinimumSize(new java.awt.Dimension(100, 37));
        evNEvento.setPreferredSize(new java.awt.Dimension(100, 37));
        evNEvento.setLayout(null);

        btnNEvento.setBorderPainted(false);
        btnNEvento.setContentAreaFilled(false);
        btnNEvento.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnNEvento.setFocusPainted(false);
        btnNEvento.setMaximumSize(new java.awt.Dimension(100, 37));
        btnNEvento.setMinimumSize(new java.awt.Dimension(100, 37));
        btnNEvento.setPreferredSize(new java.awt.Dimension(100, 37));
        btnNEvento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNEventoActionPerformed(evt);
            }
        });
        evNEvento.add(btnNEvento);
        btnNEvento.setBounds(0, 0, 100, 37);

        txtNEvento.setFont(new java.awt.Font("SansSerif", 1, 8)); // NOI18N
        txtNEvento.setForeground(new java.awt.Color(255, 255, 255));
        txtNEvento.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNEvento.setText("Novo Evento");
        txtNEvento.setToolTipText("");
        txtNEvento.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtNEvento.setMaximumSize(new java.awt.Dimension(70, 13));
        txtNEvento.setMinimumSize(new java.awt.Dimension(70, 13));
        txtNEvento.setPreferredSize(new java.awt.Dimension(70, 13));
        txtNEvento.setRequestFocusEnabled(false);
        evNEvento.add(txtNEvento);
        txtNEvento.setBounds(15, 12, 70, 13);

        ctEventos.add(evNEvento);
        evNEvento.setBounds(540, 0, 100, 37);

        fieldFEvento.setMaximumSize(new java.awt.Dimension(250, 37));
        fieldFEvento.setMinimumSize(new java.awt.Dimension(250, 37));
        fieldFEvento.setPreferredSize(new java.awt.Dimension(250, 37));
        fieldFEvento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldFEventoActionPerformed(evt);
            }
        });
        ctEventos.add(fieldFEvento);
        fieldFEvento.setBounds(160, 0, 250, 37);

        evLEvento.setBackground(new java.awt.Color(255, 255, 255));
        evLEvento.setMaximumSize(new java.awt.Dimension(640, 418));
        evLEvento.setMinimumSize(new java.awt.Dimension(640, 418));
        evLEvento.setLayout(null);

        scrollLEvento.setBackground(new java.awt.Color(243, 244, 246));
        scrollLEvento.setMaximumSize(new java.awt.Dimension(640, 418));
        scrollLEvento.setMinimumSize(new java.awt.Dimension(640, 418));
        scrollLEvento.setPreferredSize(new java.awt.Dimension(640, 418));

        tableLEvento.setBackground(new java.awt.Color(243, 244, 246));
        tableLEvento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome do Evento", "Data", "Preço", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollLEvento.setViewportView(tableLEvento);

        evLEvento.add(scrollLEvento);
        scrollLEvento.setBounds(0, 0, 640, 418);

        ctEventos.add(evLEvento);
        evLEvento.setBounds(0, 62, 640, 418);

        evREvento.setBackground(new java.awt.Color(79, 70, 229));
        evREvento.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        evREvento.setMaximumSize(new java.awt.Dimension(100, 37));
        evREvento.setMinimumSize(new java.awt.Dimension(100, 37));
        evREvento.setLayout(null);

        btnREvento.setBorderPainted(false);
        btnREvento.setContentAreaFilled(false);
        btnREvento.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnREvento.setFocusPainted(false);
        btnREvento.setMaximumSize(new java.awt.Dimension(100, 37));
        btnREvento.setMinimumSize(new java.awt.Dimension(100, 37));
        btnREvento.setPreferredSize(new java.awt.Dimension(137, 37));
        btnREvento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnREventoActionPerformed(evt);
            }
        });
        evREvento.add(btnREvento);
        btnREvento.setBounds(0, 0, 100, 37);

        txtREvento.setFont(new java.awt.Font("SansSerif", 1, 8)); // NOI18N
        txtREvento.setForeground(new java.awt.Color(255, 255, 255));
        txtREvento.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtREvento.setText("Remover Evento");
        txtREvento.setToolTipText("");
        txtREvento.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtREvento.setMaximumSize(new java.awt.Dimension(70, 13));
        txtREvento.setMinimumSize(new java.awt.Dimension(70, 13));
        txtREvento.setPreferredSize(new java.awt.Dimension(70, 13));
        txtREvento.setRequestFocusEnabled(false);
        evREvento.add(txtREvento);
        txtREvento.setBounds(15, 12, 70, 13);

        ctEventos.add(evREvento);
        evREvento.setBounds(425, 0, 100, 37);

        cardEventos.add(ctEventos, new java.awt.GridBagConstraints());

        pnlContent.add(cardEventos, "eventos");

        cardUsuarios.setBackground(new java.awt.Color(243, 244, 246));
        cardUsuarios.setMaximumSize(new java.awt.Dimension(640, 480));
        cardUsuarios.setMinimumSize(new java.awt.Dimension(640, 480));
        cardUsuarios.setLayout(new java.awt.GridBagLayout());

        ctUsuarios.setBackground(new java.awt.Color(243, 244, 246));
        ctUsuarios.setMinimumSize(new java.awt.Dimension(640, 480));
        ctUsuarios.setPreferredSize(new java.awt.Dimension(640, 480));
        ctUsuarios.setLayout(null);

        titleUsuarios.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        titleUsuarios.setForeground(new java.awt.Color(31, 41, 55));
        titleUsuarios.setText("Usuários");
        ctUsuarios.add(titleUsuarios);
        titleUsuarios.setBounds(0, 0, 102, 32);

        usNUsuario.setBackground(new java.awt.Color(79, 70, 229));
        usNUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        usNUsuario.setMaximumSize(new java.awt.Dimension(100, 37));
        usNUsuario.setMinimumSize(new java.awt.Dimension(100, 37));
        usNUsuario.setPreferredSize(new java.awt.Dimension(100, 37));
        usNUsuario.setLayout(null);

        btnNUsuario.setBorderPainted(false);
        btnNUsuario.setContentAreaFilled(false);
        btnNUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnNUsuario.setFocusPainted(false);
        btnNUsuario.setMaximumSize(new java.awt.Dimension(100, 37));
        btnNUsuario.setMinimumSize(new java.awt.Dimension(100, 37));
        btnNUsuario.setPreferredSize(new java.awt.Dimension(100, 37));
        btnNUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNUsuarioActionPerformed(evt);
            }
        });
        usNUsuario.add(btnNUsuario);
        btnNUsuario.setBounds(0, 0, 100, 37);

        txtNUsuario.setFont(new java.awt.Font("SansSerif", 1, 8)); // NOI18N
        txtNUsuario.setForeground(new java.awt.Color(255, 255, 255));
        txtNUsuario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNUsuario.setText("Novo Usuário");
        txtNUsuario.setToolTipText("");
        txtNUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtNUsuario.setMaximumSize(new java.awt.Dimension(70, 13));
        txtNUsuario.setMinimumSize(new java.awt.Dimension(70, 13));
        txtNUsuario.setPreferredSize(new java.awt.Dimension(70, 13));
        txtNUsuario.setRequestFocusEnabled(false);
        usNUsuario.add(txtNUsuario);
        txtNUsuario.setBounds(15, 12, 70, 13);

        ctUsuarios.add(usNUsuario);
        usNUsuario.setBounds(540, 0, 100, 37);

        fieldFUsuario.setMaximumSize(new java.awt.Dimension(250, 37));
        fieldFUsuario.setMinimumSize(new java.awt.Dimension(250, 37));
        fieldFUsuario.setPreferredSize(new java.awt.Dimension(250, 37));
        fieldFUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldFUsuarioActionPerformed(evt);
            }
        });
        ctUsuarios.add(fieldFUsuario);
        fieldFUsuario.setBounds(160, 0, 250, 37);

        usLUsuario.setBackground(new java.awt.Color(255, 255, 255));
        usLUsuario.setMaximumSize(new java.awt.Dimension(640, 418));
        usLUsuario.setMinimumSize(new java.awt.Dimension(640, 418));
        usLUsuario.setLayout(null);

        scrollLUsuario.setBackground(new java.awt.Color(243, 244, 246));
        scrollLUsuario.setMaximumSize(new java.awt.Dimension(640, 418));
        scrollLUsuario.setMinimumSize(new java.awt.Dimension(640, 418));
        scrollLUsuario.setPreferredSize(new java.awt.Dimension(640, 418));

        tableLUsuario.setBackground(new java.awt.Color(243, 244, 246));
        tableLUsuario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "Usuário", "Email", "Cargo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollLUsuario.setViewportView(tableLUsuario);

        usLUsuario.add(scrollLUsuario);
        scrollLUsuario.setBounds(0, 0, 640, 418);

        ctUsuarios.add(usLUsuario);
        usLUsuario.setBounds(0, 62, 640, 418);

        usRUsuario.setBackground(new java.awt.Color(79, 70, 229));
        usRUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        usRUsuario.setMaximumSize(new java.awt.Dimension(100, 37));
        usRUsuario.setMinimumSize(new java.awt.Dimension(100, 37));
        usRUsuario.setLayout(null);

        btnRUsuario.setBorderPainted(false);
        btnRUsuario.setContentAreaFilled(false);
        btnRUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnRUsuario.setFocusPainted(false);
        btnRUsuario.setMaximumSize(new java.awt.Dimension(100, 37));
        btnRUsuario.setMinimumSize(new java.awt.Dimension(100, 37));
        btnRUsuario.setPreferredSize(new java.awt.Dimension(100, 37));
        btnRUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRUsuarioActionPerformed(evt);
            }
        });
        usRUsuario.add(btnRUsuario);
        btnRUsuario.setBounds(0, 0, 100, 37);

        txtRUsuario.setFont(new java.awt.Font("SansSerif", 1, 8)); // NOI18N
        txtRUsuario.setForeground(new java.awt.Color(255, 255, 255));
        txtRUsuario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtRUsuario.setText("Remover Usuário");
        txtRUsuario.setToolTipText("");
        txtRUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtRUsuario.setMaximumSize(new java.awt.Dimension(70, 13));
        txtRUsuario.setMinimumSize(new java.awt.Dimension(70, 13));
        txtRUsuario.setPreferredSize(new java.awt.Dimension(70, 13));
        txtRUsuario.setRequestFocusEnabled(false);
        usRUsuario.add(txtRUsuario);
        txtRUsuario.setBounds(15, 12, 70, 13);

        ctUsuarios.add(usRUsuario);
        usRUsuario.setBounds(425, 0, 100, 37);

        cardUsuarios.add(ctUsuarios, new java.awt.GridBagConstraints());

        pnlContent.add(cardUsuarios, "usuarios");

        pnlMain.add(pnlContent, java.awt.BorderLayout.CENTER);

        pnlBackground.add(pnlMain, "main");

        pnlSSenha.setBackground(new java.awt.Color(243, 244, 246));
        pnlSSenha.setLayout(new java.awt.GridBagLayout());

        bgSSenha.setBackground(new java.awt.Color(255, 255, 255));
        bgSSenha.setMaximumSize(new java.awt.Dimension(400, 473));
        bgSSenha.setMinimumSize(new java.awt.Dimension(400, 473));
        bgSSenha.setPreferredSize(new java.awt.Dimension(400, 473));
        bgSSenha.setLayout(null);

        bgItensSS.setBackground(new java.awt.Color(255, 255, 255));
        bgItensSS.setLayout(null);

        fEmailSS.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        fEmailSS.setToolTipText("");
        bgItensSS.add(fEmailSS);
        fEmailSS.setBounds(0, 116, 320, 46);

        txtNSenhaSS.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtNSenhaSS.setForeground(new java.awt.Color(79, 70, 229));
        txtNSenhaSS.setText("Nova senha:");
        bgItensSS.add(txtNSenhaSS);
        txtNSenhaSS.setBounds(0, 182, 320, 17);

        txtEmailSS.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtEmailSS.setForeground(new java.awt.Color(31, 41, 55));
        txtEmailSS.setText("E-mail:");
        bgItensSS.add(txtEmailSS);
        txtEmailSS.setBounds(0, 91, 320, 17);

        fUserSS.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        fUserSS.setToolTipText("");
        bgItensSS.add(fUserSS);
        fUserSS.setBounds(0, 25, 320, 46);

        txtUserSS.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtUserSS.setText("Usuário:");
        bgItensSS.add(txtUserSS);
        txtUserSS.setBounds(0, 0, 320, 17);

        fNSenhaSS.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        fNSenhaSS.setMaximumSize(new java.awt.Dimension(320, 46));
        fNSenhaSS.setMinimumSize(new java.awt.Dimension(320, 46));
        fNSenhaSS.setPreferredSize(new java.awt.Dimension(320, 46));
        bgItensSS.add(fNSenhaSS);
        fNSenhaSS.setBounds(0, 207, 320, 46);

        pnlBRSenha.setBackground(new java.awt.Color(79, 70, 229));
        pnlBRSenha.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlBRSenha.setMaximumSize(new java.awt.Dimension(220, 44));
        pnlBRSenha.setLayout(null);

        btnRSEnha.setBorderPainted(false);
        btnRSEnha.setContentAreaFilled(false);
        btnRSEnha.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnRSEnha.setFocusPainted(false);
        btnRSEnha.setMaximumSize(new java.awt.Dimension(220, 44));
        btnRSEnha.setMinimumSize(new java.awt.Dimension(220, 44));
        btnRSEnha.setPreferredSize(new java.awt.Dimension(220, 44));
        btnRSEnha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRSEnhaActionPerformed(evt);
            }
        });
        pnlBRSenha.add(btnRSEnha);
        btnRSEnha.setBounds(0, 0, 320, 44);

        txtRSenha.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        txtRSenha.setForeground(new java.awt.Color(255, 255, 255));
        txtRSenha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtRSenha.setText("Redefinir Senha");
        txtRSenha.setToolTipText("");
        txtRSenha.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtRSenha.setMaximumSize(new java.awt.Dimension(190, 20));
        txtRSenha.setMinimumSize(new java.awt.Dimension(190, 20));
        txtRSenha.setPreferredSize(new java.awt.Dimension(190, 20));
        txtRSenha.setRequestFocusEnabled(false);
        pnlBRSenha.add(txtRSenha);
        txtRSenha.setBounds(15, 12, 290, 20);

        bgItensSS.add(pnlBRSenha);
        pnlBRSenha.setBounds(0, 277, 320, 44);

        bgSSenha.add(bgItensSS);
        bgItensSS.setBounds(40, 66, 320, 321);

        txtTitleSS.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        txtTitleSS.setForeground(new java.awt.Color(31, 41, 55));
        txtTitleSS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconTitle.png"))); // NOI18N
        txtTitleSS.setText("Ingressos");
        txtTitleSS.setIconTextGap(10);
        bgSSenha.add(txtTitleSS);
        txtTitleSS.setBounds(121, 20, 159, 32);

        msgSSCL.setBackground(new java.awt.Color(255, 255, 255));
        msgSSCL.setLayout(null);

        btnSSR.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnSSR.setForeground(new java.awt.Color(79, 70, 229));
        btnSSR.setText("Cadastre-se");
        btnSSR.setBorderPainted(false);
        btnSSR.setContentAreaFilled(false);
        btnSSR.setFocusPainted(false);
        btnSSR.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnSSR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSSRActionPerformed(evt);
            }
        });
        msgSSCL.add(btnSSR);
        btnSSR.setBounds(158, 25, 98, 18);

        msgSSR.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        msgSSR.setForeground(new java.awt.Color(107, 114, 128));
        msgSSR.setText("Não tem conta?");
        msgSSCL.add(msgSSR);
        msgSSR.setBounds(65, 23, 89, 23);

        msgSSL.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        msgSSL.setForeground(new java.awt.Color(107, 114, 128));
        msgSSL.setText("Já tem uma conta?");
        msgSSL.setMaximumSize(new java.awt.Dimension(109, 23));
        msgSSL.setMinimumSize(new java.awt.Dimension(109, 23));
        msgSSL.setPreferredSize(new java.awt.Dimension(109, 23));
        msgSSCL.add(msgSSL);
        msgSSL.setBounds(61, 0, 109, 23);

        btnSSL.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnSSL.setForeground(new java.awt.Color(79, 70, 229));
        btnSSL.setText("Faça login");
        btnSSL.setBorderPainted(false);
        btnSSL.setContentAreaFilled(false);
        btnSSL.setFocusPainted(false);
        btnSSL.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnSSL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSSLActionPerformed(evt);
            }
        });
        msgSSCL.add(btnSSL);
        btnSSL.setBounds(174, 2, 85, 18);

        bgSSenha.add(msgSSCL);
        msgSSCL.setBounds(40, 400, 320, 46);

        pnlSSenha.add(bgSSenha, new java.awt.GridBagConstraints());

        pnlBackground.add(pnlSSenha, "ssenha");

        getContentPane().add(pnlBackground, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        alterarSelecao(pnlDashboard, txtDashboard, "iconADashboard.png");
        
        atualizarDashboard();
        
        CardLayout cl = (CardLayout) pnlContent.getLayout();
        cl.show(pnlContent, "dashboard");
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuariosActionPerformed
        alterarSelecao(pnlUsuarios, txtUsuarios, "iconAUsuarios.png");
        
        carregarDadosUsuarios();
        
        CardLayout cl = (CardLayout) pnlContent.getLayout();
        cl.show(pnlContent, "usuarios");
    }//GEN-LAST:event_btnUsuariosActionPerformed

    private void btnMIngressosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMIngressosActionPerformed
        alterarSelecao(pnlMIngressos, txtMIngressos, "iconAMIngressos.png");
        
        carregarMeusIngressos();
        
        CardLayout cl = (CardLayout) pnlContent.getLayout();
        cl.show(pnlContent, "mingressos");
    }//GEN-LAST:event_btnMIngressosActionPerformed

    private void btnEventosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEventosActionPerformed
        alterarSelecao(pnlEventos, txtEventos, "iconAEventos.png");
        
        carregarTabelaEventos();
        
        CardLayout cl = (CardLayout) pnlContent.getLayout();
        cl.show(pnlContent, "eventos");
    }//GEN-LAST:event_btnEventosActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        int resposta = JOptionPane.showConfirmDialog(this,  "Deseja fazer logout e voltar para a tela inicial?",  "Sair da Conta",  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resposta == JOptionPane.YES_OPTION) {
            this.usuarioLogado = null;

            fLoginL.setText("");
            fSenhaL.setText("");

            CardLayout cl = (CardLayout) pnlBackground.getLayout();
            cl.show(pnlBackground, "login");

            if (!sidebarAberta) {
                btnToggleMenu.doClick();
            }
        }
    }//GEN-LAST:event_btnSairActionPerformed

    private void btnToggleMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleMenuActionPerformed
        if (sidebarAberta) {
            pnlSidebar.setPreferredSize(new java.awt.Dimension(larguraFechada, pnlSidebar.getHeight()));

            txtToggleMenu.setText(">");
            pnlToggleMenu.setLocation(5, 14);

            txtTitle.setVisible(false);

            pnlComprar.setSize((larguraFechada-10), 44);
            pnlMIngressos.setSize((larguraFechada-10), 44);
            pnlPerfil.setSize((larguraFechada-10), 44);
            pnlDashboard.setSize((larguraFechada-10), 44);
            pnlEventos.setSize((larguraFechada-10), 44);
            pnlUsuarios.setSize((larguraFechada-10), 44);
            pnlSair.setSize((larguraFechada-10), 44);
            pnlComprar.setLocation(5, 92);
            pnlMIngressos.setLocation(5, 141);
            pnlPerfil.setLocation(5, 190);
            pnlDashboard.setLocation(5, 92);
            pnlEventos.setLocation(5, 141);
            pnlUsuarios.setLocation(5, 190);
            pnlSair.setLocation(5, 480);

            alterarTextoBotoes(false);
        } else {
            pnlSidebar.setPreferredSize(new java.awt.Dimension(larguraAberta, pnlSidebar.getHeight()));

            txtToggleMenu.setText("<");
            pnlToggleMenu.setLocation(196, 14);

            txtTitle.setVisible(true);

            pnlComprar.setSize(220, 44);
            pnlMIngressos.setSize(220, 44);
            pnlPerfil.setSize(220, 44);
            pnlDashboard.setSize(220, 44);
            pnlEventos.setSize(220, 44);
            pnlUsuarios.setSize(220, 44);
            pnlSair.setSize(220, 44);
            pnlComprar.setLocation(20, 92);
            pnlMIngressos.setLocation(20, 141);
            pnlPerfil.setLocation(20, 190);
            pnlDashboard.setLocation(20, 92);
            pnlEventos.setLocation(20, 141);
            pnlUsuarios.setLocation(20, 190);
            pnlSair.setLocation(20, 480);

            alterarTextoBotoes(true);
        }

        sidebarAberta = !sidebarAberta;

        pnlSidebar.revalidate();
        pnlSidebar.repaint();

        this.getContentPane().revalidate();
        this.getContentPane().repaint();
    }//GEN-LAST:event_btnToggleMenuActionPerformed

    private void btnLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLActionPerformed
        CardLayout cl = (CardLayout) pnlBackground.getLayout();
        cl.show(pnlBackground, "login");
    }//GEN-LAST:event_btnLActionPerformed

    private void btnPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerfilActionPerformed
        alterarSelecao(pnlPerfil, txtPerfil, "iconAPerfil.png");
        
        carregarDadosPerfil();
        
        CardLayout cl = (CardLayout) pnlContent.getLayout();
        cl.show(pnlContent, "perfil");
    }//GEN-LAST:event_btnPerfilActionPerformed

    private void btnComprarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComprarActionPerformed
        alterarSelecao(pnlComprar, txtComprar, "iconAComprar.png");
        
        carregarDadosVenda();
        
        CardLayout cl = (CardLayout) pnlContent.getLayout();
        cl.show(pnlContent, "comprar");
    }//GEN-LAST:event_btnComprarActionPerformed

    private void btnPSConfigsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPSConfigsActionPerformed
        String novoUsuario = fieldPUsuario.getText();
        String novoEmail = fieldPEmail.getText();
        String novaSenha = fieldPNSenha.getText();

        if(!novoUsuario.isEmpty() && !novoEmail.isEmpty() && !novaSenha.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja atualizar seu perfil?", "Confirmar", JOptionPane.YES_NO_OPTION);

            if(confirm == JOptionPane.YES_OPTION) {
                UsuarioDAO dao = new UsuarioDAO();

                dao.atualizarPerfil(this.usuarioLogado, novoUsuario, novoEmail, novaSenha);
                
                this.usuarioLogado.setUsuario(novoUsuario);
                this.usuarioLogado.setEmail(novoEmail);
                
                carregarDadosPerfil(); 
            }
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar perfil. (Algum campo está vazio)", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnPSConfigsActionPerformed

    private void fieldBuscaEventoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldBuscaEventoActionPerformed
        String texto = fieldBuscaEvento.getText();

        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) tblEventosDisponiveis.getRowSorter();

        if (texto.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 1));
        }
    }//GEN-LAST:event_fieldBuscaEventoActionPerformed

    private void btnConfirmarVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarVendaActionPerformed
        if (this.usuarioLogado == null) {
            JOptionPane.showMessageDialog(this, "Erro: Você precisa estar logado para comprar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int linhaSelecionada = tblEventosDisponiveis.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um evento na tabela para continuar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int linhaModelo = tblEventosDisponiveis.convertRowIndexToModel(linhaSelecionada);
        Evento eventoSelecionado = eventosNaTabela.get(linhaModelo);

        int quantidade = (int) cprVQuantS.getValue();

        if (quantidade <= 0) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (quantidade > eventoSelecionado.getCapacidade()) {
            JOptionPane.showMessageDialog(this, "Desculpe, só restam " + eventoSelecionado.getCapacidade() + " ingressos.", "Estoque Insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double valorTotal = eventoSelecionado.getPreco() * quantidade;

        int confirmacao = JOptionPane.showConfirmDialog(this, "Confirmar compra?\n\nEvento: " + eventoSelecionado.getNome() + "\nQuantidade: " + quantidade + "\nTotal: R$ " + String.format("%.2f", valorTotal), "Finalizar Pedido", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            IngressoDAO dao = new IngressoDAO();

            boolean sucesso = dao.confirmarVenda(this.usuarioLogado.getId(), eventoSelecionado.getId(), quantidade, valorTotal);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Compra realizada com sucesso!", "Parabéns", JOptionPane.INFORMATION_MESSAGE);

                atualizarDashboard();
                carregarDadosVenda();
                limparTelaCompra();
            }
        }
    }//GEN-LAST:event_btnConfirmarVendaActionPerformed

    private void btnCancelarVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarVendaActionPerformed
        limparTelaCompra();
    }//GEN-LAST:event_btnCancelarVendaActionPerformed

    private void btnNEventoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNEventoActionPerformed
        JTextField txtNome = new JTextField();
        JTextField txtData = new JTextField();
        JTextField txtPreco = new JTextField();
        JTextField txtCapacidade = new JTextField();

        Object[] message = { "Nome do Evento:", txtNome, "Data (ex: 10/12/2024):", txtData, "Preço (ex: 50.00):", txtPreco, "Capacidade Total:", txtCapacidade };

        int option = JOptionPane.showConfirmDialog(this, message, "Novo Evento", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                if(txtNome.getText().isEmpty() || txtPreco.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                    return;
                }

                Evento e = new Evento();
                e.setNome(txtNome.getText());
                e.setData(txtData.getText());

                String precoStr = txtPreco.getText().replace(",", ".");
                e.setPreco(Double.parseDouble(precoStr));

                e.setCapacidade(Integer.parseInt(txtCapacidade.getText()));

                EventoDAO dao = new EventoDAO();
                dao.cadastrarEvento(e);

                carregarTabelaEventos();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erro: Preço ou Capacidade devem ser números válidos.");
            }
        }
    }//GEN-LAST:event_btnNEventoActionPerformed

    private void fieldFEventoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldFEventoActionPerformed
        String texto = fieldFEvento.getText();
    
        DefaultTableModel model = (DefaultTableModel) tableLEvento.getModel();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) tableLEvento.getRowSorter();

        if (sorter == null) {
            sorter = new TableRowSorter<>(model);
            tableLEvento.setRowSorter(sorter);
        }

        if (texto.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 1));
        }
    }//GEN-LAST:event_fieldFEventoActionPerformed

    private void btnNUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNUsuarioActionPerformed
        javax.swing.JTextField txtUser = new javax.swing.JTextField();
        javax.swing.JTextField txtEmail = new javax.swing.JTextField();
        javax.swing.JPasswordField txtPass = new javax.swing.JPasswordField();
        
        Object[] message = { "Usuário:", txtUser, "Email:", txtEmail, "Senha:", txtPass };

        int option = JOptionPane.showConfirmDialog(this, message, "Novo Usuário", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String u = txtUser.getText();
            String e = txtEmail.getText();
            String s = new String(txtPass.getPassword());
            
            if(!u.isEmpty() && !s.isEmpty() && !e.isEmpty()) {
                UsuarioDAO dao = new UsuarioDAO();
                dao.registrar(u, e, s);
                
                carregarDadosUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            }
        }
    }//GEN-LAST:event_btnNUsuarioActionPerformed

    private void fieldFUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldFUsuarioActionPerformed
        String texto = fieldFUsuario.getText();
        
        DefaultTableModel model = (DefaultTableModel) tableLUsuario.getModel();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) tableLUsuario.getRowSorter();
        
        if (sorter == null) {
            sorter = new TableRowSorter<>(model);
            tableLUsuario.setRowSorter(sorter);
        }

        if (texto.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }//GEN-LAST:event_fieldFUsuarioActionPerformed

    private void btnSSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSSenhaActionPerformed
        CardLayout cl = (CardLayout) pnlBackground.getLayout();
        cl.show(pnlBackground, "ssenha");
    }//GEN-LAST:event_btnSSenhaActionPerformed

    private void btnRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRActionPerformed
        CardLayout cl = (CardLayout) pnlBackground.getLayout();
        cl.show(pnlBackground, "registrar");
    }//GEN-LAST:event_btnRActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        String login = fLoginL.getText();
        String senha = fSenhaL.getText();
        
        if (!login.isEmpty() && !senha.isEmpty()) {
            UsuarioDAO dao = new UsuarioDAO();

            Cliente clienteLogado = dao.checkLogin(login, senha);

            if(clienteLogado != null) { 
                this.usuarioLogado = clienteLogado;

                JOptionPane.showMessageDialog(null, "Login efetuado com sucesso!");

                configurarPermissoes(usuarioLogado.getCargo());

                atualizarDashboard();

                CardLayout cl = (CardLayout) pnlBackground.getLayout();
                cl.show(pnlBackground, "main");
            } else {
                JOptionPane.showMessageDialog(null, "Usuário, email ou senha incorretos!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        String user = fUserR.getText();
        String email = fEmailR.getText();
        String senha = fSenhaR.getText();

        if (!user.isEmpty() && !email.isEmpty() && !senha.isEmpty()) {
            if(email.contains("@")) {
                UsuarioDAO dao = new UsuarioDAO();
                dao.registrar(user, email, senha);

                fUserR.setText("");
                fEmailR.setText("");
                fSenhaR.setText("");

                CardLayout cl = (CardLayout) pnlBackground.getLayout();
                cl.show(pnlBackground, "login");
            } else {
                JOptionPane.showMessageDialog(null, "O email precisa ter '@'!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
        }
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnRSEnhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRSEnhaActionPerformed
        String usuarioDigitado = fUserSS.getText();
        String emailDigitado = fEmailSS.getText();
        String novaSenha = fNSenhaSS.getText();

        if(usuarioDigitado.isEmpty() || emailDigitado.isEmpty() || novaSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos para redefinir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        boolean sucesso = dao.recuperarSenha(usuarioDigitado, emailDigitado, novaSenha);

        if(sucesso) {
            JOptionPane.showMessageDialog(this, "Senha redefinida com sucesso! Faça login agora.");

            fUserSS.setText("");
            fEmailSS.setText("");
            fNSenhaSS.setText("");

            CardLayout cl = (CardLayout) pnlBackground.getLayout();
            cl.show(pnlBackground, "login");
        } else {
            JOptionPane.showMessageDialog(this, "Dados incorretos. Usuário e E-mail não conferem.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnRSEnhaActionPerformed

    private void btnSSRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSSRActionPerformed
        CardLayout cl = (CardLayout) pnlBackground.getLayout();
        cl.show(pnlBackground, "registrar");
    }//GEN-LAST:event_btnSSRActionPerformed

    private void btnSSLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSSLActionPerformed
        CardLayout cl = (CardLayout) pnlBackground.getLayout();
        cl.show(pnlBackground, "login");
    }//GEN-LAST:event_btnSSLActionPerformed

    private void btnREventoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnREventoActionPerformed
        int linhaSelecionada = tableLEvento.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um evento na tabela para remover.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int linhaModelo = tableLEvento.convertRowIndexToModel(linhaSelecionada);
        
        int idEvento = (int) tableLEvento.getModel().getValueAt(linhaModelo, 0);
        String nomeEvento = (String) tableLEvento.getModel().getValueAt(linhaModelo, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja remover o evento: " + nomeEvento + "?", 
                "Confirmar Remoção", 
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            EventoDAO dao = new EventoDAO();
            dao.removerEvento(idEvento);
            
            carregarTabelaEventos();
        }
    }//GEN-LAST:event_btnREventoActionPerformed

    private void btnRUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRUsuarioActionPerformed
        int linhaSelecionada = tableLUsuario.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela para remover.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int linhaModelo = tableLUsuario.convertRowIndexToModel(linhaSelecionada);
        
        int idUsuarioAlvo = (int) tableLUsuario.getModel().getValueAt(linhaModelo, 0);
        String nomeUsuarioAlvo = (String) tableLUsuario.getModel().getValueAt(linhaModelo, 1);

        if (this.usuarioLogado != null && idUsuarioAlvo == this.usuarioLogado.getId()) {
            JOptionPane.showMessageDialog(this, "Você não pode excluir seu próprio usuário enquanto está logado.", "Ação Bloqueada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja remover o usuário: " + nomeUsuarioAlvo + "?", 
                "Confirmar Remoção", 
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UsuarioDAO dao = new UsuarioDAO();
            dao.removerCliente(idUsuarioAlvo);
            
            carregarDadosUsuarios();
        }
    }//GEN-LAST:event_btnRUsuarioActionPerformed

    private void btnCIngressoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCIngressoActionPerformed
        int linhaSelecionada = tblMeusIngressos.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um ingresso na tabela para cancelar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int linhaModelo = tblMeusIngressos.convertRowIndexToModel(linhaSelecionada);

        int idIngresso = (int) tblMeusIngressos.getModel().getValueAt(linhaModelo, 0);
        String nomeEvento = (String) tblMeusIngressos.getModel().getValueAt(linhaModelo, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente cancelar o ingresso para: " + nomeEvento + "?\nO valor será estornado e a capacidade do evento restaurada.",
                "Cancelar Ingresso",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            IngressoDAO dao = new IngressoDAO();

            boolean sucesso = dao.cancelarIngresso(idIngresso);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Ingresso cancelado com sucesso!");
                carregarMeusIngressos();
                atualizarDashboard();
            }
        }
    }//GEN-LAST:event_btnCIngressoActionPerformed

    private void btnEUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEUsuarioActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir sua conta permanentemente?\nEssa ação não pode ser desfeita.", "Excluir Conta", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                IngressoDAO daoIngresso = new IngressoDAO();
                daoIngresso.apagarTodosIngressosDoCliente(this.usuarioLogado.getId());

                UsuarioDAO daoUsuario = new UsuarioDAO();
                daoUsuario.removerCliente(this.usuarioLogado.getId());

                this.usuarioLogado = null;
                fLoginL.setText("");
                fSenhaL.setText("");
                CardLayout cl = (CardLayout) pnlBackground.getLayout();
                cl.show(pnlBackground, "login");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro crítico ao excluir conta: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnEUsuarioActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bgItensL;
    private javax.swing.JPanel bgItensR;
    private javax.swing.JPanel bgItensSS;
    private javax.swing.JPanel bgLogin;
    private javax.swing.JPanel bgRegistrar;
    private javax.swing.JPanel bgSSenha;
    private javax.swing.JButton btnCIngresso;
    private javax.swing.JButton btnCancelarVenda;
    private javax.swing.JButton btnComprar;
    private javax.swing.JButton btnConfirmarVenda;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnEUsuario;
    private javax.swing.JButton btnEventos;
    private javax.swing.JToggleButton btnL;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnMIngressos;
    private javax.swing.JButton btnNEvento;
    private javax.swing.JButton btnNUsuario;
    private javax.swing.JButton btnPSConfigs;
    private javax.swing.JButton btnPerfil;
    private javax.swing.JToggleButton btnR;
    private javax.swing.JButton btnREvento;
    private javax.swing.JButton btnRSEnha;
    private javax.swing.JButton btnRUsuario;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JToggleButton btnSSL;
    private javax.swing.JToggleButton btnSSR;
    private javax.swing.JToggleButton btnSSenha;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnToggleMenu;
    private javax.swing.JButton btnUsuarios;
    private javax.swing.JPanel cardComprar;
    private javax.swing.JPanel cardDashboard;
    private javax.swing.JPanel cardEventos;
    private javax.swing.JPanel cardMIngressos;
    private javax.swing.JPanel cardUsuarios;
    private javax.swing.JPanel cartPerfil;
    private javax.swing.JPanel cprRCancelar;
    private javax.swing.JPanel cprRConfirmar;
    private javax.swing.JLabel cprRDText;
    private javax.swing.JLabel cprRData;
    private javax.swing.JLabel cprREText;
    private javax.swing.JLabel cprREvento;
    private javax.swing.JLabel cprRPUText;
    private javax.swing.JLabel cprRPUnit;
    private javax.swing.JSeparator cprRSeparator;
    private javax.swing.JLabel cprRTitle;
    private javax.swing.JLabel cprRVTText;
    private javax.swing.JLabel cprRVTotal;
    private javax.swing.JPanel cprResumo;
    private javax.swing.JScrollPane cprVEventoS;
    private javax.swing.JScrollPane cprVEventoS1;
    private javax.swing.JLabel cprVQuant;
    private javax.swing.JSpinner cprVQuantS;
    private javax.swing.JPanel cprVenda;
    private javax.swing.JPanel ctComprar;
    private javax.swing.JPanel ctDashboard;
    private javax.swing.JPanel ctEventos;
    private javax.swing.JPanel ctMIngressos;
    private javax.swing.JPanel ctPerfil;
    private javax.swing.JPanel ctUsuarios;
    private javax.swing.JPanel dbEventos;
    private javax.swing.JPanel dbIngressos;
    private javax.swing.JPanel dbQClientes;
    private javax.swing.JPanel dbVRecentes;
    private javax.swing.JPanel dbVendas;
    private javax.swing.JLabel dbvVTotais;
    private javax.swing.JLabel dbvValor;
    private javax.swing.JLabel dvVRecentes;
    private javax.swing.JLabel dveEAtivos;
    private javax.swing.JLabel dveQuant;
    private javax.swing.JLabel dviIVendidos;
    private javax.swing.JLabel dviQuant;
    private javax.swing.JLabel dvqClientes;
    private javax.swing.JLabel dvqQClientes;
    private javax.swing.JPanel evCIngresso;
    private javax.swing.JPanel evEUsuario;
    private javax.swing.JPanel evLEvento;
    private javax.swing.JPanel evNEvento;
    private javax.swing.JPanel evREvento;
    private javax.swing.JTextField fEmailR;
    private javax.swing.JTextField fEmailSS;
    private javax.swing.JTextField fLoginL;
    private javax.swing.JPasswordField fNSenhaSS;
    private javax.swing.JPasswordField fSenhaL;
    private javax.swing.JPasswordField fSenhaR;
    private javax.swing.JTextField fUserR;
    private javax.swing.JTextField fUserSS;
    private javax.swing.JTextField fieldBuscaEvento;
    private javax.swing.JTextField fieldFEvento;
    private javax.swing.JTextField fieldFUsuario;
    private javax.swing.JTextField fieldPEmail;
    private javax.swing.JPasswordField fieldPNSenha;
    private javax.swing.JTextField fieldPUsuario;
    private javax.swing.JPanel igVenda;
    private javax.swing.JLabel msgL;
    private javax.swing.JPanel msgLogin;
    private javax.swing.JLabel msgR;
    private javax.swing.JPanel msgRegistrar;
    private javax.swing.JPanel msgSSCL;
    private javax.swing.JLabel msgSSL;
    private javax.swing.JLabel msgSSR;
    private javax.swing.JPanel pfContent;
    private javax.swing.JPanel pnlBLogin;
    private javax.swing.JPanel pnlBRSenha;
    private javax.swing.JPanel pnlBRegistrar;
    private javax.swing.JPanel pnlBackground;
    private javax.swing.JPanel pnlComprar;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlDashboard;
    private javax.swing.JPanel pnlEventos;
    private javax.swing.JPanel pnlLogin;
    private javax.swing.JPanel pnlMIngressos;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlPSConfigs;
    private javax.swing.JPanel pnlPerfil;
    private javax.swing.JPanel pnlRegistrar;
    private javax.swing.JPanel pnlSSenha;
    private javax.swing.JPanel pnlSair;
    private javax.swing.JPanel pnlSidebar;
    private javax.swing.JPanel pnlToggleMenu;
    private javax.swing.JPanel pnlUsuarios;
    private javax.swing.JScrollPane scrollLEvento;
    private javax.swing.JScrollPane scrollLUsuario;
    private javax.swing.JScrollPane scrollMeusIngressos;
    private javax.swing.JTable tableLEvento;
    private javax.swing.JTable tableLUsuario;
    private javax.swing.JTable tblEventosDisponiveis;
    private javax.swing.JTable tblEventosDisponiveis1;
    private javax.swing.JTable tblMeusIngressos;
    private javax.swing.JLabel titleComprar;
    private javax.swing.JLabel titleDashboard;
    private javax.swing.JLabel titleEventos;
    private javax.swing.JLabel titleMIngressos;
    private javax.swing.JLabel titlePerfil;
    private javax.swing.JLabel titleUsuarios;
    private javax.swing.JLabel txtCIngresso;
    private javax.swing.JLabel txtCancelarVenda;
    private javax.swing.JLabel txtClienteAtivo;
    private javax.swing.JLabel txtComprar;
    private javax.swing.JLabel txtConfirmarVenda;
    private javax.swing.JLabel txtDashboard;
    private javax.swing.JLabel txtEUsuario;
    private javax.swing.JLabel txtEmailR;
    private javax.swing.JLabel txtEmailSS;
    private javax.swing.JLabel txtEventos;
    private javax.swing.JLabel txtL;
    private javax.swing.JLabel txtLogin;
    private javax.swing.JLabel txtLoginL;
    private javax.swing.JLabel txtMIngressos;
    private javax.swing.JLabel txtNEvento;
    private javax.swing.JLabel txtNSenhaSS;
    private javax.swing.JLabel txtNUsuario;
    private javax.swing.JLabel txtPEmail;
    private javax.swing.JLabel txtPNSenha;
    private javax.swing.JLabel txtPSConfigs;
    private javax.swing.JLabel txtPTitle;
    private javax.swing.JLabel txtPUsuario;
    private javax.swing.JLabel txtPerfil;
    private javax.swing.JLabel txtR;
    private javax.swing.JLabel txtREvento;
    private javax.swing.JLabel txtRSenha;
    private javax.swing.JLabel txtRUsuario;
    private javax.swing.JLabel txtRegistrar;
    private javax.swing.JLabel txtSair;
    private javax.swing.JLabel txtSenhaL;
    private javax.swing.JLabel txtSenhaR;
    private javax.swing.JLabel txtTitle;
    private javax.swing.JLabel txtTitleL;
    private javax.swing.JLabel txtTitleR;
    private javax.swing.JLabel txtTitleSS;
    private javax.swing.JLabel txtToggleMenu;
    private javax.swing.JLabel txtUserR;
    private javax.swing.JLabel txtUserSS;
    private javax.swing.JLabel txtUsuarios;
    private javax.swing.JPanel usLUsuario;
    private javax.swing.JPanel usNUsuario;
    private javax.swing.JPanel usRUsuario;
    // End of variables declaration//GEN-END:variables
}
