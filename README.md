# 🏥 Sistema Hospitalar

Projeto de sistema hospitalar desenvolvido como trabalho prático da disciplina de Programação Modular

---

## 💡 Objetivo

Desenvolver um Sistema de Informação Hospitalar para gerenciar as operações de um hospital, centralizando informações de pacientes, profissionais da saúde, consultas, internações e quartos.

O sistema será desenvolvido utilizando conceitos de Programação Orientada a Objetos (POO), arquitetura em camadas, API REST com Spring Boot, persistência de dados, tratamento de exceções e testes automatizados.

---

## 👥 Alunos Integrantes

- Nicolas De Almeida
- Diogo Gouvêa Bastos Braga
- 
- 

## 🌐 Tecnologias

- Java
- HTML
- CSS
- Spring Boot
- Spring Data JPA
- MySQL
- Maven

---

## 🎯 Funcionalidades

- Gerenciamento de pacientes
- Gerenciamento de profissionais da saúde
- Agendamento e controle de consultas
- Controle de internações
- Gerenciamento de quartos hospitalares
- Controle de disponibilidade de atendimento
- Registro do hist´orico de atendimentos dos pacientes
- Consulta de informa¸c˜oes m´edicas e administrativas

---

## ❗️ Regras de Negócio

1. Um paciente poderá possuir diversas consultas e internações ao longo do tempo.
2. Consultas deverão estar associadas a um profissional da saúde responsável e a um paciente.
3. Um profissional não poderá possuir dois atendimentos agendados para o mesmo horário.
4. Uma internação deverá estar associada a um paciente e a um quarto disponível.
5. Um quarto não poderá ultrapassar sua capacidade máxima de ocupação.
6. O sistema deverá manter o histórico de consultas e internações dos pacientes.
7. Todas as operações realizadas deverão respeitar as regras de disponibilidade de recursos e integridade dos dados.

---

## 🎯 Características e Requisitos do Sistema

### Paciente

O sistema deverá armazenar:

- Nome
- CPF
- Data de nascimento
- Telefone
- Endereço
- E-mail para contato

### Profissional da Saúde

O sistema deverá armazenar:

- Nome
- Registro profissional
- Especialidade
- Telefone
- E-mail para contato

### Consulta

Uma consulta deverá possuir:

- Paciente
- Profissional responsável
- Data
- Horário
- Motivo da consulta
- Observações médicas

### Internação

Uma internação deverá possuir:

- Paciente
- Profissional responsável
- Quarto
- Data de entrada
- Data prevista de alta
- Data efetiva de alta
- Observações

### Quarto

Um quarto deverá possuir:

- Número de identificação
- Andar
- Capacidade máxima de pacientes
- Situação atual (disponível ou ocupado)

### Histórico Médico

O sistema deverá permitir consultar o histórico de um paciente contendo:

- Consultas realizadas
- Internações realizadas
- Informações relevantes registradas durante os atendimentos

---

## 🚦 Status

🧱 Em desenvolvimento.
