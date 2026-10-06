import { CalendarDays, Bed, DoorOpen, ArrowBigRight, UserPlus, CalendarPlus, Plus } from 'lucide-react'
import { PegarDataCalendario } from '../utils/Calendario'
import { useState } from 'react';

import CardsTeste from '../data/cardsTeste.json'
import ModalNovoPaciente from '../components/modals/ModalNovoPaciente'
import ModalAgendarConsulta from '../components/modals/ModalAgendarConsulta'
import ModalNovaInternacao from '../components/modals/ModalNovaInternacao'


export default function PainelPrincipal() {
    const dataAtual = PegarDataCalendario();

    const[modalPacienteAberto, setModalNovoPacienteAberto] = useState(false)
    const[modalAgendarConsultaAberto, setModalAgendarConsultaAberto] = useState(false)
    const[modalNovaInternacaoAberto, setModalNovaInternacaoAberto] = useState(false)


    return (
        <div>
            <div className='flex items-center justify-between mb-5'>
                <div className="flex flex-col">
                    <div className="font-bold">
                        <p>Visão geral</p>
                    </div>
                    <p className="text-slate-400">Acompanhamento dos atendimentos e ocopações da unidade.</p>
                </div>
                <div className="bg-white border border-slate-200 rounded-xl p-3 px-5 shadow-sm inline-flex items-center gap-3">
                    <CalendarDays className="w-5 h-5 text-cyan-800" />
                    <p className="text-cyan-950 font-bold text-lg">{dataAtual}</p>
                </div>
            </div>

            <div className='grid grid-cols-3 gap-5'>
                {/* Isso aqui é um exemplo de como vai parecer quando fizermos o backend */}
                {/* Card1: consulta, Card2: Internações ativas, Card3: Quartos disponíveis */}
                <span className="flex flex-col gap-1 border border-slate-200 shadow-sm rounded-lg p-5">
                    <div className="flex justify-between">
                        <CalendarDays className="bg-teal-200 text-cyan-800 rounded-lg p-1" />
                        <ArrowBigRight className="text-slate-300" />
                    </div>
                    <div className="font-bold flex flex-col gap-1">
                        <h1 className="text-lg">18</h1>
                        <p className="text-sm">Consultas hoje</p>
                        <p className="text-slate-400 text-sm">5 agendadas, 12 realizadas</p>
                    </div>
                </span>

                <span className="border border-slate-200 shadow-sm rounded-lg p-5">
                    <div className="flex justify-between">
                        <Bed className="bg-blue-200 text-blue-800 rounded-lg p-1" />
                        <ArrowBigRight className="text-slate-300" />
                    </div>
                    <div className="font-bold flex flex-col gap-1">
                        <h1 className="text-lg">24</h1>
                        <p className="text-sm">Internações ativas</p>
                        <p className="text-slate-400 text-sm">Em acompanhamento</p>
                    </div>
                </span>

                <span className="border border-slate-200 shadow-sm rounded-lg p-5">
                    <div className="flex justify-between">
                        <DoorOpen className="bg-green-200 text-green-800 rounded-lg p-1" />
                        <ArrowBigRight className="text-slate-300" />
                    </div>
                    <div className="font-bold flex flex-col gap-1">
                        <h1><span className="font-bold text-lg">9</span> <span className="text-slate-400">de 40</span></h1>
                        <p className="text-sm">Quartos disponíveis</p>
                        <p className="text-slate-400 text-sm">31 quartos ocupados no momento</p>
                    </div>
                </span>
            </div>

            <div className="flex justify-between mt-8">
                <div className="mb-4">
                    <p className="font-bold text-slate-700">Ações rápidas</p>
                </div>

                <div className="flex flex-wrap gap-4">
                    <button onClick={() => setModalNovoPacienteAberto(true)} className="flex items-center text-white w-48 h-9 font-bold bg-cyan-800 hover:bg-cyan-900 rounded-lg p-2 gap-2 cursor-pointer transition-colors">
                        <UserPlus className="w-4 h-4"/>
                        <span>Novo paciente</span>
                        <Plus className="w-4 h-4 ml-auto"/>
                    </button>

                    <button onClick={() => setModalAgendarConsultaAberto(true)} className="flex items-center text-white w-51 h-9 font-bold bg-cyan-800 hover:bg-cyan-900 rounded-lg p-2 gap-2 cursor-pointer transition-colors">
                        <CalendarPlus className="w-4 h-4"/>
                        <span>Agendar consulta</span>
                        <Plus className="w-4 h-4 ml-auto"/>
                    </button>

                    <button onClick={() => setModalNovaInternacaoAberto(true)} className="flex items-center text-white w-48 h-9 font-bold bg-cyan-800 hover:bg-cyan-900 rounded-lg p-2 gap-2 cursor-pointer transition-colors">
                        <Bed className="w-4 h-4"/>
                        <span>Nova internação</span>
                        <Plus className="w-4 h-4 ml-auto"/>
                    </button>
                </div>
            </div>
            {/*Tabela de exemplo para comos os dados vão ser mostrados */}
            <div className="border border-slate-200 rounded-lg shadow-sm mt-5 p-5">
                <table className="w-full text-left text-sm">
                    <thead>
                        <tr className="text-xs text-slate-400 border-b border-slate-100">
                            <th className="py-2">Horário</th>
                            <th className="py-2">Paciente</th>
                            <th className="py-2">Profissional</th>
                            <th className="py-2">Motivo</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                        {CardsTeste.map((item) => (
                            <tr key={item.id}>
                                <td className="py-3 font-bold text-slate-800">{item.horario}</td>
                                <td className="py-3 font-semibold text-slate-800">{item.paciente}</td>
                                <td className="py-3">
                                    <div className="font-semibold text-slate-800">{item.profissional}</div>
                                    <div className="text-xs text-slate-400">{item.especialidade}</div>
                                </td>
                                <td className="py-3 text-slate-600">{item.motivo}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <ModalNovoPaciente isOpen={modalPacienteAberto} onClose={() => setModalNovoPacienteAberto(false)}/>
            <ModalAgendarConsulta isOpen={modalAgendarConsultaAberto} onClose={() => setModalAgendarConsultaAberto(false)}/>
            <ModalNovaInternacao isOpen={modalNovaInternacaoAberto} onClose={() => setModalNovaInternacaoAberto(false)}/>
        </div>
    )
}