import { useState } from 'react'
import { X, Bed } from 'lucide-react'

import { useDadosAoAbrir } from '../../hooks/useDadosAoAbrir'
import {
    criarInternacao,
    listarPacientes,
    listarProfissionais,
    listarQuartosDisponiveis,
} from '../../services/hospitalService'
import { dataHojeISO, formatarCpf, rotuloEspecialidade } from '../../utils/rotulos'

// Classes repetidas em todos os campos (assim só mudamos o visual em um lugar)
const classeInput = 'w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors placeholder:text-slate-300'
const classeLabel = 'block text-xs font-semibold text-slate-700 mb-1'

// A data de entrada já começa com a data de hoje
const formularioVazio = () => ({
    pacienteId: '',
    profissionalId: '',
    quartoId: '',
    dataEntrada: dataHojeISO(),
    dataPrevistaAlta: '',
    observacoes: '',
})

// Com a prop "paciente", o paciente já vem escolhido.
// Sem ela, aparece uma lista para escolher o paciente.
export default function ModalNovaInternacao({ isOpen, onClose, onSalvo, paciente = null }) {
    const [form, setForm] = useState(formularioVazio)
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')

    // Listas buscadas toda vez que o modal abre (assim os quartos com vaga estão sempre atualizados)
    const pacientes = useDadosAoAbrir(isOpen && paciente === null, listarPacientes)
    const profissionais = useDadosAoAbrir(isOpen, listarProfissionais)
    const quartos = useDadosAoAbrir(isOpen, listarQuartosDisponiveis)

    if (!isOpen) return null;

    // Só profissionais ativos podem ser responsáveis por uma internação
    const profissionaisAtivos = profissionais.dados.filter((p) => p.ativo)

    // Atualiza um campo atualizar devolve a função que o onChange usa
    const atualizar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

    // Limpa o formulário e fecha
    function fechar() {
        setForm(formularioVazio())
        setErro('')
        onClose()
    }

    async function salvar(e) {
        e.preventDefault()
        setSalvando(true)
        setErro('')

        const dados = {
            pacienteId: paciente ? paciente.id : Number(form.pacienteId),
            profissionalId: Number(form.profissionalId),
            quartoId: Number(form.quartoId),
            dataEntrada: form.dataEntrada,
            dataPrevistaAlta: form.dataPrevistaAlta,
            observacoes: form.observacoes.trim() || null,
        }

        try {
            await criarInternacao(dados)
            if (onSalvo) await onSalvo()
            fechar()
        } catch (e) {
            // Mostra a mensagem do back end ("O paciente já possui uma internação ativa")
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
                            <Bed className="w-6 h-6"/>
                        </div>
                        <button type="button" onClick={fechar} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                            <X className="w-5 h-5"/>
                        </button>
                    </div>
                    <h2 className="text-xl font-bold text-slate-800">Nova internação</h2>
                    <p className="text-xs text-slate-400 mt-1">Preencha as informações para internar o paciente.</p>
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
                        <label className={classeLabel}>Profissional responsável <span className="text-red-500">*</span></label>
                        <select required value={form.profissionalId} onChange={atualizar('profissionalId')} className={classeInput}>
                            <option value="">{profissionais.carregando ? 'Carregando...' : 'Selecione...'}</option>
                            {profissionaisAtivos.map((p) => (
                                <option key={p.id} value={p.id}>{p.nome} · {rotuloEspecialidade(p.especialidade)}</option>
                            ))}
                        </select>
                        {profissionais.erro && <p className="text-xs text-red-600 mt-1">{profissionais.erro}</p>}
                    </div>

                    <div>
                        <label className={classeLabel}>Quarto <span className="text-red-500">*</span></label>
                        <select required value={form.quartoId} onChange={atualizar('quartoId')} className={classeInput}>
                            <option value="">{quartos.carregando ? 'Carregando...' : 'Selecione...'}</option>
                            {quartos.dados.map((q) => (
                                <option key={q.id} value={q.id}>
                                    Quarto {q.numero} · {q.andar}º andar · {q.vagasDisponiveis} {q.vagasDisponiveis === 1 ? 'vaga' : 'vagas'}
                                </option>
                            ))}
                        </select>
                        {quartos.erro && <p className="text-xs text-red-600 mt-1">{quartos.erro}</p>}
                        {!quartos.carregando && !quartos.erro && quartos.dados.length === 0 && (
                            <p className="text-xs text-slate-400 mt-1">Nenhum quarto com vaga no momento.</p>
                        )}
                    </div>

                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={classeLabel}>Data de entrada <span className="text-red-500">*</span></label>
                            <input type="date" required value={form.dataEntrada} onChange={atualizar('dataEntrada')} className={classeInput}/>
                        </div>
                        <div>
                            <label className={classeLabel}>Alta prevista <span className="text-red-500">*</span></label>
                            <input type="date" required min={form.dataEntrada} value={form.dataPrevistaAlta} onChange={atualizar('dataPrevistaAlta')} className={classeInput}/>
                        </div>
                    </div>

                    <div>
                        <label className={classeLabel}>Observações</label>
                        <textarea rows={3} maxLength={2000} value={form.observacoes} onChange={atualizar('observacoes')} placeholder="Opcional" className={classeInput}/>
                    </div>

                    {erro && (
                        <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                            {erro}
                        </div>
                    )}

                    <div className="flex items-center justify-between pt-2">
                        <button type="button" onClick={fechar} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 text-sm font-semibold transition-colors cursor-pointer">Cancelar</button>
                        <button type="submit" disabled={salvando} className="px-5 py-2 bg-cyan-800 hover:bg-cyan-900 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm disabled:opacity-60 disabled:cursor-not-allowed">
                            {salvando ? 'Salvando...' : 'Internar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}