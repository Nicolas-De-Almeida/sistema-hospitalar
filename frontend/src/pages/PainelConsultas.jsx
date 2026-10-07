import { useState } from 'react'
import { Plus } from 'lucide-react'
import ModalAgendarConsulta from '../components/modals/ModalAgendarConsulta'

const getIniciais = (nome) => {
    if (!nome) return ''
    return nome.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
};

export default function GestaoConsultas() {
    const [modalNovaConsulta, setModalNovaConsulta] = useState(false);

    // Estado inicial vazio aguardando fazermos o Backend
    const [consultas, setConsultas] = useState([]);

    const renderStatusBadge = (status) => {
        let colors = ""
        switch (status) {
            case 'Realizada':
                colors = "bg-emerald-100 text-emerald-700 marker-emerald-500"
                break;
            case 'Agendada':
                colors = "bg-amber-100 text-amber-700 marker-amber-500"
                break;
            case 'Cancelada':
                colors = "bg-red-100 text-red-700 marker-red-500"
                break;
            case 'Faltou':
                colors = "bg-slate-200 text-slate-700 marker-slate-500"
                break;
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
                    <span className="font-bold tracking-wider text-cyan-900 uppercase">GESTÃO DE CONSULTAS</span>
                    <p className="text-sm text-slate-500 mt-0.5">Agendamento e controle de consultas</p>
                </div>

                <button onClick={() => setModalNovaConsulta(true)} className="flex items-center gap-2 bg-cyan-800 hover:bg-cyan-900 text-white font-semibold text-sm px-4 py-2.5 rounded-lg shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4" />
                    <span>Nova consulta</span>
                </button>
            </div>

            <div className="bg-white border border-slate-200 rounded-xl shadow-sm flex flex-col flex-1 min-h-0">
                <div className="p-5 border-b border-slate-100 shrink-0">
                    <h2 className="font-semibold text-slate-800 text-sm">Agenda de consultas</h2>
                    <p className="text-xs text-slate-500 mt-0.5">{consultas.length} consultas encontradas</p>
                </div>

                <div className="overflow-auto flex-1">
                    <table className="w-full text-left text-sm whitespace-nowrap">
                        <thead className="sticky top-0 bg-slate-50/95 backdrop-blur-sm z-10">
                            <tr className="text-slate-500 font-bold border-b border-slate-200">
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Data</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Horário</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Paciente</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Profissional</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Motivo</th>
                                <th className="py-3.5 px-5 uppercase text-xs tracking-wider">Status</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {consultas.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="py-8 text-center text-slate-500">
                                        Nenhuma consulta encontrada.
                                    </td>
                                </tr>
                            ) : (
                                consultas.map((consulta) => (
                                    <tr key={consulta.id} className="hover:bg-slate-50/60 transition-colors group text-slate-700">
                                        <td className="py-3 px-5 font-semibold text-slate-800">{consulta.data}</td>
                                        <td className="py-3 px-5 font-semibold text-slate-800">{consulta.horario}</td>
                                        <td className="py-3 px-5">
                                            <div className="flex items-center gap-2.5">
                                                <div className="w-7 h-7 rounded-full bg-teal-50 text-teal-700 font-bold text-[11px] flex items-center justify-center shrink-0">
                                                    {getIniciais(consulta.paciente)}
                                                </div>
                                                <span className="font-semibold text-slate-800">
                                                    {consulta.paciente}
                                                </span>
                                            </div>
                                        </td>
                                        <td className="py-3 px-5">{consulta.profissional}</td>
                                        <td className="py-3 px-5 text-slate-500">{consulta.motivo}</td>
                                        <td className="py-3 px-5">
                                            {renderStatusBadge(consulta.status)}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            <ModalAgendarConsulta isOpen={modalNovaConsulta} onClose={() => setModalNovaConsulta(false)} />
        </div>
    )
}