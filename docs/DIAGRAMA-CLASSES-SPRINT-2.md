# Diagrama de Classes — Sprint 2

O diagrama representa o modelo de domínio implementado no backend. As camadas de API
(`Controller`, `Service`, `Repository` e DTOs) não aparecem porque não fazem parte do
modelo de domínio.

```mermaid
classDiagram
direction LR

class Pessoa {
  <<abstract>>
  -Long id
  -String nome
  -String telefone
  -String email
  +setNome(nome)
  +setTelefone(telefone)
  +setEmail(email)
}

class Paciente {
  -String cpf
  -LocalDate dataNascimento
  -Endereco endereco
  +idade() int
  +possuiInternacaoAtiva() boolean
  +getConsultas() List~Consulta~
}

class Endereco {
  <<value object>>
  -String logradouro
  -String numero
  -String complemento
  -String bairro
  -String cidade
  -String uf
  -String cep
}

class ProfissionalSaude {
  <<abstract>>
  -String registroProfissional
  -Especialidade especialidade
  -boolean ativo
  +temExpedienteEm(data, hora) boolean
  +temConflitoEm(data, hora, ignorar) boolean
  +disponivelEm(data, hora) boolean
  +adicionarDisponibilidade(dia, inicio, fim, duracao)
}

class MedicoClinicoGeral {
  +encaminhar(paciente, especialista, data, horario, motivo) ConsultaEspecializada
}

class MedicoEspecialista {
  +setEspecialidade(especialidade)
}

class Cirurgiao {
  -String areaCirurgica
  +getProcedimentosRealizados() List~ProcedimentoCirurgico~
}

class DisponibilidadeAtendimento {
  -Long id
  -DayOfWeek diaSemana
  -LocalTime horaInicio
  -LocalTime horaFim
  -int duracaoConsultaMin
  +cobre(data, hora) boolean
}

class Atendimento {
  <<abstract>>
  -Long id
  -LocalDate data
  -LocalTime horario
  -String observacoes
  -LocalDateTime criadoEm
  +validarCondicoes()*
  +ocupaAgenda()* boolean
  +getSituacao()* String
  +validarAgendamento()
  +registrar(tipo, descricao) RegistroClinico
}

class Consulta {
  <<abstract>>
  -String motivo
  -StatusConsulta status
  +confirmar()
  +iniciar()
  +concluir(observacoes)
  +cancelar()
  +registrarFalta()
  +remarcar(data, horario)
}

class ConsultaRotina

class ConsultaEspecializada {
  -Especialidade especialidade
  +validarCondicoes()
}

class AtendimentoEmergencia {
  -PrioridadeEmergencia prioridade
  -StatusEmergencia status
  -String queixaPrincipal
  +iniciarAvaliacao(profissional)
  +setPrioridade(prioridade)
  +concluir(observacoes)
}

class ProcedimentoCirurgico {
  -String descricao
  -TipoCirurgia tipoCirurgia
  -String observacoesPreOperatorias
  -String observacoesPosOperatorias
  -StatusProcedimento status
  -LocalDate dataEfetiva
  -LocalTime horarioEfetivo
  +iniciar()
  +concluir(observacoes)
  +cancelar()
}

class RegistroClinico {
  -Long id
  -TipoRegistro tipo
  -LocalDateTime dataHora
  -String descricao
}

class Internacao {
  -Long id
  -LocalDate dataEntrada
  -LocalDate dataPrevistaAlta
  -LocalDate dataEfetivaAlta
  -String observacoes
  -StatusInternacao status
  +darAlta(dataAlta)
  +estaAtiva() boolean
}

class Quarto {
  -Long id
  -String numero
  -int andar
  -int capacidadeMaxima
  -TipoQuarto tipo
  +ocupacaoAtual() int
  +temVaga() boolean
  +situacao() SituacaoQuarto
  +atualizarCaracteristicas(numero, andar, capacidade, tipo)
}

class StatusConsulta {
  <<enumeration>>
  AGENDADA
  CONFIRMADA
  EM_ATENDIMENTO
  CONCLUIDA
  CANCELADA
  NAO_COMPARECEU
}

class StatusEmergencia {
  <<enumeration>>
  AGUARDANDO_AVALIACAO
  EM_ATENDIMENTO
  CONCLUIDO
}

class PrioridadeEmergencia {
  <<enumeration>>
  BAIXA
  MODERADA
  ALTA
  CRITICA
}

class StatusProcedimento {
  <<enumeration>>
  AGENDADO
  EM_ANDAMENTO
  CONCLUIDO
  CANCELADO
}

class StatusInternacao {
  <<enumeration>>
  ATIVA
  ENCERRADA
}

class TipoQuarto {
  <<enumeration>>
  ENFERMARIA
  APARTAMENTO
  UTI
  ISOLAMENTO
}

class SituacaoQuarto {
  <<enumeration>>
  DISPONIVEL
  OCUPADO
}

class Especialidade {
  <<enumeration>>
}

class TipoCirurgia {
  <<enumeration>>
}

class TipoRegistro {
  <<enumeration>>
}

Pessoa <|-- Paciente
Pessoa <|-- ProfissionalSaude
ProfissionalSaude <|-- MedicoClinicoGeral
ProfissionalSaude <|-- MedicoEspecialista
ProfissionalSaude <|-- Cirurgiao

Atendimento <|-- Consulta
Consulta <|-- ConsultaRotina
Consulta <|-- ConsultaEspecializada
Atendimento <|-- AtendimentoEmergencia
Atendimento <|-- ProcedimentoCirurgico

Paciente "1" *-- "1" Endereco
Paciente "1" <-- "0..*" Atendimento : paciente
ProfissionalSaude "0..1" <-- "0..*" Atendimento : responsavel
ProfissionalSaude "1" *-- "0..*" DisponibilidadeAtendimento
Atendimento "1" *-- "0..*" RegistroClinico

Paciente "1" <-- "0..*" Internacao
ProfissionalSaude "1" <-- "0..*" Internacao
Quarto "1" <-- "0..*" Internacao

Consulta --> StatusConsulta
ConsultaEspecializada --> Especialidade
ProfissionalSaude --> Especialidade
AtendimentoEmergencia --> PrioridadeEmergencia
AtendimentoEmergencia --> StatusEmergencia
ProcedimentoCirurgico --> TipoCirurgia
ProcedimentoCirurgico --> StatusProcedimento
RegistroClinico --> TipoRegistro
Internacao --> StatusInternacao
Quarto --> TipoQuarto
Quarto ..> SituacaoQuarto : calcula
```

## Regras representadas

- As modalidades de atendimento reutilizam atributos e comportamentos de `Atendimento`.
- Consultas de rotina e especializadas compartilham o ciclo de estados de `Consulta`.
- Cada categoria concreta de profissional possui responsabilidades próprias.
- Uma emergência pode aguardar avaliação sem profissional; os outros atendimentos exigem responsável.
- Uma internação associa paciente, profissional e quarto e ocupa uma vaga enquanto estiver ativa.
- Endereço pertence ao paciente e registro clínico pertence ao atendimento.
