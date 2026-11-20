import { useEffect, useState } from 'react'
import { X } from 'lucide-react'

import { buscarHistoricoPaciente } from '../../services/hospitalService'
import { formatarCpf, formatarData, formatarDataHora, formatarHora } from '../../utils/rotulos'

const NOMES_REGISTRO = {
    EVOLUCAO: 'Evolução',
    DIAGNOSTICO: 'Diagnóstico',
    PRESCRICAO: 'Prescrição',
    OBSERVACAO: 'Observação',
}

// Texto cinza usado quando uma seção não tem nada para mostrar
function Vazio({ children }) {
    return <p className="text-xs text-slate-400">{children}</p>
}

// Mostra o histórico médico de um paciente: consultas concluídas, internações encerradas
// e registros clínicos. Busca os dados no back end toda vez que abre.
export default function ModalProntuario({ isOpen, onClose, paciente }) {
    const [historico, setHistorico] = useState(null)
    const [carregando, setCarregando] = useState(false)
    const [erro, setErro] = useState('')

    const pacienteId = paciente?.id

    useEffect(() => {
        if (!isOpen || !pacienteId) return

        let cancelado = false
        setCarregando(true)
        setErro('')
        setHistorico(null)

        buscarHistoricoPaciente(pacienteId)
            .then((dados) => { if (!cancelado) setHistorico(dados) })
            .catch((e) => { if (!cancelado) setErro(e.message) })
            .finally(() => { if (!cancelado) setCarregando(false) })

        // Se o modal fechar antes da resposta chegar, ignora a resposta
        return () => { cancelado = true }
    }, [isOpen, pacienteId])

    if (!isOpen) return null

    return (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl w-full max-w-2xl shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
                <div className="flex items-start justify-between p-6 border-b border-slate-100">
                    <div>
                        <h2 className="text-xl text-cyan-800 font-bold mt-0.5">Prontuário</h2>
                        <p className="text-xs text-slate-400 mt-0.5">
                            {paciente?.nome} · CPF {formatarCpf(paciente?.cpf)}
                        </p>
                    </div>
                    <button type="button" onClick={onClose} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                        <X className="w-5 h-5"/>
                    </button>
                </div>

                <div className="p-6 overflow-y-auto flex-1 space-y-6">
                    {carregando && <p className="text-sm text-slate-500">Carregando...</p>}

                    {erro && (
                        <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
                            {erro}
                        </div>
                    )}

                    {historico && (
                        <>
                            <section>
                                <h3 className="text-sm font-bold text-slate-800 mb-2">Consultas concluídas</h3>
                                {historico.consultas.length === 0 ? (
                                    <Vazio>Nenhuma consulta concluída.</Vazio>
                                ) : (
                                    <ul className="space-y-2">
                                        {historico.consultas.map((c) => (
                                            <li key={c.id} className="p-3 border border-slate-100 rounded-xl text-xs">
                                                <p className="font-bold text-slate-800">
                                                    {formatarData(c.data)} às {formatarHora(c.horario)} · {c.profissional}
                                                </p>
                                                <p className="text-slate-600 mt-0.5">Motivo: {c.motivo}</p>
                                                {c.observacoes && <p className="text-slate-500 mt-0.5">Observações: {c.observacoes}</p>}
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </section>

                            <section>
                                <h3 className="text-sm font-bold text-slate-800 mb-2">Internações encerradas</h3>
                                {historico.internacoes.length === 0 ? (
                                    <Vazio>Nenhuma internação encerrada.</Vazio>
                                ) : (
                                    <ul className="space-y-2">
                                        {historico.internacoes.map((i) => (
                                            <li key={i.id} className="p-3 border border-slate-100 rounded-xl text-xs">
                                                <p className="font-bold text-slate-800">
                                                    {formatarData(i.dataEntrada)} até {formatarData(i.dataEfetivaAlta)} · Quarto {i.quarto}
                                                </p>
                                                <p className="text-slate-600 mt-0.5">Responsável: {i.profissional}</p>
                                                {i.observacoes && <p className="text-slate-500 mt-0.5">Observações: {i.observacoes}</p>}
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </section>

                            <section>
                                <h3 className="text-sm font-bold text-slate-800 mb-2">Registros clínicos</h3>
                                {historico.registros.length === 0 ? (
                                    <Vazio>Nenhum registro clínico.</Vazio>
                                ) : (
                                    <ul className="space-y-2">
                                        {historico.registros.map((r) => (
                                            <li key={r.id} className="p-3 border border-slate-100 rounded-xl text-xs">
                                                <p className="font-bold text-slate-800">
                                                    {NOMES_REGISTRO[r.tipo] || r.tipo} · {formatarDataHora(r.dataHora)}
                                                </p>
                                                <p className="text-slate-600 mt-0.5">{r.descricao}</p>
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </section>
                        </>
                    )}
                </div>

                <div className="flex items-center justify-end p-4 bg-slate-50 border-t border-slate-100">
                    <button type="button" onClick={onClose} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-white text-sm font-semibold transition-colors cursor-pointer">Fechar</button>
                </div>
            </div>
        </div>
    )
}