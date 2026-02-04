CREATE DATABASE GIngressos;
USE GIngressos;

-- Tabela 1: Clientes
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100),
    senha VARCHAR(255) NOT NULL,
    cargo VARCHAR(100)
);

-- Tabela 2: Eventos
CREATE TABLE eventos (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    nome_evento VARCHAR(100) NOT NULL,
    data_evento VARCHAR(20) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    capacidade INT NOT NULL
);

-- Tabela 3: Ingressos
CREATE TABLE ingressos (
    id_ingresso INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_evento INT NOT NULL,
    quantidade INT NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    data_compra DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (id_evento) REFERENCES eventos(id_evento)
);

-- Inserindo Clientes
INSERT INTO clientes (usuario, senha, email, cargo) VALUES 
('admin', '123', 'admin@email.com', 'Administrador'),
('teste', '123', 'teste@email.com', 'Cliente');

-- Inserindo Eventos
INSERT INTO eventos (nome_evento, data_evento, preco, capacidade) VALUES 
('Show de Rock Nacional', '25/12/2025', 150.00, 100),
('Workshop de Java', '20/02/2026', 250.00, 30);
