import { useState } from 'react'
import { X, Play } from 'lucide-react'

import { useDadosAoAbrir } from '../../hooks/useDadosAoAbrir'
import { iniciarEmergencia, listarProfissionais } from '../../services/hospitalService'

const classeInput = 'w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors placeholder:text-slate-300 disabled:bg-slate-50 disabled:text-slate-400'
const classeLabel = 'block text-xs font-semibold text-slate-700 mb-1'

export default function ModalIniciarEmergencia({ isOpen, onClose, onSalvo, emergencia }) {
    const [profissionalId, setProfissionalId] = useState('')
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')

    const profissionais = useDadosAoAbrir(isOpen, listarProfissionais)

    if (!isOpen || !emergencia) return null

    function fechar() {
        setProfissionalId('')
        setErro('')
        onClose()
    }

    async function salvar(e) {
        e.preventDefault()
        setSalvando(true)
        setErro('')
        try {
            await iniciarEmergencia(emergencia.id, Number(profissionalId))
            if (onSalvo) await onSalvo()
            fechar()
        } catch (e) {
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
                        <div className="bg-blue-50 text-blue-700 p-2.5 rounded-xl border border-blue-100">
                            <Play className="w-6 h-6"/>
                        </div>
                        <button type="button" onClick={fechar} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                            <X className="w-5 h-5"/>
                        </button>
                    </div>
                    <h2 className="text-xl font-bold text-slate-800">Iniciar avaliação</h2>
                    <p className="text-xs text-slate-400 mt-1">Paciente: {emergencia.pacienteNome}</p>
                </div>

                <form onSubmit={salvar} className="p-6 pt-4 space-y-4 overflow-y-auto">
                    <div>
                        <label className={classeLabel}>Profissional responsável <span className="text-red-500">*</span></label>
                        <select required value={profissionalId} onChange={(e) => setProfissionalId(e.target.value)} className={classeInput}>
                            <option value="">{profissionais.carregando ? 'Carregando...' : 'Selecione...'}</option>
                            {profissionais.dados.filter((p) => p.ativo).map((p) => (
                                <option key={p.id} value={p.id}>{p.nome} · {p.registroProfissional}</option>
                            ))}
                        </select>
                        {profissionais.erro && <p className="text-xs text-red-600 mt-1">{profissionais.erro}</p>}
                    </div>

                    {erro && (
                        <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                            {erro}
                        </div>
                    )}

                    <div className="flex items-center justify-between pt-2">
                        <button type="button" onClick={fechar} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 text-sm font-semibold transition-colors cursor-pointer">Cancelar</button>
                        <button type="submit" disabled={salvando} className="px-5 py-2 bg-cyan-800 hover:bg-cyan-900 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm disabled:opacity-60 disabled:cursor-not-allowed">
                            {salvando ? 'Iniciando...' : 'Iniciar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}