import { useState } from 'react'
import { X, Scissors } from 'lucide-react'

import { useDadosAoAbrir } from '../../hooks/useDadosAoAbrir'
import { agendarProcedimento, listarPacientes, listarProfissionais } from '../../services/hospitalService'
import { dataHojeISO, formatarCpf, TIPOS_CIRURGIA } from '../../utils/rotulos'

const classeInput = 'w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors placeholder:text-slate-300 disabled:bg-slate-50 disabled:text-slate-400'
const classeLabel = 'block text-xs font-semibold text-slate-700 mb-1'

const FORMULARIO_VAZIO = {
    pacienteId: '',
    cirurgiaoId: '',
    dataPrevista: '',
    horarioPrevisto: '',
    tipoCirurgia: '',
    descricao: '',
    observacoesPreOperatorias: '',
}

export default function ModalAgendarProcedimento({ isOpen, onClose, onSalvo }) {
    const [form, setForm] = useState(FORMULARIO_VAZIO)
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')

    const pacientes = useDadosAoAbrir(isOpen, listarPacientes)
    const profissionais = useDadosAoAbrir(isOpen, listarProfissionais)

    if (!isOpen) return null

    // Só cirurgiões ativos podem ser responsáveis por um procedimento
    const cirurgioes = profissionais.dados.filter((p) => p.categoria === 'CIRURGIAO' && p.ativo)

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
            await agendarProcedimento({
                pacienteId: Number(form.pacienteId),
                cirurgiaoId: Number(form.cirurgiaoId),
                dataPrevista: form.dataPrevista,
                horarioPrevisto: form.horarioPrevisto,
                tipoCirurgia: form.tipoCirurgia,
                descricao: form.descricao.trim(),
                observacoesPreOperatorias: form.observacoesPreOperatorias.trim() || null,
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
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
                <div className="p-6 pb-2">
                    <div className="flex items-start justify-between mb-3">
                        <div className="bg-teal-50 text-teal-800 p-2.5 rounded-xl border border-teal-100">
                            <Scissors className="w-6 h-6"/>
                        </div>
                        <button type="button" onClick={fechar} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                            <X className="w-5 h-5"/>
                        </button>
                    </div>
                    <h2 className="text-xl font-bold text-slate-800">Agendar cirurgia</h2>
                    <p className="text-xs text-slate-400 mt-1">Preencha as informações do procedimento.</p>
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
                    </div>

                    <div>
                        <label className={classeLabel}>Cirurgião <span className="text-red-500">*</span></label>
                        <select required value={form.cirurgiaoId} onChange={atualizar('cirurgiaoId')} className={classeInput}>
                            <option value="">{profissionais.carregando ? 'Carregando...' : 'Selecione...'}</option>
                            {cirurgioes.map((p) => (
                                <option key={p.id} value={p.id}>{p.nome} · {p.areaCirurgica ?? p.registroProfissional}</option>
                            ))}
                        </select>
                        {profissionais.erro && <p className="text-xs text-red-600 mt-1">{profissionais.erro}</p>}
                    </div>

                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={classeLabel}>Data prevista <span className="text-red-500">*</span></label>
                            <input type="date" required min={dataHojeISO()} value={form.dataPrevista} onChange={atualizar('dataPrevista')} className={classeInput}/>
                        </div>
                        <div>
                            <label className={classeLabel}>Horário <span className="text-red-500">*</span></label>
                            <input type="time" required step={1800} value={form.horarioPrevisto} onChange={atualizar('horarioPrevisto')} className={classeInput}/>
                        </div>
                    </div>

                    <div>
                        <label className={classeLabel}>Tipo de cirurgia <span className="text-red-500">*</span></label>
                        <select required value={form.tipoCirurgia} onChange={atualizar('tipoCirurgia')} className={classeInput}>
                            <option value="">Selecione...</option>
                            {Object.entries(TIPOS_CIRURGIA).map(([valor, texto]) => (
                                <option key={valor} value={valor}>{texto}</option>
                            ))}
                        </select>
                    </div>

                    <div>
                        <label className={classeLabel}>Descrição do procedimento <span className="text-red-500">*</span></label>
                        <input type="text" required maxLength={1000} value={form.descricao} onChange={atualizar('descricao')} placeholder="Ex.: Apendicectomia" className={classeInput}/>
                    </div>

                    <div>
                        <label className={classeLabel}>Observações pré-operatórias (opcional)</label>
                        <textarea rows={3} maxLength={2000} value={form.observacoesPreOperatorias} onChange={atualizar('observacoesPreOperatorias')} className={classeInput}/>
                    </div>

                    {erro && (
                        <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                            {erro}
                        </div>
                    )}

                    <div className="flex items-center justify-between pt-2">
                        <button type="button" onClick={fechar} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 text-sm font-semibold transition-colors cursor-pointer">Cancelar</button>
                        <button type="submit" disabled={salvando} className="px-5 py-2 bg-cyan-800 hover:bg-cyan-900 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm disabled:opacity-60 disabled:cursor-not-allowed">
                            {salvando ? 'Agendando...' : 'Agendar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}
