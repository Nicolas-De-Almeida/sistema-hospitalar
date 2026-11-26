import { useState } from 'react'
import { Plus, Play, CircleCheck, Ban } from 'lucide-react'
import ModalAgendarProcedimento from '../components/modals/ModalAgendarProcedimento'
import ModalConcluirAtendimento from '../components/modals/ModalConcluirAtendimento'
import LinhaEstado from '../components/LinhaEstado'
import BadgeStatus from '../components/BadgeStatus'
import BotaoAcao from '../components/BotaoAcao'
import AvisoErro from '../components/AvisoErro'

import { useDados } from '../hooks/useDados'
import { useAcao } from '../hooks/useAcao'
import {
    listarProcedimentos,
    iniciarProcedimento,
    concluirProcedimento,
    cancelarProcedimento,
} from '../services/hospitalService'
import { getIniciais } from '../utils/primeiraLetraMaiuscula'
import { formatarData, formatarHora, estiloStatusProcedimento, TIPOS_CIRURGIA } from '../utils/rotulos'

export default function GestaoProcedimentos() {
    const [modalNovo, setModalNovo] = useState(false)
    // Procedimento que está sendo concluído (null = modal fechado)
    const [concluindo, setConcluindo] = useState(null)

    const { dados: procedimentos, carregando, erro, recarregar } = useDados(listarProcedimentos)
    const { executar, ocupado, erroAcao } = useAcao(recarregar)

    function cancelar(procedimento) {
        if (window.confirm(`Cancelar a cirurgia de ${procedimento.pacienteNome}?`)) {
            executar(() => cancelarProcedimento(procedimento.id))
        }
    }

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <div>
                    <span className="font-bold tracking-wider text-cyan-800 uppercase">GESTÃO DE CIRURGIAS</span>
                </div>

                <button onClick={() => setModalNovo(true)} className="flex items-center gap-2 bg-cyan-800 hover:bg-cyan-900 text-white font-semibold text-sm px-4 py-2.5 rounded-xl shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4" />
                    <span>Nova cirurgia</span>
                </button>
            </div>

            <AvisoErro mensagem={erroAcao}/>

            <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
                <div className="p-5 border-b border-slate-100">
                    <h2 className="font-bold text-slate-800 text-base">Procedimentos cirúrgicos</h2>
                    <p className="text-xs text-slate-400 mt-0.5">{procedimentos.length} procedimentos encontrados</p>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full text-left text-xs">
                        <thead>
                            <tr className="text-slate-400 font-bold border-b border-slate-100 bg-slate-50/50">
                                <th className="py-3 px-5">DATA</th>
                                <th className="py-3 px-5">HORÁRIO</th>
                                <th className="py-3 px-5">PACIENTE</th>
                                <th className="py-3 px-5">CIRURGIÃO</th>
                                <th className="py-3 px-5">PROCEDIMENTO</th>
                                <th className="py-3 px-5">TIPO</th>
                                <th className="py-3 px-5">STATUS</th>
                                <th className="py-3 px-5 text-right">AÇÕES</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            <LinhaEstado
                                colunas={8}
                                carregando={carregando}
                                erro={erro}
                                quantidade={procedimentos.length}
                                vazio="Nenhuma cirurgia encontrada."/>
                            {procedimentos.map((procedimento) => (
                                <tr key={procedimento.id} className="hover:bg-slate-50/60 transition-colors">
                                    <td className="py-3.5 px-5 text-slate-800 font-bold">{formatarData(procedimento.dataPrevista)}</td>
                                    <td className="py-3.5 px-5 text-slate-800 font-bold">{formatarHora(procedimento.horarioPrevisto)}</td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-lg bg-teal-50 text-teal-800 font-bold text-xs flex items-center justify-center shrink-0">
                                                {getIniciais(procedimento.pacienteNome)}
                                            </div>
                                            <span className="font-bold text-slate-800 text-xs">{procedimento.pacienteNome}</span>
                                        </div>
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">{procedimento.cirurgiaoNome}</td>
                                    <td className="py-3.5 px-5 text-slate-600">{procedimento.descricao}</td>
                                    <td className="py-3.5 px-5 text-slate-600">{TIPOS_CIRURGIA[procedimento.tipoCirurgia] ?? procedimento.tipoCirurgia}</td>
                                    <td className="py-3.5 px-5">
                                        <BadgeStatus estilo={estiloStatusProcedimento(procedimento.status)} />
                                    </td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center justify-end gap-2 text-slate-400">
                                            {procedimento.status === 'AGENDADO' && (
                                                <>
                                                    <BotaoAcao titulo="Iniciar cirurgia" desabilitado={ocupado} onClick={() => executar(() => iniciarProcedimento(procedimento.id))}>
                                                        <Play className="w-4 h-4" />
                                                    </BotaoAcao>
                                                    <BotaoAcao titulo="Cancelar cirurgia" perigo desabilitado={ocupado} onClick={() => cancelar(procedimento)}>
                                                        <Ban className="w-4 h-4" />
                                                    </BotaoAcao>
                                                </>
                                            )}
                                            {procedimento.status === 'EM_ANDAMENTO' && (
                                                <BotaoAcao titulo="Concluir cirurgia" onClick={() => setConcluindo(procedimento)}>
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

            <ModalAgendarProcedimento isOpen={modalNovo} onClose={() => setModalNovo(false)} onSalvo={recarregar} />

            <ModalConcluirAtendimento
                key={`concluir-${concluindo?.id ?? 'nenhuma'}`}
                isOpen={concluindo !== null}
                onClose={() => setConcluindo(null)}
                onSalvo={recarregar}
                titulo="Concluir cirurgia"
                rotuloObservacoes="Observações pós-operatórias"
                nomePaciente={concluindo?.pacienteNome}
                concluir={(observacoes) => concluirProcedimento(concluindo.id, observacoes)}/>
        </div>
    )
}