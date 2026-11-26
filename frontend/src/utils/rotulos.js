// Funções que transformam o que vem do back end (ex: "2026-11-17", "CLINICO_GERAL") em texto bonito para a tela, e escolhem as cores de status.

// "2026-11-17" -> "17/11/2026" (sem usar Date, para não ter erro de fuso horário)
export function formatarData(iso) {
    if (!iso) return '-'
    const [ano, mes, dia] = iso.split('-')
    return `${dia}/${mes}/${ano}`
}

// "09:00:00" -> "09:00"
export function formatarHora(hora) {
    return hora ? hora.substring(0, 5) : '-'
}

// Data de hoje no mesmo formato do back end ("aa-mm-dd"), no fuso do computador
export function dataHojeISO() {
    const agora = new Date()
    const mes = String(agora.getMonth() + 1).padStart(2, '0')
    const dia = String(agora.getDate()).padStart(2, '0')
    return `${agora.getFullYear()}-${mes}-${dia}`
}

// "2026-11-17T10:30:00" -> "17/11/2026 10:30"
export function formatarDataHora(iso) {
    if (!iso) return '-'
    return `${formatarData(iso.substring(0, 10))} ${formatarHora(iso.substring(11))}`
}

// Se o CPF tiver 11 números, coloca os pontos e o traço
export function formatarCpf(cpf) {
    if (!cpf) return '-'
    const numeros = cpf.replace(/\D/g, '')
    if (numeros.length !== 11) return cpf
    return numeros.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
}

const ESPECIALIDADES = {
    CLINICO_GERAL: 'Clínico geral',
    CARDIOLOGIA: 'Cardiologia',
    NEUROLOGIA: 'Neurologia',
    ORTOPEDIA: 'Ortopedia',
    PEDIATRIA: 'Pediatria',
    CIRURGIA_GERAL: 'Cirurgia geral',
    OUTRA: 'Outra',
}

export function rotuloEspecialidade(valor) {
    return ESPECIALIDADES[valor] || valor || '-'
}

// Opções do campo "Especialidade" de um médico especialista.
export const OPCOES_ESPECIALIDADE = Object.entries(ESPECIALIDADES)
    .filter(([valor]) => valor !== 'CLINICO_GERAL' && valor !== 'CIRURGIA_GERAL')


// Cada status da consulta: texto, cor do fundo/letra e cor da bolinha
const STATUS_CONSULTA = {
    AGENDADA: { texto: 'Agendada', cor: 'bg-amber-100 text-amber-700', bolinha: 'bg-amber-500' },
    CONFIRMADA: { texto: 'Confirmada', cor: 'bg-cyan-100 text-cyan-800', bolinha: 'bg-cyan-600' },
    EM_ATENDIMENTO: { texto: 'Em atendimento', cor: 'bg-blue-100 text-blue-700', bolinha: 'bg-blue-500' },
    CONCLUIDA: { texto: 'Concluída', cor: 'bg-emerald-100 text-emerald-700', bolinha: 'bg-emerald-500' },
    CANCELADA: { texto: 'Cancelada', cor: 'bg-red-100 text-red-700', bolinha: 'bg-red-500' },
    NAO_COMPARECEU: { texto: 'Não compareceu', cor: 'bg-slate-200 text-slate-700', bolinha: 'bg-slate-500' },
}

// Emergência 
const STATUS_INTERNACAO = {
    ATIVA: { texto: 'Ativa', cor: 'bg-cyan-50 text-cyan-800', bolinha: 'bg-cyan-600' },
    ENCERRADA: { texto: 'Encerrada', cor: 'bg-slate-100 text-slate-500', bolinha: 'bg-slate-400' },
}

const PADRAO = { cor: 'bg-slate-100 text-slate-600', bolinha: 'bg-slate-400' }

export function estiloStatusConsulta(status) {
    return STATUS_CONSULTA[status] || { ...PADRAO, texto: status }
}

export function estiloStatusInternacao(status) {
    return STATUS_INTERNACAO[status] || { ...PADRAO, texto: status }
}

const STATUS_EMERGENCIA = {
    AGUARDANDO_AVALIACAO: { texto: 'Aguardando avaliação', cor: 'bg-amber-100 text-amber-700', bolinha: 'bg-amber-500' },
    EM_ATENDIMENTO: { texto: 'Em atendimento', cor: 'bg-blue-100 text-blue-700', bolinha: 'bg-blue-500' },
    CONCLUIDO: { texto: 'Concluído', cor: 'bg-emerald-100 text-emerald-700', bolinha: 'bg-emerald-500' },
}

const STATUS_PROCEDIMENTO = {
    AGENDADO: { texto: 'Agendado', cor: 'bg-amber-100 text-amber-700', bolinha: 'bg-amber-500' },
    EM_ANDAMENTO: { texto: 'Em andamento', cor: 'bg-blue-100 text-blue-700', bolinha: 'bg-blue-500' },
    CONCLUIDO: { texto: 'Concluído', cor: 'bg-emerald-100 text-emerald-700', bolinha: 'bg-emerald-500' },
    CANCELADO: { texto: 'Cancelado', cor: 'bg-red-100 text-red-700', bolinha: 'bg-red-500' },
}

export function estiloStatusEmergencia(status) {
    return STATUS_EMERGENCIA[status] || { ...PADRAO, texto: status }
}

export function estiloStatusProcedimento(status) {
    return STATUS_PROCEDIMENTO[status] || { ...PADRAO, texto: status }
}

export const PRIORIDADES_EMERGENCIA = {
    CRITICA: { texto: 'Crítica', cor: 'bg-red-100 text-red-700', bolinha: 'bg-red-500' },
    ALTA: { texto: 'Alta', cor: 'bg-orange-100 text-orange-700', bolinha: 'bg-orange-500' },
    MODERADA: { texto: 'Moderada', cor: 'bg-yellow-100 text-yellow-700', bolinha: 'bg-yellow-500' },
    BAIXA: { texto: 'Baixa', cor: 'bg-slate-100 text-slate-600', bolinha: 'bg-slate-400' },
}

export function estiloPrioridade(prioridade) {
    return PRIORIDADES_EMERGENCIA[prioridade] || { ...PADRAO, texto: prioridade }
}

export const TIPOS_CIRURGIA = {
    ELETIVA: 'Eletiva',
    URGENCIA: 'Urgência',
    EMERGENCIA: 'Emergência',
}

// Tipos de quarto (limite de leitos que o tipo permite)
export const TIPOS_QUARTO = {
    ENFERMARIA: { texto: 'Enfermaria', limite: 6 },
    APARTAMENTO: { texto: 'Apartamento', limite: 2 },
    UTI: { texto: 'UTI', limite: 1 },
    ISOLAMENTO: { texto: 'Isolamento', limite: 1 },
}

export function rotuloTipoQuarto(valor) {
    return TIPOS_QUARTO[valor]?.texto ?? valor ?? '-'
}

//  Áreas do cirurgião
export const AREAS_CIRURGICAS = [
    'Cirurgia geral',
    'Cirurgia cardíaca',
    'Cirurgia vascular',
    'Cirurgia torácica',
    'Cirurgia ortopédica',
    'Neurocirurgia',
    'Cirurgia plástica',
    'Cirurgia do aparelho digestivo',
    'Cirurgia urológica',
    'Cirurgia ginecológica',
    'Cirurgia pediátrica',
    'Cirurgia oftalmológica',
    'Cirurgia de cabeça e pescoço',
]

// Dias da semana
export const DIAS_SEMANA = [
    ['MONDAY', 'Seg'],
    ['TUESDAY', 'Ter'],
    ['WEDNESDAY', 'Qua'],
    ['THURSDAY', 'Qui'],
    ['FRIDAY', 'Sex'],
    ['SATURDAY', 'Sáb'],
    ['SUNDAY', 'Dom'],
]

// Lista de internações só de quem está internado AGORA
export function quartosPorPaciente(internacoes) {
    const mapa = {}
    for (const internacao of internacoes) {
        if (internacao.status === 'ATIVA') mapa[internacao.pacienteId] = internacao.quartoNumero
    }
    return mapa
}