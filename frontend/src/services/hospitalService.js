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
export const excluirPaciente = (id) => api(`/pacientes/${id}`, { method: 'DELETE' })
export const buscarHistoricoPaciente = (id) => api(`/pacientes/${id}/historico`)


//  Agendamentos e internações
export const agendarConsulta = (dados) => post('/consultas', dados)
export const criarInternacao = (dados) => post('/internacoes', dados)
export const listarQuartosDisponiveis = () => api('/quartos/disponiveis')

// Profissionais da especialidade que podem atender na data e horário
export const listarProfissionaisDisponiveis = (especialidade, data, horario) =>
    api(`/profissionais/disponiveis?${new URLSearchParams({ especialidade, data, horario })}`)