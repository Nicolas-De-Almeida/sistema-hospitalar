import { useState } from 'react'
import { X, Bed } from 'lucide-react'

import { atualizarQuarto, cadastrarQuarto } from '../../services/hospitalService'
import { TIPOS_QUARTO } from '../../utils/rotulos'

// Classes repetidas em todos os campos (assim só muda o visual em um lugar)
const classeInput = 'w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors placeholder:text-slate-300'
const classeLabel = 'block text-xs font-semibold text-slate-700 mb-1'

// Estado inicial: vazio (novo quarto) ou preenchido com os dados do quarto (edição)
function formularioInicial(quarto) {
    return {
        numero: quarto?.numero ?? '',
        andar: quarto ? String(quarto.andar) : '',
        tipo: quarto?.tipo ?? '',
        capacidadeMaxima: quarto ? String(quarto.capacidadeMaxima) : '',
    }
}

// Serve para cadastrar e para editar
export default function ModalQuarto({ isOpen, onClose, onSalvo, quarto = null }) {
    const editando = quarto !== null
    const [form, setForm] = useState(() => formularioInicial(quarto))
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')

    if (!isOpen) return null

    // Cada tipo de quarto comporta um número máximo de pacientes
    const limite = TIPOS_QUARTO[form.tipo]?.limite

    // Atualiza um campo: atualizar('numero') devolve a função que o onChange usa
    const atualizar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

    // Ao trocar o tipo, a capacidade não pode passar do limite do novo tipo
    function trocarTipo(e) {
        const tipo = e.target.value
        const novoLimite = TIPOS_QUARTO[tipo]?.limite
        const capacidade = Number(form.capacidadeMaxima)
        const ajustada = novoLimite && capacidade > novoLimite ? String(novoLimite) : form.capacidadeMaxima
        setForm({ ...form, tipo, capacidadeMaxima: ajustada })
    }

    // Limpa o formulário e fecha
    function fechar() {
        setForm(formularioInicial(quarto))
        setErro('')
        onClose()
    }

    async function salvar(e) {
        e.preventDefault()
        setSalvando(true)
        setErro('')

        const dados = {
            numero: form.numero.trim(),
            andar: Number(form.andar),
            capacidadeMaxima: Number(form.capacidadeMaxima),
            tipo: form.tipo,
        }

        try {
            if (editando) {
                await atualizarQuarto(quarto.id, dados)
            } else {
                await cadastrarQuarto(dados)
            }
            if (onSalvo) await onSalvo()
            fechar()
        } catch (e) {
            // Mostra a mensagem do back end ("Já existe um quarto cadastrado com este número")
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
                    <h2 className="text-xl font-bold text-slate-800">{editando ? 'Editar quarto' : 'Novo quarto'}</h2>
                    <p className="text-xs text-slate-400 mt-1">
                        {editando ? 'Altere as informações do quarto.' : 'Preencha as informações para cadastrar o quarto.'}
                    </p>
                </div>

                <form onSubmit={salvar} className="p-6 pt-4 space-y-4 overflow-y-auto">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={classeLabel}>Número <span className="text-red-500">*</span></label>
                            <input type="text" required maxLength={20} value={form.numero} onChange={atualizar('numero')} placeholder="Ex.: 101" className={classeInput}/>
                        </div>
                        <div>
                            <label className={classeLabel}>Andar <span className="text-red-500">*</span></label>
                            <input type="number" required min={0} max={99} value={form.andar} onChange={atualizar('andar')} placeholder="Ex.: 1" className={classeInput}/>
                        </div>
                    </div>

                    <div>
                        <label className={classeLabel}>Tipo <span className="text-red-500">*</span></label>
                        <select required value={form.tipo} onChange={trocarTipo} className={classeInput}>
                            <option value="">Selecione...</option>
                            {Object.entries(TIPOS_QUARTO).map(([valor, { texto }]) => (
                                <option key={valor} value={valor}>{texto}</option>
                            ))}
                        </select>
                    </div>

                    <div>
                        <label className={classeLabel}>Capacidade (leitos) <span className="text-red-500">*</span></label>
                        <input
                            type="number"
                            required
                            min={1}
                            max={limite ?? undefined}
                            value={form.capacidadeMaxima}
                            onChange={atualizar('capacidadeMaxima')}
                            placeholder="Ex.: 2"
                            className={classeInput}/>
                        <p className="text-xs text-slate-400 mt-1">
                            {limite ? `Esse tipo de quarto comporta no máximo ${limite} ${limite === 1 ? 'paciente' : 'pacientes'}.` : 'Escolha o tipo para ver o limite de leitos.'}
                        </p>
                        {editando && quarto.ocupacaoAtual > 0 && (
                            <p className="text-xs text-slate-400 mt-1">
                                Há {quarto.ocupacaoAtual} {quarto.ocupacaoAtual === 1 ? 'paciente internado' : 'pacientes internados'} agora; a capacidade não pode ser menor que isso.
                            </p>
                        )}
                    </div>

                    {erro && (
                        <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                            {erro}
                        </div>
                    )}

                    <div className="flex items-center justify-between pt-2">
                        <button type="button" onClick={fechar} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 text-sm font-semibold transition-colors cursor-pointer">Cancelar</button>
                        <button type="submit" disabled={salvando} className="px-5 py-2 bg-cyan-800 hover:bg-cyan-900 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm disabled:opacity-60 disabled:cursor-not-allowed">
                            {salvando ? 'Salvando...' : 'Salvar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}