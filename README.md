# 🎟️ GIngressos - Sistema de Gestão de Eventos e Vendas

Trabalho final desenvolvido para a disciplina de **Linguagem de Programação II** no 2º ano do curso técnico em Informática do **IFAL - Campus Arapiraca**.

## 📖 Sobre o Projeto
O **GIngressos** é uma aplicação desktop desenvolvida em Java para facilitar o gerenciamento de eventos e a comercialização de ingressos. O sistema permite que administradores gerenciem o catálogo de eventos enquanto clientes podem realizar compras de forma segura e rápida.

## ✨ Funcionalidades
- **🔐 Controle de Acesso:** Sistema de login com diferenciação de cargos (Administrador e Cliente).
- **📅 Gestão de Eventos:** Cadastro completo de eventos com data, preço e capacidade de público.
- **🛒 Venda de Ingressos:** Processamento de compras com cálculo automático de valor total e registro de data/hora.
- **👥 Gestão de Clientes:** Cadastro e armazenamento de informações de usuários.

## 🛠️ Tecnologias Utilizadas
- **Linguagem:** Java
- **Interface:** Java Swing (NetBeans)
- **Banco de Dados:** MySQL 8.0

## 🗄️ Estrutura do Banco de Dados
O banco de dados `GIngressos` é composto por três tabelas principais:
1. `clientes`: Armazena credenciais e perfis de acesso.
2. `eventos`: Registra as informações dos shows e workshops.
3. `ingressos`: Tabela de relacionamento que registra cada venda realizada.

## 🚀 Como executar
1. Certifique-se de ter o MySQL instalado.
2. Execute o script contido em `sql/ProjetoFinal.sql` para criar o banco de dados e as tabelas.
3. Abra o projeto no NetBeans e configure a conexão JDBC.
