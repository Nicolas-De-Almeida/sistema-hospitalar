// Uma função por tela/lista. Cada uma só chama o endpoint certo do back end.

import { api } from './api'

export const listarPacientes = () => api('/pacientes')
export const listarProfissionais = () => api('/profissionais')
export const listarConsultas = () => api('/consultas')
export const listarInternacoes = () => api('/internacoes')
export const listarQuartos = () => api('/quartos')


// Ações (mudam o estado de algo que já existe)
const patch = (caminho, corpo) => api(caminho, { method: 'PATCH', body: JSON.stringify(corpo ?? {}) })

// Consultas
export const confirmarConsulta = (id) => patch(`/consultas/${id}/confirmar`)
export const iniciarConsulta = (id) => patch(`/consultas/${id}/iniciar`)
export const concluirConsulta = (id, observacoes) => patch(`/consultas/${id}/concluir`, { observacoes })
export const cancelarConsulta = (id) => patch(`/consultas/${id}/cancelar`)
export const registrarFaltaConsulta = (id) => patch(`/consultas/${id}/falta`)

// Internações
export const darAltaInternacao = (id, dataAlta) => patch(`/internacoes/${id}/alta`, { dataAlta })

// Profissionais
export const alterarStatusProfissional = (id, ativo) => patch(`/profissionais/${id}/status`, { ativo })


// Cadastros
const post = (caminho, corpo) => api(caminho, { method: 'POST', body: JSON.stringify(corpo) })

export const cadastrarPaciente = (dados) => post('/pacientes', dados)

export const cadastrarProfissional = (dados) => post('/profissionais', dados)


// Edição, exclusão e consulta de detalhes 
const put = (caminho, corpo) => api(caminho, { method: 'PUT', body: JSON.stringify(corpo) })

export const atualizarPaciente = (id, dados) => put(`/pacientes/${id}`, dados)
export const atualizarProfissional = (id, dados) => put(`/profissionais/${id}`, dados)
export const excluirPaciente = (id) => api(`/pacientes/${id}`, { method: 'DELETE' })
export const excluirProfissional = (id) => api(`/profissionais/${id}`, { method: 'DELETE' })
export const buscarHistoricoPaciente = (id) => api(`/pacientes/${id}/historico`)


//  Consultas
export const agendarConsulta = (dados) => post('/consultas', dados)
export const remarcarConsulta = (id, dados) => patch(`/consultas/${id}/remarcar`, dados)

//  Internações
export const criarInternacao = (dados) => post('/internacoes', dados)
export const atualizarInternacao = (id, dados) => put(`/internacoes/${id}`, dados)
export const transferirInternacao = (id, quartoId) => patch(`/internacoes/${id}/quarto`, { quartoId })

//  Quartos
export const cadastrarQuarto = (dados) => post('/quartos', dados)
export const atualizarQuarto = (id, dados) => put(`/quartos/${id}`, dados)
export const listarQuartosDisponiveis = () => api('/quartos/disponiveis')
export const excluirQuarto = (id) => api(`/quartos/${id}`, { method: 'DELETE' })
export const listarTransferenciasInternacao = (id) => api(`/internacoes/${id}/transferencias`)

// Profissionais da especialidade que podem atender na data e horário
export const listarProfissionaisDisponiveis = (especialidade, data, horario) =>
    api(`/profissionais/disponiveis?${new URLSearchParams({ especialidade, data, horario })}`)

// Horários de atendimento do profissional
export const adicionarHorario = (profissionalId, dados) => post(`/profissionais/${profissionalId}/disponibilidades`, dados)
export const removerHorario = (profissionalId, horarioId) =>
    api(`/profissionais/${profissionalId}/disponibilidades/${horarioId}`, { method: 'DELETE' })

// Emergências 
export const listarEmergencias = () => api('/emergencias')
export const registrarEmergencia = (dados) => post('/emergencias', dados)
export const iniciarEmergencia = (id, profissionalId) => patch(`/emergencias/${id}/iniciar`, { profissionalId })
export const reclassificarEmergencia = (id, prioridade) => patch(`/emergencias/${id}/prioridade`, { prioridade })
export const concluirEmergencia = (id, observacoes) => patch(`/emergencias/${id}/concluir`, { observacoes })

// Procedimentos cirúrgicos
export const listarProcedimentos = () => api('/procedimentos')
export const agendarProcedimento = (dados) => post('/procedimentos', dados)
export const iniciarProcedimento = (id) => patch(`/procedimentos/${id}/iniciar`)
export const concluirProcedimento = (id, observacoesPosOperatorias) =>
    patch(`/procedimentos/${id}/concluir`, { observacoesPosOperatorias })
export const cancelarProcedimento = (id) => patch(`/procedimentos/${id}/cancelar`)