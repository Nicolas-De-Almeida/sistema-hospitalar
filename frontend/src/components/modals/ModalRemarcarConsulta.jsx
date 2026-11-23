import { useState } from 'react'
import { X, CalendarClock } from 'lucide-react'

import { remarcarConsulta } from '../../services/hospitalService'
import { dataHojeISO, formatarData, formatarHora } from '../../utils/rotulos'

// Classes repetidas em todos os campos (assim só mudamos o visual em um lugar)
const classeInput = 'w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors'
const classeLabel = 'block text-xs font-semibold text-slate-700 mb-1'

// Troca a data e o horário de uma consulta agendada ou confirmada.
// O back end confere se o médico atende e está livre no novo horário.
export default function ModalRemarcarConsulta({ isOpen, onClose, onSalvo, consulta }) {
    const [form, setForm] = useState(() => ({
        data: consulta?.data ?? '',
        horario: consulta ? formatarHora(consulta.horario) : '',
    }))
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')

    if (!isOpen || !consulta) return null

    const atualizar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

    function fechar() {
        setErro('')
        onClose()
    }

    async function salvar(e) {
        e.preventDefault()
        setSalvando(true)
        setErro('')

        try {
            await remarcarConsulta(consulta.id, { data: form.data, horario: form.horario })
            if (onSalvo) await onSalvo()
            fechar()
        } catch (e) {
            // Mostra a mensagem("O profissional já tem consulta neste horário")
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
                            <CalendarClock className="w-6 h-6"/>
                        </div>
                        <button type="button" onClick={fechar} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                            <X className="w-5 h-5"/>
                        </button>
                    </div>
                    <h2 className="text-xl font-bold text-slate-800">Remarcar consulta</h2>
                    <p className="text-xs text-slate-400 mt-1">Escolha a nova data e o novo horário.</p>
                </div>

                <form onSubmit={salvar} className="p-6 pt-4 space-y-4 overflow-y-auto">
                    <div className="p-3 bg-slate-50 border border-slate-100 rounded-xl space-y-0.5">
                        <p className="text-xs text-slate-400">Paciente</p>
                        <p className="text-sm font-bold text-slate-800">{consulta.pacienteNome}</p>
                        <p className="text-xs text-slate-500">
                            {consulta.profissionalNome} · hoje marcada para {formatarData(consulta.data)} às {formatarHora(consulta.horario)}
                        </p>
                    </div>

                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={classeLabel}>Nova data <span className="text-red-500">*</span></label>
                            <input type="date" required min={dataHojeISO()} value={form.data} onChange={atualizar('data')} className={classeInput}/>
                        </div>
                        <div>
                            <label className={classeLabel}>Novo horário <span className="text-red-500">*</span></label>
                            <input type="time" required value={form.horario} onChange={atualizar('horario')} className={classeInput}/>
                        </div>
                    </div>

                    {erro && (
                        <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                            {erro}
                        </div>
                    )}

                    <div className="flex items-center justify-between pt-2">
                        <button type="button" onClick={fechar} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 text-sm font-semibold transition-colors cursor-pointer">Cancelar</button>
                        <button type="submit" disabled={salvando} className="px-5 py-2 bg-cyan-800 hover:bg-cyan-900 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm disabled:opacity-60 disabled:cursor-not-allowed">
                            {salvando ? 'Remarcando...' : 'Remarcar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}