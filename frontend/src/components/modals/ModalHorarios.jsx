import { useState } from 'react'
import { X, Clock, Trash2 } from 'lucide-react'

import { adicionarHorario, removerHorario } from '../../services/hospitalService'
import { formatarHora } from '../../utils/rotulos'

// Classes repetidas nos campos (assim só mudamos o visual em um lugar)
const classeInput = 'w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors'
const classeLabel = 'block text-xs font-semibold text-slate-700 mb-1'

// Aordem é a da semana
const DIAS_SEMANA = [
    ['MONDAY', 'Segunda-feira'],
    ['TUESDAY', 'Terça-feira'],
    ['WEDNESDAY', 'Quarta-feira'],
    ['THURSDAY', 'Quinta-feira'],
    ['FRIDAY', 'Sexta-feira'],
    ['SATURDAY', 'Sábado'],
    ['SUNDAY', 'Domingo'],
]
const NOME_DIA = Object.fromEntries(DIAS_SEMANA)
const ORDEM_DIA = Object.fromEntries(DIAS_SEMANA.map(([valor], indice) => [valor, indice]))

const FORMULARIO_VAZIO = { diaSemana: '', horaInicio: '', horaFim: '', duracaoConsultaMin: '30' }

// Mostra os horários de atendimento do profissional e permite adicionar ou remover.
export default function ModalHorarios({ isOpen, onClose, onSalvo, profissional }) {
    const [form, setForm] = useState(FORMULARIO_VAZIO)
    const [ocupado, setOcupado] = useState(false)
    const [erro, setErro] = useState('')

    if (!isOpen || !profissional) return null

    // Atualiza um campo: atualizar('horaInicio') devolve a função que o onChange usa
    const atualizar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

    // Segunda a domingo, e por horário de início dentro do mesmo dia
    const horarios = [...(profissional.disponibilidades ?? [])].sort((a, b) =>
        ORDEM_DIA[a.diaSemana] - ORDEM_DIA[b.diaSemana] || a.horaInicio.localeCompare(b.horaInicio)
    )

    function fechar() {
        setForm(FORMULARIO_VAZIO)
        setErro('')
        onClose()
    }

    // Roda uma ação (adicionar/remover), recarrega a lista e mostra o erro do back end, se houver
    async function executar(acao) {
        setOcupado(true)
        setErro('')
        try {
            await acao()
            if (onSalvo) await onSalvo()
            return true
        } catch (e) {
            setErro(e.message)
            return false
        } finally {
            setOcupado(false)
        }
    }

    async function adicionar(e) {
        e.preventDefault()

        const deuCerto = await executar(() => adicionarHorario(profissional.id, {
            diaSemana: form.diaSemana,
            horaInicio: form.horaInicio,
            horaFim: form.horaFim,
            duracaoConsultaMin: Number(form.duracaoConsultaMin),
        }))

        // Só limpa o formulário se salvou; se deu erro, o usuário corrige sem redigitar
        if (deuCerto) setForm(FORMULARIO_VAZIO)
    }

    function remover(horario) {
        const texto = `${NOME_DIA[horario.diaSemana]}, ${formatarHora(horario.horaInicio)} às ${formatarHora(horario.horaFim)}`
        if (window.confirm(`Remover o horário de ${texto}?`)) {
            executar(() => removerHorario(profissional.id, horario.id))
        }
    }

    return (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl w-full max-w-lg shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
                <div className="p-6 pb-2">
                    <div className="flex items-start justify-between mb-3">
                        <div className="bg-teal-50 text-teal-800 p-2.5 rounded-xl border border-teal-100">
                            <Clock className="w-6 h-6"/>
                        </div>
                        <button type="button" onClick={fechar} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                            <X className="w-5 h-5"/>
                        </button>
                    </div>
                    <h2 className="text-xl font-bold text-slate-800">Horários de atendimento</h2>
                    <p className="text-xs text-slate-400 mt-1">{profissional.nome}</p>
                </div>

                <div className="p-6 pt-4 space-y-5 overflow-y-auto">

                    {/* Horários já cadastrados */}
                    <div>
                        <h3 className="text-xs font-bold tracking-wider text-slate-400 uppercase mb-2">Cadastrados</h3>
                        {horarios.length === 0 ? (
                            <p className="text-sm text-slate-400">Nenhum horário cadastrado. Sem horário, o profissional não aparece como disponível para consultas.</p>
                        ) : (
                            <ul className="divide-y divide-slate-100 border border-slate-100 rounded-xl">
                                {horarios.map((h) => (
                                    <li key={h.id} className="flex items-center justify-between gap-3 px-3 py-2.5">
                                        <div>
                                            <p className="text-sm font-semibold text-slate-800">{NOME_DIA[h.diaSemana]}</p>
                                            <p className="text-xs text-slate-500">
                                                {formatarHora(h.horaInicio)} às {formatarHora(h.horaFim)} · consultas de {h.duracaoConsultaMin} min
                                            </p>
                                        </div>
                                        <button
                                            type="button"
                                            title="Remover horário"
                                            disabled={ocupado}
                                            onClick={() => remover(h)}
                                            className="text-slate-400 hover:text-red-600 p-1 transition-colors cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed"
                                        >
                                            <Trash2 className="w-4 h-4"/>
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>

                    {/* Formulário para adicionar um horário */}
                    <form onSubmit={adicionar} className="space-y-3">
                        <h3 className="text-xs font-bold tracking-wider text-slate-400 uppercase">Adicionar horário</h3>

                        <div>
                            <label className={classeLabel}>Dia da semana <span className="text-red-500">*</span></label>
                            <select required value={form.diaSemana} onChange={atualizar('diaSemana')} className={classeInput}>
                                <option value="">Selecione...</option>
                                {DIAS_SEMANA.map(([valor, texto]) => (
                                    <option key={valor} value={valor}>{texto}</option>
                                ))}
                            </select>
                        </div>

                        <div className="grid grid-cols-3 gap-3">
                            <div>
                                <label className={classeLabel}>Início <span className="text-red-500">*</span></label>
                                <input type="time" required value={form.horaInicio} onChange={atualizar('horaInicio')} className={classeInput}/>
                            </div>
                            <div>
                                <label className={classeLabel}>Fim <span className="text-red-500">*</span></label>
                                <input type="time" required min={form.horaInicio} value={form.horaFim} onChange={atualizar('horaFim')} className={classeInput}/>
                            </div>
                            <div>
                                <label className={classeLabel}>Consulta (min) <span className="text-red-500">*</span></label>
                                <input type="number" required min={5} max={240} step={5} value={form.duracaoConsultaMin} onChange={atualizar('duracaoConsultaMin')} className={classeInput}/>
                            </div>
                        </div>

                        {erro && (
                            <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                                {erro}
                            </div>
                        )}

                        <div className="flex items-center justify-between pt-1">
                            <button type="button" onClick={fechar} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 text-sm font-semibold transition-colors cursor-pointer">Fechar</button>
                            <button type="submit" disabled={ocupado} className="px-5 py-2 bg-cyan-800 hover:bg-cyan-900 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm disabled:opacity-60 disabled:cursor-not-allowed">
                                {ocupado ? 'Salvando...' : 'Adicionar'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}