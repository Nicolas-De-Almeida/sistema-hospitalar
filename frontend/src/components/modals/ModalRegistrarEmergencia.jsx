import { useState } from 'react'
import { X, Siren, UserPlus } from 'lucide-react'

import ModalPaciente from './ModalPaciente'

import { useDadosAoAbrir } from '../../hooks/useDadosAoAbrir'
import { registrarEmergencia, listarPacientes } from '../../services/hospitalService'
import { formatarCpf, PRIORIDADES_EMERGENCIA } from '../../utils/rotulos'

const classeInput = 'w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors placeholder:text-slate-300 disabled:bg-slate-50 disabled:text-slate-400'
const classeLabel = 'block text-xs font-semibold text-slate-700 mb-1'

const FORMULARIO_VAZIO = { pacienteId: '', prioridade: '', queixaPrincipal: '' }

export default function ModalRegistrarEmergencia({ isOpen, onClose, onSalvo }) {
    const [form, setForm] = useState(FORMULARIO_VAZIO)
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')
    const [cadastrandoPaciente, setCadastrandoPaciente] = useState(false)

    const pacientes = useDadosAoAbrir(isOpen, listarPacientes)

    if (!isOpen) return null

    const atualizar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

    function fechar() {
        setForm(FORMULARIO_VAZIO)
        setErro('')
        onClose()
    }

    async function salvar(e) {
        e.preventDefault()
        setSalvando(true)
        setErro('')
        try {
            await registrarEmergencia({
                pacienteId: Number(form.pacienteId),
                prioridade: form.prioridade,
                queixaPrincipal: form.queixaPrincipal.trim(),
            })
            if (onSalvo) await onSalvo()
            fechar()
        } catch (e) {
            setErro(e.message)
        } finally {
            setSalvando(false)
        }
    }

    return (
        <>
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
                <div className="p-6 pb-2">
                    <div className="flex items-start justify-between mb-3">
                        <div className="bg-red-50 text-red-700 p-2.5 rounded-xl border border-red-100">
                            <Siren className="w-6 h-6"/>
                        </div>
                        <button type="button" onClick={fechar} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                            <X className="w-5 h-5"/>
                        </button>
                    </div>
                    <h2 className="text-xl font-bold text-slate-800">Registrar emergência</h2>
                    <p className="text-xs text-slate-400 mt-1">O paciente entra na fila, ordenada pela prioridade.</p>
                </div>

                <form onSubmit={salvar} className="p-6 pt-4 space-y-4 overflow-y-auto">
                    <div>
                        <label className={classeLabel}>Paciente <span className="text-red-500">*</span></label>
                        <select required value={form.pacienteId} onChange={atualizar('pacienteId')} className={classeInput}>
                            <option value="">{pacientes.carregando ? 'Carregando...' : 'Selecione...'}</option>
                            {pacientes.dados.map((p) => (
                                <option key={p.id} value={p.id}>{p.nome} · {formatarCpf(p.cpf)}</option>
                            ))}
                        </select>
                        {pacientes.erro && <p className="text-xs text-red-600 mt-1">{pacientes.erro}</p>}
                        <button type="button" onClick={() => setCadastrandoPaciente(true)} className="mt-2 flex items-center gap-1.5 text-xs font-semibold text-cyan-800 hover:text-cyan-900 cursor-pointer">
                            <UserPlus className="w-3.5 h-3.5" />
                            Paciente não cadastrado? Cadastrar agora
                        </button>
                    </div>

                    <div>
                        <label className={classeLabel}>Prioridade <span className="text-red-500">*</span></label>
                        <select required value={form.prioridade} onChange={atualizar('prioridade')} className={classeInput}>
                            <option value="">Selecione...</option>
                            {Object.entries(PRIORIDADES_EMERGENCIA).map(([valor, { texto }]) => (
                                <option key={valor} value={valor}>{texto}</option>
                            ))}
                        </select>
                    </div>

                    <div>
                        <label className={classeLabel}>Queixa principal <span className="text-red-500">*</span></label>
                        <textarea required rows={3} maxLength={500} value={form.queixaPrincipal} onChange={atualizar('queixaPrincipal')} placeholder="Ex.: Dor intensa no peito" className={classeInput}/>
                    </div>

                    {erro && (
                        <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                            {erro}
                        </div>
                    )}

                    <div className="flex items-center justify-between pt-2">
                        <button type="button" onClick={fechar} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 text-sm font-semibold transition-colors cursor-pointer">Cancelar</button>
                        <button type="submit" disabled={salvando} className="px-5 py-2 bg-cyan-800 hover:bg-cyan-900 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm disabled:opacity-60 disabled:cursor-not-allowed">
                            {salvando ? 'Registrando...' : 'Registrar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <ModalPaciente
            key="cadastro-na-emergencia"
            isOpen={cadastrandoPaciente}
            onClose={() => setCadastrandoPaciente(false)}
            onSalvo={pacientes.recarregar}/>
        </>
    )
}