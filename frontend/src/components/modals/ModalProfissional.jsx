import { useState } from 'react'
import { X } from 'lucide-react'

import { adicionarHorario, atualizarProfissional, cadastrarProfissional } from '../../services/hospitalService'
import { mascararTelefone } from '../../utils/mascaras'
import { AREAS_CIRURGICAS, DIAS_SEMANA, OPCOES_ESPECIALIDADE } from '../../utils/rotulos'

// Classe repetida em todos os campos (assim só mudamos o visual em um lugar)
const classeInput = 'w-full px-3 py-2.5 border border-slate-200 rounded-lg text-sm text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-cyan-700 focus:ring-1 focus:ring-cyan-700 transition-colors'
const classeLabel = 'block text-sm font-semibold text-slate-700 mb-1.5'

// Estado inicial do formulário: vazio (novo) ou preenchido com os dados do profissional (edição)
function formularioInicial(profissional) {
    return {
        nome: profissional?.nome ?? '',
        registroProfissional: profissional?.registroProfissional ?? '',
        categoria: profissional?.categoria ?? '',
        especialidade: profissional?.especialidade ?? '',
        areaCirurgica: profissional?.areaCirurgica ?? '',
        telefone: mascararTelefone(profissional?.telefone ?? ''),
        email: profissional?.email ?? '',
        // Horário de atendimento (só no cadastro; na edição use o botão "Horários")
        dias: [],
        horaInicio: '',
        horaFim: '',
    }
}

// Serve para cadastrar e para editar
export default function ModalProfissional({ isOpen, onClose, onSalvo, profissional = null }) {
    const editando = profissional !== null
    const [form, setForm] = useState(() => formularioInicial(profissional))
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')

    if (!isOpen) return null

    // Atualiza um campo: atualizar('nome') devolve a função que o onChange do input usa
    const atualizar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

    // Ao trocar a categoria, limpa os campos que só existem em outra categoria
    const trocarCategoria = (e) => setForm({ ...form, categoria: e.target.value, especialidade: '', areaCirurgica: '' })

    // Marca/desmarca um dia da semana
    const alternarDia = (dia) => setForm({
        ...form,
        dias: form.dias.includes(dia) ? form.dias.filter((d) => d !== dia) : [...form.dias, dia],
    })

    // Área cirúrgica de um cadastro antigo (texto livre) continua aparecendo na lista
    const areas = form.areaCirurgica && !AREAS_CIRURGICAS.includes(form.areaCirurgica)
        ? [form.areaCirurgica, ...AREAS_CIRURGICAS]
        : AREAS_CIRURGICAS

    // Limpa o formulário e fecha
    function fechar() {
        setForm(formularioInicial(profissional))
        setErro('')
        onClose()
    }

    async function salvar(e) {
        e.preventDefault()
        setErro('')

        // Horário de atendimento é opcional, mas se marcou dias precisa de início e fim
        const querHorario = !editando && form.dias.length > 0
        if (querHorario && (!form.horaInicio || !form.horaFim)) {
            setErro('Informe a hora de início e a hora de fim do atendimento.')
            return
        }
        if (querHorario && form.horaFim <= form.horaInicio) {
            setErro('A hora de fim deve ser depois da hora de início.')
            return
        }
        setSalvando(true)

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
            if (editando) {
                await atualizarProfissional(profissional.id, dados)
            } else {
                const novo = await cadastrarProfissional(dados)
                if (querHorario) {
                    // Um expediente por dia marcado (cada consulta dura 30 minutos)
                    const diasOrdenados = DIAS_SEMANA.map(([valor]) => valor).filter((d) => form.dias.includes(d))
                    try {
                        for (const dia of diasOrdenados) {
                            await adicionarHorario(novo.id, {
                                diaSemana: dia,
                                horaInicio: form.horaInicio,
                                horaFim: form.horaFim,
                                duracaoConsultaMin: 30,
                            })
                        }
                    } catch (erroHorario) {
                        // O profissional já foi criado: não dá para salvar de novo, então fecha e avisa
                        if (onSalvo) await onSalvo()
                        fechar()
                        window.alert(`Profissional cadastrado, mas os horários não foram salvos: ${erroHorario.message}\nUse o botão "Horários" na lista para cadastrá-los.`)
                        return
                    }
                }
            }
            if (onSalvo) await onSalvo()
            fechar()
        } catch (e) {
            // Mostra a mensagem do back end ("Já existe um profissional com este registro.")
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
                        <h2 className="text-xs font-bold tracking-widest text-cyan-800 uppercase">{editando ? 'EDITAR PROFISSIONAL' : 'NOVO PROFISSIONAL'}</h2>
                        <p className="text-xs text-slate-400 mt-0.5">
                            {editando ? 'Altere as informações do profissional.' : 'Preencha as informações para iniciar o cadastro.'}
                        </p>
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
                    <form id="form-profissional" onSubmit={salvar} className="space-y-4">

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
                                <select required disabled={editando} value={form.categoria} onChange={trocarCategoria} className={`${classeInput} disabled:bg-slate-50 disabled:text-slate-400`}>
                                    <option value="">Selecione...</option>
                                    <option value="CLINICO_GERAL">Clínico geral</option>
                                    <option value="ESPECIALISTA">Médico especialista</option>
                                    <option value="CIRURGIAO">Cirurgião</option>
                                </select>
                                {editando && <p className="text-xs text-slate-400 mt-1">A categoria não pode ser alterada.</p>}
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
                                <select required value={form.areaCirurgica} onChange={atualizar('areaCirurgica')} className={classeInput}>
                                    <option value="">Selecione...</option>
                                    {areas.map((area) => <option key={area} value={area}>{area}</option>)}
                                </select>
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

                        {!editando && (
                            <div className="border border-slate-100 rounded-xl p-4 bg-slate-50/50">
                                <h3 className="text-sm font-bold text-slate-800">Horário de atendimento <span className="font-normal text-slate-400">(opcional)</span></h3>
                                <p className="text-xs text-slate-400 mb-3">Marque os dias em que atende. Cada consulta dura 30 minutos. Dá para ajustar depois no botão "Horários".</p>

                                <div className="flex flex-wrap gap-2 mb-3">
                                    {DIAS_SEMANA.map(([valor, texto]) => {
                                        const marcado = form.dias.includes(valor)
                                        return (
                                            <button type="button" key={valor} aria-pressed={marcado} onClick={() => alternarDia(valor)}
                                                className={`px-3 py-1.5 rounded-lg text-xs font-semibold border transition-colors cursor-pointer ${marcado ? 'bg-cyan-800 text-white border-cyan-800' : 'bg-white text-slate-600 border-slate-200 hover:bg-slate-50'}`}>
                                                {texto}
                                            </button>
                                        )
                                    })}
                                </div>

                                <div className="grid grid-cols-2 gap-3">
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Início</label>
                                        <input type="time" value={form.horaInicio} onChange={atualizar('horaInicio')} className={classeInput}/>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Fim</label>
                                        <input type="time" value={form.horaFim} onChange={atualizar('horaFim')} className={classeInput}/>
                                    </div>
                                </div>
                            </div>
                        )}

                        {erro && (
                            <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                                {erro}
                            </div>
                        )}

                    </form>
                </div>

                <div className="p-5 border-t border-slate-100 bg-slate-50/50 flex items-center justify-between">
                    <button type="button" onClick={fechar} className="px-5 py-2.5 text-sm font-semibold text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors cursor-pointer">Cancelar</button>
                    <button type="submit" form="form-profissional" disabled={salvando} className="px-5 py-2.5 text-sm font-semibold text-white bg-cyan-800 rounded-lg hover:bg-cyan-900 shadow-sm transition-colors cursor-pointer disabled:opacity-60 disabled:cursor-not-allowed">
                        {salvando ? 'Salvando...' : 'Salvar'}
                    </button>
                </div>
            </div>
        </div>
    )
}