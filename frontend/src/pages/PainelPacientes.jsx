import { Search, Plus, FileText, Calendar, Bed, Pencil, Trash2 } from 'lucide-react'

import pacientesDataTeste from '../data/CardsTeste.json'
import { getIniciais } from '../utils/primeiraLetraMaiuscula'



export default function PainelPacientes() {

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <div>
                    <span className="font-bold tracking-wider text-cyan-800 uppercase">GESTÃO DE CADASTROS</span>
                </div>

                <button className="flex items-center gap-2 bg-cyan-800 hover:bg-cyan-950 text-white font-semibold text-sm px-4 py-2.5 rounded-xl shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4" />
                    <span>Novo paciente</span>
                </button>
            </div>

            <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
                <div className="p-5 border-b border-slate-100 flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h2 className="font-bold text-slate-800 text-base">Lista de pacientes</h2>
                        <p className="text-xs text-slate-400 mt-0.5">{pacientesDataTeste?.length || 0} pacientes cadastrados</p>
                    </div>
                    <div className="relative w-full md:w-80">
                        <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2"/>
                        <input type="text" placeholder="Buscar por nome ou CPF" className="w-full pl-9 pr-3 py-2 border border-slate-200 rounded-xl text-xs text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-400"/>
                    </div>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full text-left text-xs">
                        <thead>
                            <tr className="text-slate-400 font-bold border-b border-slate-100 bg-slate-50/50">
                                <th className="py-3 px-5">NOME</th>
                                <th className="py-3 px-5">CPF</th>
                                <th className="py-3 px-5">TELEFONE</th>
                                <th className="py-3 px-5">E-MAIL</th>
                                <th className="py-3 px-5 text-right">AÇÕES</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {pacientesDataTeste.map((paciente) => (
                                <tr key={paciente.id} className="hover:bg-slate-50/60 transition-colors">
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-lg bg-teal-50 text-teal-800 font-bold text-xs flex items-center justify-center shrink-0">
                                                {getIniciais(paciente.nome || paciente.paciente)}
                                            </div>
                                            <span className="font-bold text-slate-800 text-xs">
                                                {paciente.nome || paciente.paciente}
                                            </span>
                                        </div>
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600 font-medium">
                                        {paciente.cpf}
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">
                                        {paciente.telefone}
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">
                                        {paciente.email}
                                    </td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center justify-end gap-2 text-slate-400">
                                            <button className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Prontuário">
                                                <FileText className="w-4 h-4"/>
                                            </button>
                                            <button className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Agendar Consulta">
                                                <Calendar className="w-4 h-4"/>
                                            </button>
                                            <button className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Nova Internação">
                                                <Bed className="w-4 h-4"/>
                                            </button>
                                            <span className="text-slate-200">|</span>
                                            <button className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Editar">
                                                <Pencil className="w-4 h-4"/>
                                            </button>
                                            <button className="hover:text-red-600 p-1 transition-colors cursor-pointer" title="Excluir">
                                                <Trash2 className="w-4 h-4"/>
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