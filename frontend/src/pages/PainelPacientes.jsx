import { Search, Plus, FileText, Calendar, Bed, Pencil, Trash2 } from 'lucide-react'
import { useState } from 'react'

import ModalPaciente from '../components/modals/ModalPaciente'
import ModalProntuario from '../components/modals/ModalProntuario'
import ModalAgendarConsulta from '../components/modals/ModalAgendarConsulta'
import ModalNovaInternacao from '../components/modals/ModalNovaInternacao'
import ModalConfirmarExclusao from '../components/modals/ModalConfirmarExclusao'
import LinhaEstado from '../components/LinhaEstado'

import { useDados } from '../hooks/useDados'
import { listarPacientes, excluirPaciente } from '../services/hospitalService'
import { getIniciais } from '../utils/primeiraLetraMaiuscula'
import { formatarCpf } from '../utils/rotulos'


export default function PainelPacientes() {
    // Modal de cadastro/edição: pacienteEditando = null significa "novo paciente"
    const [modalPaciente, setModalPaciente] = useState(false)
    const [pacienteEditando, setPacienteEditando] = useState(null)

    // Paciente cujo prontuário está aberto (null = fechado)
    const [pacienteProntuario, setPacienteProntuario] = useState(null)

    // Paciente que a pessoa está tentando excluir (null = modal fechado)
    const [pacienteExcluindo, setPacienteExcluindo] = useState(null)
    const [excluindo, setExcluindo] = useState(false)
    const [erroExclusao, setErroExclusao] = useState('')

    // Paciente para quem estamos agendando consulta / criando internação (null = modal fechado)
    const [pacienteConsulta, setPacienteConsulta] = useState(null)
    const [pacienteInternacao, setPacienteInternacao] = useState(null)

    // Lista real de pacientes, vinda do back end
    const { dados: pacientes, carregando, erro, recarregar } = useDados(listarPacientes)

    // Texto digitado na busca (nome ou CPF)
    const [busca, setBusca] = useState('')
    const termo = busca.trim().toLowerCase()
    const termoNumeros = termo.replace(/\D/g, '')

    const pacientesFiltrados = pacientes.filter((p) =>
        p.nome.toLowerCase().includes(termo) ||
        (termoNumeros !== '' && (p.cpf || '').replace(/\D/g, '').includes(termoNumeros))
    )

    function abrirNovo() {
        setPacienteEditando(null)
        setModalPaciente(true)
    }

    function abrirEdicao(paciente) {
        setPacienteEditando(paciente)
        setModalPaciente(true)
    }

    function fecharExclusao() {
        setPacienteExcluindo(null)
        setErroExclusao('')
    }

    async function confirmarExclusao() {
        setExcluindo(true)
        setErroExclusao('')
        try {
            await excluirPaciente(pacienteExcluindo.id)
            await recarregar()
            fecharExclusao()
        } catch (e) {
            // Ex: paciente com consultas/internações não pode ser excluído
            setErroExclusao(e.message)
        } finally {
            setExcluindo(false)
        }
    }

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <div>
                    <span className="font-bold tracking-wider text-cyan-800 uppercase">GESTÃO DE CADASTROS</span>
                </div>

                <button onClick={abrirNovo} className="flex items-center gap-2 bg-cyan-800 hover:bg-cyan-900 text-white font-semibold text-sm px-4 py-2.5 rounded-xl shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4" />
                    <span>Novo paciente</span>
                </button>
            </div>

            <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
                <div className="p-5 border-b border-slate-100 flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h2 className="font-bold text-slate-800 text-base">Lista de pacientes</h2>
                        <p className="text-xs text-slate-400 mt-0.5">{pacientes.length} pacientes cadastrados</p>
                    </div>
                    <div className="relative w-full md:w-80">
                        <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2"/>
                        <input
                            type="text"
                            value={busca}
                            onChange={(e) => setBusca(e.target.value)}
                            placeholder="Buscar por nome ou CPF"
                            className="w-full pl-9 pr-3 py-2 border border-slate-200 rounded-xl text-xs text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors placeholder:text-slate-400"/>
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
                            <LinhaEstado
                                colunas={5}
                                carregando={carregando}
                                erro={erro}
                                quantidade={pacientesFiltrados.length}
                                vazio={pacientes.length === 0 ? 'Nenhum paciente cadastrado.' : 'Nenhum paciente encontrado para essa busca.'}/>
                            {pacientesFiltrados.map((paciente) => (
                                <tr key={paciente.id} className="hover:bg-slate-50/60 transition-colors">
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-lg bg-teal-50 text-teal-800 font-bold text-xs flex items-center justify-center shrink-0">
                                                {getIniciais(paciente.nome)}
                                            </div>
                                            <span className="font-bold text-slate-800 text-xs">
                                                {paciente.nome}
                                            </span>
                                        </div>
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600 font-medium">
                                        {formatarCpf(paciente.cpf)}
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">
                                        {paciente.telefone}
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">
                                        {paciente.email}
                                    </td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center justify-end gap-2 text-slate-400">
                                            <button onClick={() => setPacienteProntuario(paciente)} className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Prontuário">
                                                <FileText className="w-4 h-4"/>
                                            </button>
                                            <button onClick={() => setPacienteConsulta(paciente)} className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Agendar Consulta">
                                                <Calendar className="w-4 h-4"/>
                                            </button>
                                            <button onClick={() => setPacienteInternacao(paciente)} className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Nova Internação">
                                                <Bed className="w-4 h-4"/>
                                            </button>
                                            <span className="text-slate-200">|</span>
                                            <button onClick={() => abrirEdicao(paciente)} className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Editar">
                                                <Pencil className="w-4 h-4"/>
                                            </button>
                                            <button onClick={() => setPacienteExcluindo(paciente)} className="hover:text-red-600 p-1 transition-colors cursor-pointer" title="Excluir">
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
            <ModalPaciente
                key={pacienteEditando?.id ?? 'novo'}
                isOpen={modalPaciente}
                onClose={() => setModalPaciente(false)}
                onSalvo={recarregar}
                paciente={pacienteEditando}/>

            <ModalProntuario isOpen={pacienteProntuario !== null} onClose={() => setPacienteProntuario(null)} paciente={pacienteProntuario} />

            <ModalAgendarConsulta isOpen={pacienteConsulta !== null} onClose={() => setPacienteConsulta(null)} paciente={pacienteConsulta} />

            <ModalNovaInternacao isOpen={pacienteInternacao !== null} onClose={() => setPacienteInternacao(null)} paciente={pacienteInternacao} />

            <ModalConfirmarExclusao
                isOpen={pacienteExcluindo !== null}
                onClose={fecharExclusao}
                onConfirm={confirmarExclusao}
                titulo="Excluir paciente?"
                mensagem={`Você tem certeza que deseja excluir ${pacienteExcluindo?.nome ?? 'este paciente'}?`}
                erro={erroExclusao}
                excluindo={excluindo}/>
        </div>

    )
}