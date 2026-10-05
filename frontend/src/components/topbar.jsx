import { useLocation } from "react-router-dom";
import { useState } from 'react';
import { Search } from 'lucide-react';

export default function TopBar() {
    const usuarioLogado = {
        nome: "Cleitin Santana",
        funcao: "Administrador"
    };
    const iniciais = getIniciais(usuarioLogado.nome);

    const [busca, setBusca] = useState('');
    const location = useLocation();

    const paginas = {
        '/': 'PAINEL PRINCIPAL',
        '/pacientes': 'PACIENTES',
        '/profissionais': 'PROFISSIONAIS',
        '/consultas': 'CONSULTAS',
        '/internacoes': 'INTERNAÇÕES',
        '/quartos': 'QUARTOS',
        '/historico': 'HISTÓRICO',
    };

    const paginaAtual = paginas[location.pathname] || 'PAINEL PRINCIPAL';

    return (
        <header className="flex items-center justify-between p-4 h-15 border-b border-slate-100 bg-white gap-2">

            {/* Título da páginas */}
            <div className="shrink-0">
                <p className="text-cyan-800 text-base sm:text-lg font-bold truncate">
                    {paginaAtual}
                </p>
            </div>

            <div className="flex items-center gap-3 sm:gap-6">
                
                {/* Barra de pesquisa */}
                <div className="pr-3 sm:pr-6 border-r border-slate-200">
                    <div className="flex items-center gap-2 bg-slate-100 rounded-xl w-32 sm:w-60 md:w-80 h-10 p-3 border border-slate-200 focus-within:border-cyan-800 transition-all">
                        <Search className="w-4 h-4 text-slate-400 shrink-0" />
                        <input 
                            className="w-full bg-transparent focus:outline-none text-sm text-slate-600 placeholder:text-slate-400" 
                            type="text" 
                            value={busca} 
                            onChange={(e) => setBusca(e.target.value)} 
                            placeholder="Buscar por nome..."
                        />
                    </div>
                </div>

                {/* Perfil do usuario */}
                <div className="flex gap-3 items-center shrink-0">
                    <div className="w-9 h-9 rounded-full border border-emerald-200 bg-emerald-100 flex items-center justify-center">
                        <span className="text-teal-800 text-sm font-bold">{iniciais}</span>
                    </div>
                    
                    {/* Esconde o texto em telas muito pequenas para poupar espaço */}
                    <div className="hidden sm:block text-sm">
                        <p className="font-bold text-slate-800 leading-none">{usuarioLogado.nome}</p>
                        <p className="text-xs text-slate-500 leading-none mt-1">{usuarioLogado.funcao}</p>
                    </div>
                </div>

            </div>
        </header>
    );
}

{/*Permite pegar a primeira letra do nome e sobrenome do usuário logado para usar como "foto" de perfil*/}
function getIniciais(nomeCompleto) {
  if (!nomeCompleto) return '';
  const nomes = nomeCompleto.trim().split(' ');
  if (nomes.length === 1) return nomes[0][0].toUpperCase();
  return (nomes[0][0] + nomes[nomes.length - 1][0]).toUpperCase();
}