import { useCallback } from 'react'
import { X, History } from 'lucide-react'

import AvisoErro from '../AvisoErro'
import { useDadosAoAbrir } from '../../hooks/useDadosAoAbrir'
import { listarTransferenciasInternacao } from '../../services/hospitalService'
import { formatarData, formatarDataHora } from '../../utils/rotulos'

// Mostra por quais quartos o paciente passou durante uma internação.
export default function ModalHistoricoQuartos({ isOpen, onClose, internacao }) {
    const idInternacao = internacao?.id

    // Função estável(o hook só busca de novo se o id mudar)
    const buscar = useCallback(() => listarTransferenciasInternacao(idInternacao), [idInternacao])
    const { dados: trocas, carregando, erro } = useDadosAoAbrir(isOpen && idInternacao != null, buscar)

    if (!isOpen || !internacao) return null

    // O primeiro quarto é o de origem (ou o quarto atual, se nunca trocou)
    const quartoInicial = trocas.length > 0 ? trocas[0].quartoOrigemNumero : internacao.quartoNumero

    return (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
                <div className="p-6 pb-2">
                    <div className="flex items-start justify-between mb-3">
                        <div className="bg-teal-50 text-teal-800 p-2.5 rounded-xl border border-teal-100">
                            <History className="w-6 h-6"/>
                        </div>
                        <button type="button" onClick={onClose} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                            <X className="w-5 h-5"/>
                        </button>
                    </div>
                    <h2 className="text-xl font-bold text-slate-800">Histórico de quartos</h2>
                    <p className="text-xs text-slate-400 mt-1">{internacao.pacienteNome}</p>
                </div>

                <div className="p-6 pt-4 space-y-3 overflow-y-auto">
                    <AvisoErro mensagem={erro} />

                    {carregando ? (
                        <p className="text-sm text-slate-400">Carregando...</p>
                    ) : !erro && (
                        <ol className="border-l-2 border-slate-100 ml-2 space-y-4">
                            <li className="pl-4">
                                <p className="text-sm font-bold text-slate-800">Quarto {quartoInicial}</p>
                                <p className="text-xs text-slate-500">Entrada em {formatarData(internacao.dataEntrada)}</p>
                            </li>
                            {trocas.map((t) => (
                                <li key={t.id} className="pl-4">
                                    <p className="text-sm font-bold text-slate-800">
                                        Quarto {t.quartoOrigemNumero} → Quarto {t.quartoDestinoNumero}
                                    </p>
                                    <p className="text-xs text-slate-500">{formatarDataHora(t.dataHora)}</p>
                                </li>
                            ))}
                        </ol>
                    )}

                    {!carregando && !erro && trocas.length === 0 && (
                        <p className="text-xs text-slate-400">Este paciente nunca mudou de quarto nesta internação.</p>
                    )}

                    <div className="flex justify-end pt-1">
                        <button type="button" onClick={onClose} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 text-sm font-semibold transition-colors cursor-pointer">Fechar</button>
                    </div>
                </div>
            </div>
        </div>
    )
}