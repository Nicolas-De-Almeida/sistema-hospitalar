import { useState } from 'react'
import { X } from 'lucide-react'

import { cadastrarPaciente } from '../../services/hospitalService'
import { mascararCpf, mascararTelefone, mascararCep } from '../../utils/mascaras'

// Classe repetida em todos os campos (assim só mudamos o visual em um lugar)
const classeInput = 'w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors placeholder:text-slate-300'

// Estado inicial do formulário (todos os campos vazios)
const FORMULARIO_VAZIO = {
    nome: '',
    cpf: '',
    dataNascimento: '',
    telefone: '',
    email: '',
    cep: '',
    logradouro: '',
    numero: '',
    complemento: '',
    bairro: '',
    cidade: '',
    uf: '',
}

// onSalvo: função chamada depois de salvar (ex: recarregar a lista de pacientes)
export default function ModalNovoPaciente({ isOpen, onClose, onSalvo }) {
    const [form, setForm] = useState(FORMULARIO_VAZIO)
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')

    if (!isOpen) return null;

    // Atualiza um campo: atualizar('nome') devolve a função que o onChange do input usa
    const atualizar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

    // Igual ao atualizar, mas passa o padroniza o texto (CPF, telefone, CEP)
    const atualizarComMascara = (campo, mascara) => (e) => setForm({ ...form, [campo]: mascara(e.target.value) })

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

        // O back end espera o endereço dentro de um objeto "endereco"
        const dados = {
            nome: form.nome.trim(),
            cpf: form.cpf.trim(),
            dataNascimento: form.dataNascimento,
            telefone: form.telefone.trim(),
            email: form.email.trim(),
            endereco: {
                cep: form.cep.trim(),
                logradouro: form.logradouro.trim(),
                numero: form.numero.trim(),
                complemento: form.complemento.trim(),
                bairro: form.bairro.trim(),
                cidade: form.cidade.trim(),
                uf: form.uf.trim(),
            },
        }

        try {
            await cadastrarPaciente(dados)
            if (onSalvo) await onSalvo()
            fechar()
        } catch (e) {
            // Mostra a mensagem (ex: "Já existe um paciente com este CPF")
            setErro(e.message)
        } finally {
            setSalvando(false)
        }
    }

    return (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl w-full max-w-2xl shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
                <div className="flex items-start justify-between p-6 border-b border-slate-100">
                    <div>
                        <h2 className="text-xl text-cyan-800 font-bold text-slate-800 mt-0.5">Novo paciente</h2>
                        <p className="text-xs text-slate-400 mt-0.5">Preencha as informações para iniciar o cadastro.</p>
                    </div>
                    <button type="button" onClick={fechar} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                        <X className="w-5 h-5"/>
                    </button>
                </div>

                <form onSubmit={salvar} className="overflow-y-auto flex-1">
                    <div className="p-6 space-y-6">
                        <div>
                            <div className="flex items-center gap-2 mb-3">
                                <span className="bg-teal-50 text-teal-700 font-bold text-xs px-2 py-0.5 rounded-md">01</span>
                                <div>
                                    <h3 className="text-sm font-bold text-slate-800">Dados pessoais</h3>
                                    <p className="text-xs text-slate-400">Informações de identificação e contato.</p>
                                </div>
                            </div>

                            <div className="space-y-3">
                                <div>
                                    <label className="block text-xs font-semibold text-slate-700 mb-1">Nome completo <span className="text-red-500">*</span></label>
                                    <input type="text" required maxLength={120} value={form.nome} onChange={atualizar('nome')} placeholder="Nome do paciente" className={classeInput}/>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">CPF <span className="text-red-500">*</span></label>
                                        <input type="text" required inputMode="numeric" minLength={14} maxLength={14} value={form.cpf} onChange={atualizarComMascara('cpf', mascararCpf)} placeholder="000.000.000-00" className={classeInput}/>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Data de nascimento <span className="text-red-500">*</span></label>
                                        <input type="date" required value={form.dataNascimento} onChange={atualizar('dataNascimento')} className={classeInput}/>
                                    </div>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Telefone <span className="text-red-500">*</span></label>
                                        <input type="text" required inputMode="tel" minLength={14} maxLength={15} value={form.telefone} onChange={atualizarComMascara('telefone', mascararTelefone)} placeholder="(00) 00000-0000" className={classeInput}/>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">E-mail <span className="text-red-500">*</span></label>
                                        <input type="email" required value={form.email} onChange={atualizar('email')} placeholder="email@exemplo.com" className={classeInput}/>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div>
                            <div className="flex items-center gap-2 mb-3">
                                <span className="bg-teal-50 text-teal-700 font-bold text-xs px-2 py-0.5 rounded-md">02</span>
                                <div>
                                    <h3 className="text-sm font-bold text-slate-800">Endereço</h3>
                                    <p className="text-xs text-slate-400">Localização residencial do paciente.</p>
                                </div>
                            </div>

                            <div className="space-y-3">
                                <div className="grid grid-cols-1 md:grid-cols-6 gap-3">
                                    <div className="md:col-span-2">
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">CEP</label>
                                        <input type="text" inputMode="numeric" minLength={9} maxLength={9} value={form.cep} onChange={atualizarComMascara('cep', mascararCep)} placeholder="00000-000" className={classeInput}/>
                                    </div>
                                    <div className="md:col-span-3">
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Logradouro <span className="text-red-500">*</span></label>
                                        <input type="text" required value={form.logradouro} onChange={atualizar('logradouro')} placeholder="Rua, avenida..." className={classeInput}/>
                                    </div>
                                    <div className="md:col-span-1">
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Número</label>
                                        <input type="text" value={form.numero} onChange={atualizar('numero')} placeholder="Nº" className={classeInput}/>
                                    </div>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Complemento</label>
                                        <input type="text" value={form.complemento} onChange={atualizar('complemento')} placeholder="Apto, bloco..." className={classeInput}/>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Bairro</label>
                                        <input type="text" value={form.bairro} onChange={atualizar('bairro')} placeholder="Bairro" className={classeInput}/>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Cidade <span className="text-red-500">*</span></label>
                                        <input type="text" required value={form.cidade} onChange={atualizar('cidade')} placeholder="Cidade" className={classeInput}/>
                                    </div>
                                </div>

                                <div className="w-full md:w-1/3">
                                    <label className="block text-xs font-semibold text-slate-700 mb-1">UF <span className="text-red-500">*</span></label>
                                    <input type="text" required maxLength={2} value={form.uf} onChange={(e) => setForm({ ...form, uf: e.target.value.toUpperCase() })} placeholder="UF" className={classeInput}/>
                                </div>
                            </div>
                        </div>

                        {erro && (
                            <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                                {erro}
                            </div>
                        )}
                    </div>

                    <div className="flex items-center justify-between p-4 bg-slate-50 border-t border-slate-100">
                        <button type="button" onClick={fechar} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-white text-sm font-semibold transition-colors cursor-pointer">Cancelar</button>
                        <button type="submit" disabled={salvando} className="px-5 py-2 bg-cyan-800 hover:bg-cyan-900 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm disabled:opacity-60 disabled:cursor-not-allowed">
                            {salvando ? 'Salvando...' : 'Salvar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}