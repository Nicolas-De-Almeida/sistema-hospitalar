import { useState } from 'react'
import { X } from 'lucide-react'

import { cadastrarProfissional } from '../../services/hospitalService'
import { mascararTelefone } from '../../utils/mascaras'
import { OPCOES_ESPECIALIDADE } from '../../utils/rotulos'

// Classe repetida em todos os campos (assim só mudamos o visual em um lugar)
const classeInput = 'w-full px-3 py-2.5 border border-slate-200 rounded-lg text-sm text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-cyan-700 focus:ring-1 focus:ring-cyan-700 transition-colors'
const classeLabel = 'block text-sm font-semibold text-slate-700 mb-1.5'

// Estado inicial do formulário (todos os campos vazios)
const FORMULARIO_VAZIO = {
    nome: '',
    registroProfissional: '',
    categoria: '',
    especialidade: '',
    areaCirurgica: '',
    telefone: '',
    email: '',
}

// onSalvo (opcional): função chamada depois de salvar, ex.: recarregar a lista de profissionais
export default function ModalNovoProfissional({ isOpen, onClose, onSalvo }) {
    const [form, setForm] = useState(FORMULARIO_VAZIO)
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')

    if (!isOpen) return null

    // Atualiza um campo: atualizar('nome') devolve a função que o onChange do input usa
    const atualizar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

    // Ao trocar a categoria, limpa os campos que só existem em outra categoria
    const trocarCategoria = (e) => setForm({ ...form, categoria: e.target.value, especialidade: '', areaCirurgica: '' })

    // Limpa o formulário e fecha
    function fechar() {
        setForm(FORMULARIO_VAZIO)
        setErro('')
        onClose()
    }

    async function salvar(e) {
        e.preventDefault()
        setSalvando(true)
        setErro('')

        // Especialidade só vale para ESPECIALISTA e área cirúrgica só para CIRURGIAO
        const dados = {
            nome: form.nome.trim(),
            registroProfissional: form.registroProfissional.trim(),
            categoria: form.categoria,
            especialidade: form.categoria === 'ESPECIALISTA' ? form.especialidade : null,
            areaCirurgica: form.categoria === 'CIRURGIAO' ? form.areaCirurgica.trim() : null,
            telefone: form.telefone.trim(),
            email: form.email.trim(),
        }

        try {
            await cadastrarProfissional(dados)
            if (onSalvo) await onSalvo()
            fechar()
        } catch (e) {
            // Mostra a mensagem do back end (ex.: "Já existe um profissional com este registro.")
            setErro(e.message)
        } finally {
            setSalvando(false)
        }
    }

    return (
        <div className="fixed inset-0 bg-slate-900/50 flex items-center justify-center z-50 p-4">

            <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg overflow-hidden flex flex-col max-h-[90vh]">

                <div className="flex items-start justify-between p-6 border-b border-slate-100">
                    <div>
                        <h2 className="text-xs font-bold tracking-widest text-cyan-800 uppercase">NOVO PROFISSIONAL</h2>
                        <p className="text-xs text-slate-400 mt-0.5">Preencha as informações para iniciar o cadastro.</p>
                    </div>
                    <button type="button" onClick={fechar} className="text-slate-400 hover:text-slate-600 transition-colors cursor-pointer p-1">
                        <X className="w-5 h-5" />
                    </button>
                </div>

                <div className="p-6 overflow-y-auto">

                    <div className="flex gap-3 mb-6">
                        <div>
                            <h3 className="font-semibold text-slate-800">Dados profissionais</h3>
                            <p className="text-sm text-slate-500">Informações de identificação, registro e contato.</p>
                        </div>
                    </div>

                    {/* O botão Salvar fica fora do <form>, por isso ele aponta para este id (atributo form) */}
                    <form id="form-novo-profissional" onSubmit={salvar} className="space-y-4">

                        <div>
                            <label className={classeLabel}>Nome completo <span className="text-red-500">*</span></label>
                            <input type="text" required maxLength={120} value={form.nome} onChange={atualizar('nome')} placeholder="Ex.: Dr. Carlos Lima" className={classeInput}/>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className={classeLabel}>Registro profissional <span className="text-red-500">*</span></label>
                                <input type="text" required maxLength={30} value={form.registroProfissional} onChange={atualizar('registroProfissional')} placeholder="Ex.: CRM-MG 12345" className={classeInput}/>
                            </div>
                            <div>
                                <label className={classeLabel}>Categoria <span className="text-red-500">*</span></label>
                                <select required value={form.categoria} onChange={trocarCategoria} className={classeInput}>
                                    <option value="">Selecione...</option>
                                    <option value="CLINICO_GERAL">Clínico geral</option>
                                    <option value="ESPECIALISTA">Médico especialista</option>
                                    <option value="CIRURGIAO">Cirurgião</option>
                                </select>
                            </div>
                        </div>

                        {/* Campo extra que depende da categoria escolhida */}
                        {form.categoria === 'ESPECIALISTA' && (
                            <div>
                                <label className={classeLabel}>Especialidade <span className="text-red-500">*</span></label>
                                <select required value={form.especialidade} onChange={atualizar('especialidade')} className={classeInput}>
                                    <option value="">Selecione...</option>
                                    {OPCOES_ESPECIALIDADE.map(([valor, texto]) => (
                                        <option key={valor} value={valor}>{texto}</option>
                                    ))}
                                </select>
                            </div>
                        )}

                        {form.categoria === 'CIRURGIAO' && (
                            <div>
                                <label className={classeLabel}>Área cirúrgica <span className="text-red-500">*</span></label>
                                <input type="text" required maxLength={100} value={form.areaCirurgica} onChange={atualizar('areaCirurgica')} placeholder="Ex.: Cirurgia cardíaca" className={classeInput}/>
                            </div>
                        )}

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className={classeLabel}>Telefone <span className="text-red-500">*</span></label>
                                <input type="text" required inputMode="tel" minLength={14} maxLength={15} value={form.telefone} onChange={(e) => setForm({ ...form, telefone: mascararTelefone(e.target.value) })} placeholder="(00) 00000-0000" className={classeInput}/>
                            </div>
                            <div>
                                <label className={classeLabel}>E-mail <span className="text-red-500">*</span></label>
                                <input type="email" required value={form.email} onChange={atualizar('email')} placeholder="nome@hospital.com" className={classeInput}/>
                            </div>
                        </div>

                        {erro && (
                            <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                                {erro}
                            </div>
                        )}

                    </form>
                </div>

                <div className="p-5 border-t border-slate-100 bg-slate-50/50 flex items-center justify-between">
                    <button type="button" onClick={fechar} className="px-5 py-2.5 text-sm font-semibold text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors cursor-pointer">Cancelar</button>
                    <button type="submit" form="form-novo-profissional" disabled={salvando} className="px-5 py-2.5 text-sm font-semibold text-white bg-cyan-800 rounded-lg hover:bg-cyan-900 shadow-sm transition-colors cursor-pointer disabled:opacity-60 disabled:cursor-not-allowed">
                        {salvando ? 'Salvando...' : 'Salvar'}
                    </button>
                </div>
            </div>
        </div>
    )
}