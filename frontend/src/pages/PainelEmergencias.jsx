import { useState } from 'react'
import { Plus, Play, CircleCheck, ArrowUpDown } from 'lucide-react'
import ModalRegistrarEmergencia from '../components/modals/ModalRegistrarEmergencia'
import ModalIniciarEmergencia from '../components/modals/ModalIniciarEmergencia'
import ModalPrioridadeEmergencia from '../components/modals/ModalPrioridadeEmergencia'
import ModalConcluirAtendimento from '../components/modals/ModalConcluirAtendimento'
import LinhaEstado from '../components/LinhaEstado'
import BadgeStatus from '../components/BadgeStatus'
import BotaoAcao from '../components/BotaoAcao'

import { useDados } from '../hooks/useDados'
import { listarEmergencias, concluirEmergencia } from '../services/hospitalService'
import { getIniciais } from '../utils/primeiraLetraMaiuscula'
import { formatarData, formatarHora, estiloStatusEmergencia, estiloPrioridade } from '../utils/rotulos'

export default function GestaoEmergencias() {
    const [modalNova, setModalNova] = useState(false)
    // Emergência escolhida em cada ação (null = modal fechado)
    const [iniciando, setIniciando] = useState(null)
    const [reclassificando, setReclassificando] = useState(null)
    const [concluindo, setConcluindo] = useState(null)

    const { dados: emergencias, carregando, erro, recarregar } = useDados(listarEmergencias)

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <div>
                    <span className="font-bold tracking-wider text-cyan-800 uppercase">GESTÃO DE EMERGÊNCIAS</span>
                </div>

                <button onClick={() => setModalNova(true)} className="flex items-center gap-2 bg-cyan-800 hover:bg-cyan-900 text-white font-semibold text-sm px-4 py-2.5 rounded-xl shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4" />
                    <span>Nova emergência</span>
                </button>
            </div>

            <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
                <div className="p-5 border-b border-slate-100">
                    <h2 className="font-bold text-slate-800 text-base">Atendimentos de emergência</h2>
                    <p className="text-xs text-slate-400 mt-0.5">{emergencias.length} atendimentos encontrados</p>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full text-left text-xs">
                        <thead>
                            <tr className="text-slate-400 font-bold border-b border-slate-100 bg-slate-50/50">
                                <th className="py-3 px-5">CHEGADA</th>
                                <th className="py-3 px-5">PACIENTE</th>
                                <th className="py-3 px-5">PRIORIDADE</th>
                                <th className="py-3 px-5">QUEIXA</th>
                                <th className="py-3 px-5">PROFISSIONAL</th>
                                <th className="py-3 px-5">STATUS</th>
                                <th className="py-3 px-5 text-right">AÇÕES</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            <LinhaEstado
                                colunas={7}
                                carregando={carregando}
                                erro={erro}
                                quantidade={emergencias.length}
                                vazio="Nenhuma emergência registrada."/>
                            {emergencias.map((emergencia) => (
                                <tr key={emergencia.id} className="hover:bg-slate-50/60 transition-colors">
                                    <td className="py-3.5 px-5 text-slate-800 font-bold">
                                        {formatarData(emergencia.dataChegada)} {formatarHora(emergencia.horarioChegada)}
                                    </td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-lg bg-teal-50 text-teal-800 font-bold text-xs flex items-center justify-center shrink-0">
                                                {getIniciais(emergencia.pacienteNome)}
                                            </div>
                                            <span className="font-bold text-slate-800 text-xs">{emergencia.pacienteNome}</span>
                                        </div>
                                    </td>
                                    <td className="py-3.5 px-5">
                                        <BadgeStatus estilo={estiloPrioridade(emergencia.prioridade)} />
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">{emergencia.queixaPrincipal}</td>
                                    <td className="py-3.5 px-5 text-slate-600">{emergencia.profissionalNome ?? '-'}</td>
                                    <td className="py-3.5 px-5">
                                        <BadgeStatus estilo={estiloStatusEmergencia(emergencia.status)} />
                                    </td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center justify-end gap-2 text-slate-400">
                                            {emergencia.status === 'AGUARDANDO_AVALIACAO' && (
                                                <BotaoAcao titulo="Iniciar avaliação" onClick={() => setIniciando(emergencia)}>
                                                    <Play className="w-4 h-4" />
                                                </BotaoAcao>
                                            )}
                                            {emergencia.status !== 'CONCLUIDO' && (
                                                <BotaoAcao titulo="Reclassificar prioridade" onClick={() => setReclassificando(emergencia)}>
                                                    <ArrowUpDown className="w-4 h-4" />
                                                </BotaoAcao>
                                            )}
                                            {emergencia.status === 'EM_ATENDIMENTO' && (
                                                <BotaoAcao titulo="Concluir atendimento" onClick={() => setConcluindo(emergencia)}>
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

            <ModalRegistrarEmergencia isOpen={modalNova} onClose={() => setModalNova(false)} onSalvo={recarregar} />

            <ModalIniciarEmergencia
                key={`iniciar-${iniciando?.id ?? 'nenhuma'}`}
                isOpen={iniciando !== null}
                onClose={() => setIniciando(null)}
                onSalvo={recarregar}
                emergencia={iniciando}/>

            <ModalPrioridadeEmergencia
                key={`prioridade-${reclassificando?.id ?? 'nenhuma'}`}
                isOpen={reclassificando !== null}
                onClose={() => setReclassificando(null)}
                onSalvo={recarregar}
                emergencia={reclassificando}/>

            <ModalConcluirAtendimento
                key={`concluir-${concluindo?.id ?? 'nenhuma'}`}
                isOpen={concluindo !== null}
                onClose={() => setConcluindo(null)}
                onSalvo={recarregar}
                titulo="Concluir emergência"
                rotuloObservacoes="Observações do atendimento"
                nomePaciente={concluindo?.pacienteNome}
                concluir={(observacoes) => concluirEmergencia(concluindo.id, observacoes)}/>
        </div>
    )
}