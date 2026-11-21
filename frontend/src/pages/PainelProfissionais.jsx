import { Search, Plus, Clock, Pencil, ToggleRight, Trash2 } from 'lucide-react'
import { useState } from 'react'


import { useDados } from '../hooks/useDados'
import { useAcao } from '../hooks/useAcao'
import { listarProfissionais, alterarStatusProfissional, excluirProfissional } from '../services/hospitalService'
import { getIniciais } from '../utils/primeiraLetraMaiuscula'
import { rotuloEspecialidade } from '../utils/rotulos'

import LinhaEstado from '../components/LinhaEstado'
import AvisoErro from '../components/AvisoErro'
import ModalProfissional from '../components/modals/ModalProfissional'
import ModalHorarios from '../components/modals/ModalHorarios'
import ModalConfirmarExclusao from '../components/modals/ModalConfirmarExclusao'

export default function PainelProfissionais() {
    // Modal de cadastro/edição: aberto + qual profissional está sendo editado (null = novo)
    const [modalProfissional, setModalProfissional] = useState(false)
    const [profissionalEditando, setProfissionalEditando] = useState(null)

    // Lista real de profissionais, vinda do back end
    const { dados: profissionais, carregando, erro, recarregar } = useDados(listarProfissionais)

    // Guardas só o id e busca o profissional na lista atual
    const [idHorarios, setIdHorarios] = useState(null)
    const profissionalHorarios = profissionais.find((p) => p.id === idHorarios) ?? null

     // Exclusão: guarda qual profissional está sendo excluído (null = modal fechado)
    const [profissionalExcluindo, setProfissionalExcluindo] = useState(null)
    const [excluindo, setExcluindo] = useState(false)
    const [erroExclusao, setErroExclusao] = useState('')

    function fecharExclusao() {
        setProfissionalExcluindo(null)
        setErroExclusao('')
    }

    async function confirmarExclusao() {
        setExcluindo(true)
        setErroExclusao('')
        try {
            await excluirProfissional(profissionalExcluindo.id)
            await recarregar()
            fecharExclusao()
        } catch (e) {
            // Profissional com consultas/internações não pode ser excluído
            setErroExclusao(e.message)
        } finally {
            setExcluindo(false)
        }
    }
    function abrirNovo() {
        setProfissionalEditando(null)
        setModalProfissional(true)
    }

    function abrirEdicao(prof) {
        setProfissionalEditando(prof)
        setModalProfissional(true)
    }
    const { executar, ocupado, erroAcao } = useAcao(recarregar)

    // Ativa ou desativa o profissional
    function alternarStatus(prof) {
        const acao = prof.ativo ? 'desativar' : 'ativar'
        if (window.confirm(`Deseja ${acao} ${prof.nome}?`)) {
            executar(() => alterarStatusProfissional(prof.id, !prof.ativo))
        }
    }
    // Texto digitado na busca (nome ou registro)
    const [busca, setBusca] = useState('')
    const termo = busca.trim().toLowerCase()

    const profissionaisFiltrados = profissionais.filter((p) =>
        p.nome.toLowerCase().includes(termo) ||
        (p.registroProfissional || '').toLowerCase().includes(termo)
    )

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <div>
                    <span className="font-bold tracking-wider text-cyan-800 uppercase">EQUIPE DE PROFISSIONAIS</span>
                </div>

                <button onClick={abrirNovo} className="flex items-center gap-2 bg-cyan-800 hover:bg-cyan-900 text-white font-semibold text-sm px-4 py-2.5 rounded-xl shadow-sm transition-colors cursor-pointer">
                    <Plus className="w-4 h-4" />
                    <span>Novo profissional</span>
                </button>
            </div>

            <AvisoErro mensagem={erroAcao} />
            <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">

                <div className="p-5 border-b border-slate-100 flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                        <h2 className="font-bold text-slate-800 text-base">Lista de profissionais</h2>
                        <p className="text-xs text-slate-400 mt-0.5">{profissionais.length} profissionais cadastrados</p>
                    </div>

                    <div className="relative w-full md:w-80">
                        <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
                        <input
                            type="text"
                            value={busca}
                            onChange={(e) => setBusca(e.target.value)}
                            placeholder="Buscar por nome ou registro"
                            className="w-full pl-9 pr-3 py-2 border border-slate-200 rounded-xl text-xs text-slate-800 focus:outline-none focus:border-cyan-700 transition-colors placeholder:text-slate-400"/>
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
                            <LinhaEstado
                                colunas={7}
                                carregando={carregando}
                                erro={erro}
                                quantidade={profissionaisFiltrados.length}
                                vazio={profissionais.length === 0 ? 'Nenhum profissional cadastrado.' : 'Nenhum profissional encontrado para essa busca.'}/>
                            {profissionaisFiltrados.map((prof) => (
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
                                        {prof.registroProfissional}
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">
                                        {rotuloEspecialidade(prof.especialidade)}
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">
                                        {prof.telefone}
                                    </td>
                                    <td className="py-3.5 px-5 text-slate-600">
                                        {prof.email}
                                    </td>
                                    <td className="py-3.5 px-5">
                                        {prof.ativo ? (
                                            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-semibold bg-emerald-50 text-emerald-700">
                                                <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>Ativo</span>
                                        ) : (
                                            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-semibold bg-slate-100 text-slate-500">
                                                <span className="w-1.5 h-1.5 rounded-full bg-slate-400"></span>Inativo</span>
                                        )}
                                    </td>
                                    <td className="py-3.5 px-5">
                                        <div className="flex items-center justify-end gap-2 text-slate-400">
                                            <button onClick={() => setIdHorarios(prof.id)} className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Horários de atendimento">
                                                <Clock className="w-4 h-4" />
                                            </button>
                                            <button onClick={() => abrirEdicao(prof)} className="hover:text-slate-700 p-1 transition-colors cursor-pointer" title="Editar">
                                                <Pencil className="w-4 h-4" />
                                            </button>
                                            <button onClick={() => alternarStatus(prof)} disabled={ocupado} className="hover:text-slate-700 p-1 transition-colors cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed" title={prof.ativo ? 'Desativar' : 'Ativar'}>
                                                <ToggleRight className="w-4 h-4" />
                                            </button>
                                            <button onClick={() => setProfissionalExcluindo(prof)} className="hover:text-red-600 p-1 transition-colors cursor-pointer" title="Excluir">
                                                <Trash2 className="w-4 h-4" />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
                
            <ModalProfissional key={profissionalEditando?.id ?? 'novo'} isOpen={modalProfissional} onClose={() => setModalProfissional(false)} onSalvo={recarregar} profissional={profissionalEditando}/>
            <ModalHorarios isOpen={profissionalHorarios !== null} onClose={() => setIdHorarios(null)} onSalvo={recarregar} profissional={profissionalHorarios}/>
            <ModalConfirmarExclusao isOpen={profissionalExcluindo !== null} onClose={fecharExclusao} onConfirm={confirmarExclusao} titulo="Excluir profissional?" mensagem={`Você tem certeza que deseja excluir ${profissionalExcluindo?.nome ?? 'este profissional'}?`} erro={erroExclusao} excluindo={excluindo}/>
        </div>
    )
}