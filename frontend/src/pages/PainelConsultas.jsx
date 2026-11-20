import { useState } from 'react'
import { Plus, CheckCheck, Play, CircleCheck, CalendarClock, Ban, UserX } from 'lucide-react'
import ModalAgendarConsulta from '../components/modals/ModalAgendarConsulta'
import LinhaEstado from '../components/LinhaEstado'
import BadgeStatus from '../components/BadgeStatus'
import BotaoAcao from '../components/BotaoAcao'
import AvisoErro from '../components/AvisoErro'

import { useDados } from '../hooks/useDados'
import { useAcao } from '../hooks/useAcao'
import {
    listarConsultas,
    confirmarConsulta,
    iniciarConsulta,
    concluirConsulta,
    cancelarConsulta,
    registrarFaltaConsulta,
} from '../services/hospitalService'
import { getIniciais } from '../utils/primeiraLetraMaiuscula'
import { formatarData, formatarHora, estiloStatusConsulta } from '../utils/rotulos'

export default function GestaoConsultas() {
    const [modalNovaConsulta, setModalNovaConsulta] = useState(false)

    // Lista de consultas
    const { dados: consultas, carregando, erro, recarregar } = useDados(listarConsultas)
    const { executar, ocupado, erroAcao } = useAcao(recarregar)

    // Pede observações e conclui. Se a pessoa clicar em "Cancelar", não faz nada.
    function concluir(consulta) {
        const observacoes = window.prompt('Observações da consulta (opcional):', '')
        if (observacoes === null) return
        executar(() => concluirConsulta(consulta.id, observacoes.trim() || null))
    }

    function cancelar(consulta) {
        if (window.confirm(`Cancelar a consulta de ${consulta.pacienteNome}?`)) {
            executar(() => cancelarConsulta(consulta.id))
        }
    }

    function registrarFalta(consulta) {
        if (window.confirm(`Registrar que ${consulta.pacienteNome} não compareceu?`)) {
            executar(() => registrarFaltaConsulta(consulta.id))
        }
    }

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <div>
                    <span className="font-bold tracking-wider text-cyan-800 uppercase">GESTÃO DE CONSULTAS</span>
                </div>

                <button onClick={() => setModalNovaConsulta(true)} className="flex items-center gap-2 bg-cyan-800 hover:bg-cyan-900 text-white font-semibold text-sm px-4 py-2.5 rounded-xl shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4" />
                    <span>Nova consulta</span>
                </button>
            </div>

            <AvisoErro mensagem={erroAcao} />

            <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
                <div className="p-5 border-b border-slate-100">
                    <h2 className="font-bold text-slate-800 text-base">Agenda de consultas</h2>
                    <p className="text-xs text-slate-400 mt-0.5">{consultas.length} consultas encontradas</p>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full text-left text-xs">
                        <thead>
                            <tr className="text-slate-400 font-bold border-b border-slate-100 bg-slate-50/50">
                                <th className="py-3 px-5">DATA</th>
                                <th className="py-3 px-5">HORÁRIO</th>
                                <th className="py-3 px-5">PACIENTE</th>
                                <th className="py-3 px-5">PROFISSIONAL</th>
                                <th className="py-3 px-5">MOTIVO</th>
                                <th className="py-3 px-5">STATUS</th>
                                <th className="py-3 px-5 text-right">AÇÕES</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            <LinhaEstado
                                colunas={7}
                                carregando={carregando}
                                erro={erro}
                                quantidade={consultas.length}
                                vazio="Nenhuma consulta encontrada."/>
                            {consultas.map((consulta) => (
                                <tr key={consulta.id} className="hover:bg-slate-50/60 transition-colors">
                                    <td className="py-3.5 px-5 text-slate-800 font-bold">{formatarData(consulta.data)}</td>
                                    <td className="py-3.5 px-5 text-slate-800 font-bold">{formatarHora(consulta.horario)}</td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-lg bg-teal-50 text-teal-800 font-bold text-xs flex items-center justify-center shrink-0">
                                                {getIniciais(consulta.pacienteNome)}
                                            </div>
                                            <span className="font-bold text-slate-800 text-xs">
                                                {consulta.pacienteNome}
                                            </span>
                                        </div>
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">{consulta.profissionalNome}</td>
                                    <td className="py-3.5 px-5 text-slate-600">{consulta.motivo}</td>
                                    <td className="py-3.5 px-5">
                                        <BadgeStatus estilo={estiloStatusConsulta(consulta.status)} />
                                    </td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center justify-end gap-2 text-slate-400">
                                            {/* Só aparecem as ações que a regra de negócio permite para o status atual */}
                                            {consulta.status === 'AGENDADA' && (
                                                <BotaoAcao titulo="Confirmar" desabilitado={ocupado} onClick={() => executar(() => confirmarConsulta(consulta.id))}>
                                                    <CheckCheck className="w-4 h-4" />
                                                </BotaoAcao>
                                            )}
                                            {(consulta.status === 'AGENDADA' || consulta.status === 'CONFIRMADA') && (
                                                <>
                                                    <BotaoAcao titulo="Iniciar atendimento" desabilitado={ocupado} onClick={() => executar(() => iniciarConsulta(consulta.id))}>
                                                        <Play className="w-4 h-4" />
                                                    </BotaoAcao>
                                                    <BotaoAcao titulo="Remarcar">
                                                        <CalendarClock className="w-4 h-4" />
                                                    </BotaoAcao>
                                                    <BotaoAcao titulo="Registrar falta" desabilitado={ocupado} onClick={() => registrarFalta(consulta)}>
                                                        <UserX className="w-4 h-4" />
                                                    </BotaoAcao>
                                                    <BotaoAcao titulo="Cancelar consulta" perigo desabilitado={ocupado} onClick={() => cancelar(consulta)}>
                                                        <Ban className="w-4 h-4" />
                                                    </BotaoAcao>
                                                </>
                                            )}
                                            {consulta.status === 'EM_ATENDIMENTO' && (
                                                <BotaoAcao titulo="Concluir consulta" desabilitado={ocupado} onClick={() => concluir(consulta)}>
                                                    <CircleCheck className="w-4 h-4" />
                                                </BotaoAcao>
                                            )}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>

            <ModalAgendarConsulta isOpen={modalNovaConsulta} onClose={() => setModalNovaConsulta(false)} onSalvo={recarregar} />
        </div>
    )
}