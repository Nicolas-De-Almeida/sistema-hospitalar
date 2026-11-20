import { CalendarDays, Bed, DoorOpen, ArrowBigRight, UserPlus, CalendarPlus, Plus } from 'lucide-react'
import { PegarDataCalendario } from '../utils/Calendario'
import { useState } from 'react'

import ModalPaciente from '../components/modals/ModalPaciente'
import ModalAgendarConsulta from '../components/modals/ModalAgendarConsulta'
import ModalNovaInternacao from '../components/modals/ModalNovaInternacao'
import LinhaEstado from '../components/LinhaEstado'

import { useDados } from '../hooks/useDados'
import { listarConsultas, listarInternacoes, listarQuartos } from '../services/hospitalService'
import { dataHojeISO, formatarHora } from '../utils/rotulos'


export default function PainelPrincipal() {
    const dataAtual = PegarDataCalendario();

    const[modalPacienteAberto, setModalNovoPacienteAberto] = useState(false)
    const[modalAgendarConsultaAberto, setModalAgendarConsultaAberto] = useState(false)
    const[modalNovaInternacaoAberto, setModalNovaInternacaoAberto] = useState(false)

    // Dados reais, vindos do back end
    const consultas = useDados(listarConsultas)
    const internacoes = useDados(listarInternacoes)
    const quartos = useDados(listarQuartos)

    // Consultas de hoje, da mais cedo para a mais tarde
    const hoje = dataHojeISO()
    const consultasHoje = consultas.dados
        .filter((c) => c.data === hoje)
        .sort((a, b) => a.horario.localeCompare(b.horario))
    const consultasAbertas = consultasHoje.filter((c) => c.status === 'AGENDADA' || c.status === 'CONFIRMADA').length
    const consultasConcluidas = consultasHoje.filter((c) => c.status === 'CONCLUIDA').length

    // Internações ativas
    const internacoesAtivas = internacoes.dados.filter((i) => i.status === 'ATIVA').length

    const totalQuartos = quartos.dados.length
    const quartosDisponiveis = quartos.dados.filter((q) => q.situacao === 'DISPONIVEL').length
    const quartosOcupados = totalQuartos - quartosDisponiveis

    // Enquanto carrega mostra "...", se der erro mostra "-"
    const mostrar = (lista, valor) => (lista.carregando ? '...' : lista.erro ? '-' : valor)

    return (
        <div>
            <div className='flex items-center justify-between mb-5'>
                <div className="flex flex-col">
                    <div className="font-bold">
                        <p>Visão geral</p>
                    </div>
                    <p className="text-slate-400">Acompanhamento dos atendimentos e ocopações da unidade.</p>
                </div>
                <div className="bg-white border border-slate-200 rounded-xl p-3 px-5 shadow-sm inline-flex items-center gap-3">
                    <CalendarDays className="w-5 h-5 text-cyan-800" />
                    <p className="text-cyan-950 font-bold text-lg">{dataAtual}</p>
                </div>
            </div>

            <div className='grid grid-cols-1 md:grid-cols-3 gap-5'>
                {/* Card1: consultas de hoje, Card2: internações ativas, Card3: quartos disponíveis */}
                <span className="flex flex-col gap-1 border border-slate-200 shadow-sm rounded-lg p-5">
                    <div className="flex justify-between">
                        <CalendarDays className="bg-teal-200 text-cyan-800 rounded-lg p-1" />
                        <ArrowBigRight className="text-slate-300" />
                    </div>
                    <div className="font-bold flex flex-col gap-1">
                        <h1 className="text-lg">{mostrar(consultas, consultasHoje.length)}</h1>
                        <p className="text-sm">Consultas hoje</p>
                        <p className="text-slate-400 text-sm">
                            {mostrar(consultas, `${consultasAbertas} a realizar, ${consultasConcluidas} concluídas`)}
                        </p>
                    </div>
                </span>

                <span className="border border-slate-200 shadow-sm rounded-lg p-5">
                    <div className="flex justify-between">
                        <Bed className="bg-blue-200 text-blue-800 rounded-lg p-1" />
                        <ArrowBigRight className="text-slate-300" />
                    </div>
                    <div className="font-bold flex flex-col gap-1">
                        <h1 className="text-lg">{mostrar(internacoes, internacoesAtivas)}</h1>
                        <p className="text-sm">Internações ativas</p>
                        <p className="text-slate-400 text-sm">Em acompanhamento</p>
                    </div>
                </span>

                <span className="border border-slate-200 shadow-sm rounded-lg p-5">
                    <div className="flex justify-between">
                        <DoorOpen className="bg-green-200 text-green-800 rounded-lg p-1" />
                        <ArrowBigRight className="text-slate-300" />
                    </div>
                    <div className="font-bold flex flex-col gap-1">
                        <h1>
                            <span className="font-bold text-lg">{mostrar(quartos, quartosDisponiveis)}</span>{' '}
                            <span className="text-slate-400">de {mostrar(quartos, totalQuartos)}</span>
                        </h1>
                        <p className="text-sm">Quartos disponíveis</p>
                        <p className="text-slate-400 text-sm">
                            {mostrar(quartos, `${quartosOcupados} quartos ocupados no momento`)}
                        </p>
                    </div>
                </span>
            </div>

            <div className="flex flex-wrap items-center justify-between gap-4 mt-8">
                <div>
                    <p className="font-bold text-slate-700">Ações rápidas</p>
                </div>

                <div className="flex flex-wrap gap-4">
                    <button onClick={() => setModalNovoPacienteAberto(true)} className="flex items-center text-white w-48 h-9 font-bold bg-cyan-800 hover:bg-cyan-900 rounded-lg p-2 gap-2 cursor-pointer transition-colors">
                        <UserPlus className="w-4 h-4"/>
                        <span>Novo paciente</span>
                        <Plus className="w-4 h-4 ml-auto"/>
                    </button>

                    <button onClick={() => setModalAgendarConsultaAberto(true)} className="flex items-center text-white w-51 h-9 font-bold bg-cyan-800 hover:bg-cyan-900 rounded-lg p-2 gap-2 cursor-pointer transition-colors">
                        <CalendarPlus className="w-4 h-4"/>
                        <span>Agendar consulta</span>
                        <Plus className="w-4 h-4 ml-auto"/>
                    </button>

                    <button onClick={() => setModalNovaInternacaoAberto(true)} className="flex items-center text-white w-48 h-9 font-bold bg-cyan-800 hover:bg-cyan-900 rounded-lg p-2 gap-2 cursor-pointer transition-colors">
                        <Bed className="w-4 h-4"/>
                        <span>Nova internação</span>
                        <Plus className="w-4 h-4 ml-auto"/>
                    </button>
                </div>
            </div>

            {/* Agenda do dia */}
            <div className="border border-slate-200 rounded-lg shadow-sm mt-5 p-5 overflow-x-auto">
                <p className="font-bold text-slate-700 mb-3">Consultas de hoje</p>
                <table className="w-full text-left text-sm">
                    <thead>
                        <tr className="text-xs text-slate-400 border-b border-slate-100">
                            <th className="py-2">Horário</th>
                            <th className="py-2">Paciente</th>
                            <th className="py-2">Profissional</th>
                            <th className="py-2">Motivo</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                        <LinhaEstado
                            colunas={4}
                            carregando={consultas.carregando}
                            erro={consultas.erro}
                            quantidade={consultasHoje.length}
                            vazio="Nenhuma consulta marcada para hoje."/>
                        {consultasHoje.map((consulta) => (
                            <tr key={consulta.id}>
                                <td className="py-3 font-bold text-slate-800">{formatarHora(consulta.horario)}</td>
                                <td className="py-3 font-semibold text-slate-800">{consulta.pacienteNome}</td>
                                <td className="py-3 font-semibold text-slate-800">{consulta.profissionalNome}</td>
                                <td className="py-3 text-slate-600">{consulta.motivo}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <ModalPaciente isOpen={modalPacienteAberto} onClose={() => setModalNovoPacienteAberto(false)}/>
            <ModalAgendarConsulta isOpen={modalAgendarConsultaAberto} onClose={() => setModalAgendarConsultaAberto(false)} onSalvo={consultas.recarregar}/>
            <ModalNovaInternacao isOpen={modalNovaInternacaoAberto} onClose={() => setModalNovaInternacaoAberto(false)} onSalvo={() => Promise.all([internacoes.recarregar(), quartos.recarregar()])}/>
        </div>
    )
}