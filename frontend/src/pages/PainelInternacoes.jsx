import { useState } from 'react'
import { Plus } from 'lucide-react'
import ModalNovaInternacao from '../components/modals/ModalNovaInternacao'

export default function GestaoInternacoes() {
    const [modalNovaInternacao, setModalNovaInternacao] = useState(false);
    
    // Estado inicial vazio aguardando fazermos o Backend
    const [internacoes, setInternacoes] = useState([])

    const renderStatusBadge = (status) => {
        let colors = ""
        switch (status) {
            case 'Ativa':
                colors = "bg-teal-50 text-teal-700 marker-teal-500"
                break
            case 'Encerrada':
                colors = "bg-slate-100 text-slate-500 marker-slate-400"
                break
            default:
                colors = "bg-slate-100 text-slate-600 marker-slate-400"
        }

        const [bgText, markerText] = colors.split(" marker-")

        return (
            <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-bold ${bgText}`}>
                <span className={`w-1.5 h-1.5 rounded-full ${markerText}`}></span>
                {status}
            </span>
        )
    }

    return (
        <div className="p-8 max-w-7xl mx-auto h-[calc(100vh-4rem)] flex flex-col">
            
            <div className="flex items-center justify-between mb-8 shrink-0">
                <div>
                    <span className="font-bold tracking-wider text-cyan-800 uppercase text-xs">GESTÃO DE INTERNAÇÕES</span>
                </div>

                <button 
                    onClick={() => setModalNovaInternacao(true)} className="flex items-center gap-2 bg-cyan-800 hover:bg-cyan-900 text-white font-semibold text-sm px-4 py-2.5 rounded-lg shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4" />
                    <span>Nova internação</span>
                </button>
            </div>

            <div className="bg-white border border-slate-200 rounded-xl shadow-sm flex flex-col flex-1 min-h-0">
                
                <div className="p-5 border-b border-slate-100 shrink-0">
                    <h2 className="font-semibold text-slate-800 text-sm">Pacientes internados</h2>
                </div>

                <div className="overflow-auto flex-1">
                    <table className="w-full text-left text-sm whitespace-nowrap">
                        <thead className="sticky top-0 bg-slate-50/95 backdrop-blur-sm z-10">
                            <tr className="text-slate-500 font-bold border-b border-slate-200">
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Paciente</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Profissional Responsável</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Quarto</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Entrada</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Alta Prevista</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Status</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider text-right">Ações</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {internacoes.length === 0 ? (
                                <tr>
                                    <td colSpan="7" className="py-8 text-center text-slate-500">
                                        Nenhuma internação encontrada.
                                    </td>
                                </tr>
                            ) : (
                                internacoes.map((internacao) => (
                                    <tr key={internacao.id} className="hover:bg-slate-50/60 transition-colors group text-slate-700">
                                        <td className="py-3 px-5">
                                            <div className="flex items-center gap-2.5">
                                                <div className="w-7 h-7 rounded-full bg-teal-50 text-teal-700 font-bold text-[11px] flex items-center justify-center shrink-0">
                                                    {getIniciais(internacao.paciente)}
                                                </div>
                                                <span className="font-semibold text-slate-800">
                                                    {internacao.paciente}
                                                </span>
                                            </div>
                                        </td>
                                        <td className="py-3 px-5">{internacao.profissional}</td>
                                        <td className="py-3 px-5">{internacao.quarto}</td>
                                        <td className="py-3 px-5">{internacao.entrada}</td>
                                        <td className="py-3 px-5">{internacao.altaPrevista}</td>
                                        <td className="py-3 px-5">
                                            {renderStatusBadge(internacao.status)}
                                        </td>
                                        <td className="py-3 px-5 text-right text-slate-400">
                                            {/* Espaço reservado para os botões de ação que virão do backend */}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            <ModalNovaInternacao isOpen={modalNovaInternacao} onClose={() => setModalNovaInternacao(false)} />
        </div>
    )
}