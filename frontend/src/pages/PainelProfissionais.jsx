import { Search, Plus, Clock, Pencil, ToggleRight } from 'lucide-react'

import profissionaisDataTeste from '../data/profissionaisDataTeste.json'
import { getIniciais } from '../utils/primeiraLetraMaiuscula';

export default function PainelProfissionais() {
    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <div>
                    <span className="font-bold tracking-wider text-cyan-800 uppercase">EQUIPE DE PROFISSIONAIS</span>
                </div>

                <button className="flex items-center gap-2 bg-teal-800 hover:bg-teal-900 text-white font-semibold text-sm px-4 py-2.5 rounded-xl shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4" />
                    <span>Novo profissional</span>
                </button>
            </div>

            <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
                
                <div className="p-5 border-b border-slate-100 flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h2 className="font-bold text-slate-800 text-base">Lista de profissionais</h2>
                        <p className="text-xs text-slate-400 mt-0.5">{profissionaisDataTeste?.length || 0} profissionais cadastrados</p>
                    </div>

                    <div className="relative w-full md:w-80">
                        <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
                        <input type="text" placeholder="Buscar por nome ou registro" className="w-full pl-9 pr-3 py-2 border border-slate-200 rounded-xl text-xs text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-400"/>
                    </div>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full text-left text-xs">
                        <thead>
                            <tr className="text-slate-400 font-bold border-b border-slate-100 bg-slate-50/50">
                                <th className="py-3 px-5">NOME</th>
                                <th className="py-3 px-5">REGISTRO PROFISSIONAL</th>
                                <th className="py-3 px-5">ESPECIALIDADE</th>
                                <th className="py-3 px-5">TELEFONE</th>
                                <th className="py-3 px-5">E-MAIL</th>
                                <th className="py-3 px-5">STATUS</th>
                                <th className="py-3 px-5 text-right">AÇÕES</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {profissionaisDataTeste.map((prof) => (
                                <tr key={prof.id} className="hover:bg-slate-50/60 transition-colors">
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-lg bg-teal-50 text-teal-800 font-bold text-xs flex items-center justify-center shrink-0">
                                                {getIniciais(prof.nome)}
                                            </div>
                                            <span className="font-bold text-slate-800 text-xs">
                                                {prof.nome}
                                            </span>
                                        </div>
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-800 font-bold">
                                        {prof.registro}
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">
                                        {prof.especialidade}
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">
                                        {prof.telefone}
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">
                                        {prof.email}
                                    </td>
                                    <td className="py-3.5 px-5">
                                        {prof.status === 'Ativo' ? (
                                            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-semibold bg-emerald-50 text-emerald-700">
                                                <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>Ativo</span>
                                        ) : (
                                            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-semibold bg-slate-100 text-slate-500">
                                                <span className="w-1.5 h-1.5 rounded-full bg-slate-400"></span>Inativo</span>
                                        )}
                                    </td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center justify-end gap-2 text-slate-400">
                                            <button className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Horários de Atendimento">
                                                <Clock className="w-4 h-4" />
                                            </button>
                                            <button className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Editar">
                                                <Pencil className="w-4 h-4" />
                                            </button>
                                            <button className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Alterar Status">
                                                <ToggleRight className="w-4 h-4" />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

            </div>
        </div>
    );
}