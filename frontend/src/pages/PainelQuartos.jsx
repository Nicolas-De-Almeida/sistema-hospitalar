import { Plus, Pencil } from 'lucide-react'

import LinhaEstado from '../components/LinhaEstado'
import BotaoAcao from '../components/BotaoAcao'
import { useDados } from '../hooks/useDados'
import { listarQuartos } from '../services/hospitalService'

export default function GestaoQuartos() {
    // Lista de quartos
    const { dados: quartos, carregando, erro } = useDados(listarQuartos)

    // Os números dos 3 cartões são calculados a partir da lista
    const disponiveis = quartos.filter((q) => q.situacao === 'DISPONIVEL').length
    const ocupados = quartos.length - disponiveis

    // Enquanto carrega mostra "...", se der erro mostra "-"
    const mostrar = (valor) => (carregando ? '...' : erro ? '-' : valor)

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <div>
                    <span className="font-bold tracking-wider text-cyan-800 uppercase">GESTÃO DE QUARTOS</span>
                </div>

                <button className="flex items-center gap-2 bg-cyan-800 hover:bg-cyan-900 text-white font-semibold text-sm px-4 py-2.5 rounded-xl shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4" />
                    <span>Novo quarto</span>
                </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <div className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Total de quartos</p>
                    <p className="text-2xl font-bold text-slate-800">{mostrar(quartos.length)}</p>
                </div>
                <div className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Disponíveis</p>
                    <p className="text-2xl font-bold text-emerald-600">{mostrar(disponiveis)}</p>
                </div>
                <div className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm">
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Ocupados</p>
                    <p className="text-2xl font-bold text-red-600">{mostrar(ocupados)}</p>
                </div>
            </div>

            <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
                <div className="p-5 border-b border-slate-100">
                    <h2 className="font-bold text-slate-800 text-base">Lista de quartos</h2>
                    <p className="text-xs text-slate-400 mt-0.5">{quartos.length} quartos cadastrados</p>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full text-left text-xs">
                        <thead>
                            <tr className="text-slate-400 font-bold border-b border-slate-100 bg-slate-50/50">
                                <th className="py-3 px-5">NÚMERO</th>
                                <th className="py-3 px-5">ANDAR</th>
                                <th className="py-3 px-5">OCUPAÇÃO</th>
                                <th className="py-3 px-5">SITUAÇÃO</th>
                                <th className="py-3 px-5 text-right">AÇÕES</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            <LinhaEstado
                                colunas={5}
                                carregando={carregando}
                                erro={erro}
                                quantidade={quartos.length}
                                vazio="Nenhum quarto cadastrado no momento."/>
                            {quartos.map((quarto) => (
                                <tr key={quarto.id} className="hover:bg-slate-50/60 transition-colors">
                                    <td className="py-3.5 px-5 text-slate-800 font-bold">{quarto.numero}</td>
                                    <td className="py-3.5 px-5 text-slate-600">{quarto.andar}º andar</td>
                                    <td className="py-3.5 px-5 text-slate-600">{quarto.ocupacaoAtual} / {quarto.capacidadeMaxima}</td>
                                    <td className="py-3.5 px-5">
                                        {quarto.situacao === 'DISPONIVEL' ? (
                                            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-semibold bg-emerald-50 text-emerald-700">
                                                <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>Disponível</span>
                                        ) : (
                                            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-semibold bg-red-50 text-red-700">
                                                <span className="w-1.5 h-1.5 rounded-full bg-red-500"></span>Ocupado</span>
                                        )}
                                    </td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center justify-end gap-2 text-slate-400">
                                            <BotaoAcao titulo="Editar quarto">
                                                <Pencil className="w-4 h-4" />
                                            </BotaoAcao>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    )
}