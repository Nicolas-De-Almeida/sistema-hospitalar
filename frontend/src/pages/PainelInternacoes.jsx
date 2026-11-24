import { useState } from 'react'
import { Plus, Pencil, LogOut, ArrowRightLeft, History } from 'lucide-react'
import ModalNovaInternacao from '../components/modals/ModalNovaInternacao'
import ModalEditarInternacao from '../components/modals/ModalEditarInternacao'
import ModalTransferirInternacao from '../components/modals/ModalTransferirInternacao'
import ModalHistoricoQuartos from '../components/modals/ModalHistoricoQuartos'

import LinhaEstado from '../components/LinhaEstado'
import BadgeStatus from '../components/BadgeStatus'
import BotaoAcao from '../components/BotaoAcao'
import AvisoErro from '../components/AvisoErro'

import { useDados } from '../hooks/useDados'
import { useAcao } from '../hooks/useAcao'
import { listarInternacoes, darAltaInternacao } from '../services/hospitalService'
import { getIniciais } from '../utils/primeiraLetraMaiuscula'
import { formatarData, dataHojeISO, estiloStatusInternacao } from '../utils/rotulos'

export default function GestaoInternacoes() {
    const [modalNovaInternacao, setModalNovaInternacao] = useState(false)

    // Internação que está sendo editada (null = modal fechado)
    const [internacaoEditando, setInternacaoEditando] = useState(null)

    // Internação cujo paciente está sendo movido de quarto (null = modal fechado)
    const [internacaoMovendo, setInternacaoMovendo] = useState(null)

    // Internação cujo histórico de quartos está aberto (null = modal fechado)
    const [internacaoHistorico, setInternacaoHistorico] = useState(null)

    // Lista de internações
    const { dados: internacoes, carregando, erro, recarregar } = useDados(listarInternacoes)
    const { executar, ocupado, erroAcao } = useAcao(recarregar)

    // Dá alta com a data de hoje
    function darAlta(internacao) {
        if (window.confirm(`Dar alta para ${internacao.pacienteNome} hoje?`)) {
            executar(() => darAltaInternacao(internacao.id, dataHojeISO()))
        }
    }

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <div>
                    <span className="font-bold tracking-wider text-cyan-800 uppercase">GESTÃO DE INTERNAÇÕES</span>
                </div>

                <button onClick={() => setModalNovaInternacao(true)} className="flex items-center gap-2 bg-cyan-800 hover:bg-cyan-900 text-white font-semibold text-sm px-4 py-2.5 rounded-xl shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4"/>
                    <span>Nova internação</span>
                </button>
            </div>

            <AvisoErro mensagem={erroAcao}/>

            <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
                <div className="p-5 border-b border-slate-100">
                    <h2 className="font-bold text-slate-800 text-base">Pacientes internados</h2>
                    <p className="text-xs text-slate-400 mt-0.5">{internacoes.length} internações encontradas</p>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full text-left text-xs">
                        <thead>
                            <tr className="text-slate-400 font-bold border-b border-slate-100 bg-slate-50/50">
                                <th className="py-3 px-5">PACIENTE</th>
                                <th className="py-3 px-5">PROFISSIONAL RESPONSÁVEL</th>
                                <th className="py-3 px-5">QUARTO</th>
                                <th className="py-3 px-5">ENTRADA</th>
                                <th className="py-3 px-5">ALTA PREVISTA</th>
                                <th className="py-3 px-5">STATUS</th>
                                <th className="py-3 px-5 text-right">AÇÕES</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            <LinhaEstado
                                colunas={7}
                                carregando={carregando}
                                erro={erro}
                                quantidade={internacoes.length}
                                vazio="Nenhuma internação encontrada."/>
                            {internacoes.map((internacao) => (
                                <tr key={internacao.id} className="hover:bg-slate-50/60 transition-colors">
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-lg bg-teal-50 text-teal-800 font-bold text-xs flex items-center justify-center shrink-0">
                                                {getIniciais(internacao.pacienteNome)}
                                            </div>
                                            <span className="font-bold text-slate-800 text-xs">
                                                {internacao.pacienteNome}
                                            </span>
                                        </div>
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">{internacao.profissionalNome}</td>
                                    <td className="py-3.5 px-5 text-slate-800 font-bold">{internacao.quartoNumero}</td>
                                    <td className="py-3.5 px-5 text-slate-600">{formatarData(internacao.dataEntrada)}</td>
                                    <td className="py-3.5 px-5 text-slate-600">{formatarData(internacao.dataPrevistaAlta)}</td>
                                    <td className="py-3.5 px-5">
                                        <BadgeStatus estilo={estiloStatusInternacao(internacao.status)} />
                                    </td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center justify-end gap-2 text-slate-400">
                                            {/* Só dá para editar, mover e dar alta em internação ativa */}
                                            <BotaoAcao titulo="Histórico de quartos" onClick={() => setInternacaoHistorico(internacao)}>
                                                <History className="w-4 h-4" />
                                            </BotaoAcao>
                                            {internacao.status === 'ATIVA' && (
                                                <>
                                                    <BotaoAcao titulo="Editar internação" onClick={() => setInternacaoEditando(internacao)}>
                                                        <Pencil className="w-4 h-4" />
                                                    </BotaoAcao>
                                                    <BotaoAcao titulo="Mover de quarto" onClick={() => setInternacaoMovendo(internacao)}>
                                                        <ArrowRightLeft className="w-4 h-4" />
                                                    </BotaoAcao>
                                                    <BotaoAcao titulo="Dar alta" desabilitado={ocupado} onClick={() => darAlta(internacao)}>
                                                        <LogOut className="w-4 h-4" />
                                                    </BotaoAcao>
                                                </>
                                            )}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>

            <ModalNovaInternacao isOpen={modalNovaInternacao} onClose={() => setModalNovaInternacao(false)} onSalvo={recarregar} />

            <ModalEditarInternacao
                key={`editar-${internacaoEditando?.id ?? 'nenhuma'}`}
                isOpen={internacaoEditando !== null}
                onClose={() => setInternacaoEditando(null)}
                onSalvo={recarregar}
                internacao={internacaoEditando}/>

            <ModalTransferirInternacao
                key={`mover-${internacaoMovendo?.id ?? 'nenhuma'}`}
                isOpen={internacaoMovendo !== null}
                onClose={() => setInternacaoMovendo(null)}
                onSalvo={recarregar}
                internacao={internacaoMovendo}/>
            
            <ModalHistoricoQuartos
                key={`historico-${internacaoHistorico?.id ?? 'nenhuma'}`}
                isOpen={internacaoHistorico !== null}
                onClose={() => setInternacaoHistorico(null)}
                internacao={internacaoHistorico}/>
        </div>
    )
}