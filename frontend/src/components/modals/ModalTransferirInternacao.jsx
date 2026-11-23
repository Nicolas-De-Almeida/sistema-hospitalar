import { useState } from 'react'
import { X, ArrowRightLeft } from 'lucide-react'

import { useDadosAoAbrir } from '../../hooks/useDadosAoAbrir'
import { listarQuartosDisponiveis, transferirInternacao } from '../../services/hospitalService'

// Classes repetidas nos campos (assim só mudamos o visual em um lugar)
const classeInput = 'w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors'
const classeLabel = 'block text-xs font-semibold text-slate-700 mb-1'

// Move o paciente de uma internação ativa para outro quarto com vaga.
export default function ModalTransferirInternacao({ isOpen, onClose, onSalvo, internacao }) {
    const [quartoId, setQuartoId] = useState('')
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')

    // Quartos com vaga, buscados toda vez que o modal abre
    const quartos = useDadosAoAbrir(isOpen, listarQuartosDisponiveis)

    if (!isOpen || !internacao) return null

    const destinos = quartos.dados.filter((q) => q.id !== internacao.quartoId)

    function fechar() {
        setQuartoId('')
        setErro('')
        onClose()
    }

    async function salvar(e) {
        e.preventDefault()
        setSalvando(true)
        setErro('')

        try {
            await transferirInternacao(internacao.id, Number(quartoId))
            if (onSalvo) await onSalvo()
            fechar()
        } catch (e) {
            // Mostra a mensagem do back end ("O quarto está lotado")
            setErro(e.message)
        } finally {
            setSalvando(false)
        }
    }

    return (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
                <div className="p-6 pb-2">
                    <div className="flex items-start justify-between mb-3">
                        <div className="bg-teal-50 text-teal-800 p-2.5 rounded-xl border border-teal-100">
                            <ArrowRightLeft className="w-6 h-6"/>
                        </div>
                        <button type="button" onClick={fechar} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                            <X className="w-5 h-5"/>
                        </button>
                    </div>
                    <h2 className="text-xl font-bold text-slate-800">Mover de quarto</h2>
                    <p className="text-xs text-slate-400 mt-1">Escolha o novo quarto do paciente.</p>
                </div>

                <form onSubmit={salvar} className="p-6 pt-4 space-y-4 overflow-y-auto">
                    <div className="p-3 bg-slate-50 border border-slate-100 rounded-xl space-y-0.5">
                        <p className="text-xs text-slate-400">Paciente</p>
                        <p className="text-sm font-bold text-slate-800">{internacao.pacienteNome}</p>
                        <p className="text-xs text-slate-500">Está agora no quarto {internacao.quartoNumero}</p>
                    </div>

                    <div>
                        <label className={classeLabel}>Novo quarto <span className="text-red-500">*</span></label>
                        <select required value={quartoId} onChange={(e) => setQuartoId(e.target.value)} className={classeInput}>
                            <option value="">{quartos.carregando ? 'Carregando...' : 'Selecione...'}</option>
                            {destinos.map((q) => (
                                <option key={q.id} value={q.id}>
                                    Quarto {q.numero} · {q.andar}º andar · {q.vagasDisponiveis} {q.vagasDisponiveis === 1 ? 'vaga' : 'vagas'}
                                </option>
                            ))}
                        </select>
                        {quartos.erro && <p className="text-xs text-red-600 mt-1">{quartos.erro}</p>}
                        {!quartos.carregando && !quartos.erro && destinos.length === 0 && (
                            <p className="text-xs text-slate-400 mt-1">Nenhum outro quarto com vaga no momento.</p>
                        )}
                    </div>

                    {erro && (
                        <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                            {erro}
                        </div>
                    )}

                    <div className="flex items-center justify-between pt-2">
                        <button type="button" onClick={fechar} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 text-sm font-semibold transition-colors cursor-pointer">Cancelar</button>
                        <button type="submit" disabled={salvando} className="px-5 py-2 bg-cyan-800 hover:bg-cyan-900 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm disabled:opacity-60 disabled:cursor-not-allowed">
                            {salvando ? 'Movendo...' : 'Mover'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}