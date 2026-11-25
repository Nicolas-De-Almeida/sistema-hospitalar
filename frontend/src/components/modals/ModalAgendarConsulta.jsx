import { useEffect, useState } from 'react'
import { X, Calendar } from 'lucide-react'

import { useDadosAoAbrir } from '../../hooks/useDadosAoAbrir'
import { agendarConsulta, listarPacientes, listarProfissionaisDisponiveis } from '../../services/hospitalService'
import { dataHojeISO, formatarCpf, OPCOES_ESPECIALIDADE } from '../../utils/rotulos'

// Classes repetidas em todos os campos (assim só mudamos o visual em um lugar)
const classeInput = 'w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors placeholder:text-slate-300 disabled:bg-slate-50 disabled:text-slate-400'
const classeLabel = 'block text-xs font-semibold text-slate-700 mb-1'

const OPCOES_ESPECIALIDADE_CONSULTA = [['CLINICO_GERAL', 'Clínico geral'], ...OPCOES_ESPECIALIDADE]

const FORMULARIO_VAZIO = {
    pacienteId: '',
    especialidade: '',
    data: '',
    horario: '',
    profissionalId: '',
    motivo: '',
}

// Com a prop "paciente", o paciente já vem escolhido.
// Sem ela (botão "Nova consulta"), aparece uma lista para escolher o paciente.
export default function ModalAgendarConsulta({ isOpen, onClose, onSalvo, paciente = null }) {
    const [form, setForm] = useState(FORMULARIO_VAZIO)
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')

    // Lista de pacientes, só é buscada se o paciente não veio escolhido
    const pacientes = useDadosAoAbrir(isOpen && paciente === null, listarPacientes)

    // Profissionais disponíveis para a especialidade + data + horário escolhidos
    const [disponiveis, setDisponiveis] = useState([])
    const [buscando, setBuscando] = useState(false)
    const [erroBusca, setErroBusca] = useState('')

    const { especialidade, data, horario } = form
    const criterioCompleto = Boolean(especialidade && data && horario)

    useEffect(() => {
        if (!criterioCompleto) return

        let cancelado = false
        setBuscando(true)
        setErroBusca('')
        setDisponiveis([])

        listarProfissionaisDisponiveis(especialidade, data, horario)
            .then((lista) => { if (!cancelado) setDisponiveis(lista) })
            .catch((e) => { if (!cancelado) setErroBusca(e.message) })
            .finally(() => { if (!cancelado) setBuscando(false) })

        return () => { cancelado = true }
    }, [criterioCompleto, especialidade, data, horario])

    if (!isOpen) return null;

    // Atualiza um campo, devolve a função que o onChange usa
    const atualizar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

    // Mudou especialidade, data ou horário, o médico escolhido antes pode não servir mais
    const atualizarCriterio = (campo) => (e) => setForm({ ...form, [campo]: e.target.value, profissionalId: '' })

    // Limpa o formulário e fecha
    function fechar() {
        setForm(FORMULARIO_VAZIO)
        setDisponiveis([])
        setErro('')
        onClose()
    }

    async function salvar(e) {
        e.preventDefault()
        setSalvando(true)
        setErro('')

        const rotina = form.especialidade === 'CLINICO_GERAL'

        const dados = {
            pacienteId: paciente ? paciente.id : Number(form.pacienteId),
            profissionalId: Number(form.profissionalId),
            modalidade: rotina ? 'ROTINA' : 'ESPECIALIZADA',
            especialidade: rotina ? null : form.especialidade,
            data: form.data,
            horario: form.horario,
            motivo: form.motivo.trim(),
        }

        try {
            await agendarConsulta(dados)
            if (onSalvo) await onSalvo()
            fechar()
        } catch (e) {
            // Mostra a mensagem do back end ("O profissional já tem consulta neste horário.")
            setErro(e.message)
        } finally {
            setSalvando(false)
        }
    }

    // Texto de ajuda embaixo da lista de médicos
    let ajudaMedico = ''
    if (!criterioCompleto) ajudaMedico = 'Escolha a especialidade, a data e o horário para ver os médicos disponíveis.'
    else if (buscando) ajudaMedico = 'Buscando médicos...'
    else if (erroBusca) ajudaMedico = erroBusca
    else if (disponiveis.length === 0) ajudaMedico = 'Nenhum médico disponível para esse horário. Tente outro dia ou horário.'

    return (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
                <div className="p-6 pb-2">
                    <div className="flex items-start justify-between mb-3">
                        <div className="bg-teal-50 text-teal-800 p-2.5 rounded-xl border border-teal-100">
                            <Calendar className="w-6 h-6"/>
                        </div>
                        <button type="button" onClick={fechar} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                            <X className="w-5 h-5"/>
                        </button>
                    </div>
                    <h2 className="text-xl font-bold text-slate-800">Agendar consulta</h2>
                    <p className="text-xs text-slate-400 mt-1">Preencha as informações para agendar.</p>
                </div>

                <form onSubmit={salvar} className="p-6 pt-4 space-y-4 overflow-y-auto">
                    {paciente ? (
                        <div className="p-3 bg-slate-50 border border-slate-100 rounded-xl">
                            <p className="text-xs text-slate-400">Paciente</p>
                            <p className="text-sm font-bold text-slate-800">{paciente.nome}</p>
                            <p className="text-xs text-slate-500">CPF {formatarCpf(paciente.cpf)}</p>
                        </div>
                    ) : (
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
                    )}

                    <div>
                        <label className={classeLabel}>Especialidade <span className="text-red-500">*</span></label>
                        <select required value={form.especialidade} onChange={atualizarCriterio('especialidade')} className={classeInput}>
                            <option value="">Selecione...</option>
                            {OPCOES_ESPECIALIDADE_CONSULTA.map(([valor, texto]) => (
                                <option key={valor} value={valor}>{texto}</option>
                            ))}
                        </select>
                    </div>

                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={classeLabel}>Data <span className="text-red-500">*</span></label>
                            <input type="date" required min={dataHojeISO()} value={form.data} onChange={atualizarCriterio('data')} className={classeInput}/>
                        </div>
                        <div>
                            <label className={classeLabel}>Horário <span className="text-red-500">*</span></label>
                            <input type="time" required step={1800} value={form.horario} onChange={atualizarCriterio('horario')} className={classeInput}/>
                        </div>
                    </div>

                    <div>
                        <label className={classeLabel}>Médico <span className="text-red-500">*</span></label>
                        <select
                            required
                            disabled={!criterioCompleto || buscando || disponiveis.length === 0}
                            value={form.profissionalId}
                            onChange={atualizar('profissionalId')}
                            className={classeInput}>
                            <option value="">Selecione...</option>
                            {disponiveis.map((p) => (
                                <option key={p.id} value={p.id}>{p.nome} · {p.registroProfissional}</option>
                            ))}
                        </select>
                        {ajudaMedico && <p className={`text-xs mt-1 ${erroBusca ? 'text-red-600' : 'text-slate-400'}`}>{ajudaMedico}</p>}
                    </div>

                    <div>
                        <label className={classeLabel}>Motivo da consulta <span className="text-red-500">*</span></label>
                        <input type="text" required maxLength={255} value={form.motivo} onChange={atualizar('motivo')} placeholder="Ex.: Dor no peito" className={classeInput}/>
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