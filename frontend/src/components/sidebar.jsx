import LogoHospital from '../assets/img/logo-hospital.png'
import { NavLink } from 'react-router-dom'
import { Home, Users, Stethoscope, Calendar, Bed, LayoutGrid, Hospital, Siren, Scissors } from 'lucide-react'

// Lista dos itens do menu. Para criar um novo item, basta adicionar uma linha aqui.
const ITENS_MENU = [
    { para: '/', texto: 'Início', Icone: Home },
    { para: '/pacientes', texto: 'Pacientes', Icone: Users },
    { para: '/profissionais', texto: 'Profissionais', Icone: Stethoscope },
    { para: '/consultas', texto: 'Consultas', Icone: Calendar },
    { para: '/internacoes', texto: 'Internações', Icone: Bed },
    { para: '/quartos', texto: 'Quartos', Icone: LayoutGrid },
    { para: '/emergencias', texto: 'Emergências', Icone: Siren },
    { para: '/procedimentos', texto: 'Cirurgias', Icone: Scissors },
]

// Classe de cada item. O NavLink diz sozinho se a página dele está aberta (isActive).
// Apenas UM elemento por item, e o hover e o ativo têm exatamente o mesmo tamanho.
const classeItem = ({ isActive }) =>
    `flex items-center justify-center lg:justify-start gap-3 h-11 lg:h-10 rounded-lg px-0 lg:px-3 transition-colors duration-150 ${
        isActive ? 'bg-cyan-800 text-white' : 'text-slate-400 hover:bg-cyan-900 hover:text-white'
    }`

export default function SideBar() {
    return (
        // Telas pequenas: só ícones (w-16). Telas grandes (lg): ícones + texto (w-56).
        <aside className="shrink-0 w-16 lg:w-56 h-screen bg-cyan-950 text-white flex flex-col overflow-y-auto overflow-x-hidden">
            <div className="flex items-center justify-center lg:justify-start h-16 px-0 lg:px-5 border-b border-cyan-900 shrink-0">
                <div className="flex gap-2 items-center">
                    <img className="w-8 h-8 rounded-md border border-slate-600 shrink-0 object-cover" src={LogoHospital} alt="Logo do hospital" />
                    <div className="hidden lg:block whitespace-nowrap">
                        <p className="font-bold leading-none">PUC</p>
                        <p className="text-slate-300 leading-none">Hospital</p>
                    </div>
                </div>
            </div>

            <nav className="flex flex-col gap-1.5 p-2 lg:p-4">
                <p className="hidden lg:block text-slate-400 text-xs font-bold tracking-wider mb-2 px-3">MENU PRINCIPAL</p>

                {ITENS_MENU.map(({ para, texto, Icone }) => (
                    <NavLink key={para} to={para} end={para === '/'} title={texto} className={classeItem}>
                        <Icone className="w-5 h-5 shrink-0" />
                        <span className="hidden lg:inline whitespace-nowrap font-semibold text-sm">{texto}</span>
                    </NavLink>
                ))}
            </nav>

            <div className="flex gap-2 items-center justify-center lg:justify-start mt-auto p-4 border-t border-cyan-900 shrink-0">
                <Hospital className="w-5 h-5 shrink-0" />
                <div className="hidden lg:block whitespace-nowrap">
                    <p className="font-bold leading-none">PUC Minas</p>
                    <p className="text-slate-300 leading-none">Belo Horizonte</p>
                </div>
            </div>
        </aside>
    )
}