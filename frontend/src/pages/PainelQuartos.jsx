import { Plus} from 'lucide-react'

export default function GestaoQuartos() {
    
    return (
        <div className="p-8 max-w-7xl mx-auto h-[calc(100vh-4rem)] flex flex-col">
            
            <div className="flex items-center justify-between mb-6 shrink-0">
                <div>
                    <span className="font-bold tracking-wider text-teal-700 uppercase">GESTÃO DE QUARTOS</span>
                </div>

                <button className="flex items-center gap-2 bg-cyan-800 hover:bg-cyan-900 text-white font-semibold text-sm px-4 py-2.5 rounded-lg shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4" />
                    <span>Novo quarto</span>
                </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6 shrink-0">
                <div className="bg-white border border-slate-200 rounded-xl p-5 shadow-sm">
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Total de quartos</p>
                    <p className="text-2xl font-bold text-slate-800"></p>
                </div>
                <div className="bg-white border border-slate-200 rounded-xl p-5 shadow-sm">
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Disponíveis</p>
                    <p className="text-2xl font-bold text-emerald-600"></p>
                </div>
                <div className="bg-white border border-slate-200 rounded-xl p-5 shadow-sm">
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Ocupados</p>
                    <p className="text-2xl font-bold text-red-600"></p>
                </div>
            </div>

            <div className="bg-white border border-slate-200 rounded-xl shadow-sm flex flex-col flex-1 min-h-0">
                
                <div className="p-5 border-b border-slate-100 shrink-0">
                    <h2 className="font-semibold text-slate-800 text-sm">Lista de quartos</h2>
                </div>

                <div className="overflow-auto flex-1">
                    <table className="w-full text-left text-sm whitespace-nowrap">
                        <thead className="sticky top-0 bg-slate-50/95 backdrop-blur-sm z-10">
                            <tr className="text-slate-500 font-bold border-b border-slate-200">
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Número</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Andar</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Ocupação</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Situação</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider text-right">Ações</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                                <tr>
                                    <td colSpan="5" className="py-12 text-center text-slate-500">
                                        Nenhum quarto cadastrado no momento.
                                    </td>
                                </tr>
        
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    )
}