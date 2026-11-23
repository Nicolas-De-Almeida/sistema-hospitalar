import { useState } from 'react'
import { X, Users, ArrowRightLeft, LogOut } from 'lucide-react'

import ModalTransferirInternacao from './ModalTransferirInternacao'
import AvisoErro from '../AvisoErro'
import { useDadosAoAbrir } from '../../hooks/useDadosAoAbrir'
import { darAltaInternacao, listarInternacoes } from '../../services/hospitalService'
import { dataHojeISO, formatarData } from '../../utils/rotulos'

// Mostra quem está internado em um quarto e permite mover ou dar alta daqui mesmo.
// "quarto" vem da lista atualizada da tela (assim a ocupação aparece sempre correta).
export default function ModalPacientesQuarto({ isOpen, onClose, onAlterado, quarto }) {
    const internacoes = useDadosAoAbrir(isOpen, listarInternacoes)
    const [internacaoMovendo, setInternacaoMovendo] = useState(null)
    const [ocupado, setOcupado] = useState(false)
    const [erro, setErro] = useState('')

    if (!isOpen || !quarto) return null

    // Só quem está internado AGORA neste quarto (alta encerra a internação e libera a vaga)
    const pacientes = internacoes.dados.filter((i) => i.quartoId === quarto.id && i.status === 'ATIVA')

    // Atualiza a lista do modal e avisa a tela
    async function atualizarTudo() {
        internacoes.recarregar()
        if (onAlterado) await onAlterado()
    }

    async function darAlta(internacao) {
        if (!window.confirm(`Dar alta para ${internacao.pacienteNome} hoje?`)) return

        setOcupado(true)
        setErro('')
        try {
            await darAltaInternacao(internacao.id, dataHojeISO())
            await atualizarTudo()
        } catch (e) {
            setErro(e.message)
        } finally {
            setOcupado(false)
        }
    }

    function fechar() {
        setErro('')
        setInternacaoMovendo(null)
        onClose()
    }

    return (
        <>
            <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
                <div className="bg-white rounded-2xl w-full max-w-lg shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
                    <div className="p-6 pb-2">
                        <div className="flex items-start justify-between mb-3">
                            <div className="bg-teal-50 text-teal-800 p-2.5 rounded-xl border border-teal-100">
                                <Users className="w-6 h-6"/>
                            </div>
                            <button type="button" onClick={fechar} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                                <X className="w-5 h-5"/>
                            </button>
                        </div>
                        <h2 className="text-xl font-bold text-slate-800">Pacientes do quarto {quarto.numero}</h2>
                        <p className="text-xs text-slate-400 mt-1">
                            {quarto.ocupacaoAtual} de {quarto.capacidadeMaxima} {quarto.capacidadeMaxima === 1 ? 'leito ocupado' : 'leitos ocupados'}
                        </p>
                    </div>

                    <div className="p-6 pt-4 space-y-3 overflow-y-auto">
                        <AvisoErro mensagem={erro || internacoes.erro} />

                        {internacoes.carregando && pacientes.length === 0 ? (
                            <p className="text-sm text-slate-400">Carregando...</p>
                        ) : pacientes.length === 0 ? (
                            <p className="text-sm text-slate-400">Nenhum paciente internado neste quarto.</p>
                        ) : (
                            <ul className="divide-y divide-slate-100 border border-slate-100 rounded-xl">
                                {pacientes.map((i) => (
                                    <li key={i.id} className="flex items-center justify-between gap-3 px-3 py-2.5">
                                        <div>
                                            <p className="text-sm font-bold text-slate-800">{i.pacienteNome}</p>
                                            <p className="text-xs text-slate-500">
                                                {i.profissionalNome} · entrada {formatarData(i.dataEntrada)} · alta prevista {formatarData(i.dataPrevistaAlta)}
                                            </p>
                                        </div>
                                        <div className="flex items-center gap-1 text-slate-400">
                                            <button type="button" title="Mover de quarto" onClick={() => setInternacaoMovendo(i)} className="hover:text-slate-700 p-1 transition-colors cursor-pointer">
                                                <ArrowRightLeft className="w-4 h-4"/>
                                            </button>
                                            <button type="button" title="Dar alta" disabled={ocupado} onClick={() => darAlta(i)} className="hover:text-slate-700 p-1 transition-colors cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed">
                                                <LogOut className="w-4 h-4"/>
                                            </button>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        )}

                        <p className="text-xs text-slate-400">
                            Para internar um novo paciente neste quarto, use "Nova internação" na tela de Internações.
                        </p>

                        <div className="flex justify-end pt-1">
                            <button type="button" onClick={fechar} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 text-sm font-semibold transition-colors cursor-pointer">Fechar</button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Fica depois do modal acima, então aparece por cima dele */}
            <ModalTransferirInternacao
                key={internacaoMovendo?.id ?? 'nenhuma'}
                isOpen={internacaoMovendo !== null}
                onClose={() => setInternacaoMovendo(null)}
                onSalvo={atualizarTudo}
                internacao={internacaoMovendo}/>
        </>
    )
}