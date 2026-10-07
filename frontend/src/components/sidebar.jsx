
import LogoHospital from '../assets/img/logo-hospital.png'
import { Link, useLocation } from 'react-router-dom'
import { Home, Users, Stethoscope, Calendar, Bed, LayoutGrid, Hospital } from 'lucide-react';

export default function SideBar() {
    const location = useLocation();
    
    const getLinkClass = (path) => {
        const isActive = location.pathname === path;
        return `-ml-1.5 lg:ml-0 flex items-center gap-5 pl-2 w-10 h-10 lg:w-full lg:h-auto mx-auto lg:mx-0 lg:px-3 lg:py-2.5 rounded-xl lg:rounded-lg transition-colors cursor-pointer ${
            isActive ? 'bg-cyan-800 text-white' : 'hover:bg-cyan-800 hover:text-white'
        }`;
    };
    
    return(
        <div className="text-white h-screen bg-cyan-950 w-16 lg:w-56 transition-[width] duration-300 ease-in-out flex overflow-hidden flex-col">
            <div className="flex items-center justify-start lg: p-5 border-b border-cyan-900 justify-center pl-4 lg:justify-start">
                <div className="flex gap-2 items-center">
                    <img className="w-8 h-8 rounded-md border border-slate-600 shrink-0 object-cover" src={LogoHospital} alt="Logo do hospital" />
                    <div className="hidden md:block whitespace-nowrap">
                        <p className="hidden lg:block font-bold leading-none">PUC</p>
                        <p className="hidden lg:block text-slate-300 leading-none">Hospital</p>
                    </div>
                </div>
            </div>
            <div className="text-slate-400 font-bold p-5 tracking-wider">
                <div className="hidden lg:block">
                    <h1>MENU</h1>
                    <h1>PRINCIPAL</h1>
                </div>

                {/*Links de navegação para as outras páginas*/}
                <div className="flex flex-col gap-2 mt-4">
                    <div className="-ml-1.5 lg:ml-0 flex items-center gap-5 pl-2 w-10 h-10 lg:w-full lg:h-auto mx-auto lg:mx-0 lg:px-3 lg:py-2.5 hover:bg-cyan-800 hover:text-white rounded-xl lg:rounded-lg transition-colors cursor-pointer">
                        <Link to="/" className={getLinkClass('/')} >
                            <Home className="shrink-0"/>
                            <p className="md:block">Início</p>
                        </Link>
                    </div>
                    <div className="-ml-1.5 lg:ml-0 flex items-center gap-5 pl-2 w-10 h-10 lg:w-full lg:h-auto mx-auto lg:mx-0 lg:px-3 lg:py-2.5 hover:bg-cyan-800 hover:text-white rounded-xl lg:rounded-lg transition-colors cursor-pointer">
                        <Link to="/pacientes" className={getLinkClass('/pacientes')}>
                            <Users className="shrink-0"/>
                            <p className="md:block">Pacientes</p>
                        </Link>
                    </div>
                    <div className="-ml-1.5 lg:ml-0 flex items-center gap-5 pl-2 w-10 h-10 lg:w-full lg:h-auto mx-auto lg:mx-0 lg:px-3 lg:py-2.5 hover:bg-cyan-800 hover:text-white rounded-xl lg:rounded-lg transition-colors cursor-pointer">
                        <Link to="/profissionais" className={getLinkClass('/profissionais')}>
                            <Stethoscope className="shrink-0"/>
                            <p className="md:block">Profissionais</p>
                        </Link>
                    </div>
                    <div className="-ml-1.5 lg:ml-0 flex items-center gap-5 pl-2 w-10 h-10 lg:w-full lg:h-auto mx-auto lg:mx-0 lg:px-3 lg:py-2.5 hover:bg-cyan-800 hover:text-white rounded-xl lg:rounded-lg transition-colors cursor-pointer">
                        <Link to="/consultas" className={getLinkClass('/consultas')}>
                            <Calendar className="shrink-0"/>
                            <p className="md:block">Consultas</p>
                        </Link>
                    </div>
                    <div className="-ml-1.5 lg:ml-0 flex items-center gap-5 pl-2 w-10 h-10 lg:w-full lg:h-auto mx-auto lg:mx-0 lg:px-3 lg:py-2.5 hover:bg-cyan-800 hover:text-white rounded-xl lg:rounded-lg transition-colors cursor-pointer">
                        <Link to="/internacoes" className={getLinkClass('/internacoes')}>
                            <Bed className="shrink-0"/>
                            <p className="md:block">Internações</p>
                        </Link>
                    </div>
                    <div className="-ml-1.5 lg:ml-0 flex items-center gap-5 pl-2 w-10 h-10 lg:w-full lg:h-auto mx-auto lg:mx-0 lg:px-3 lg:py-2.5 hover:bg-cyan-800 hover:text-white rounded-xl lg:rounded-lg transition-colors cursor-pointer">
                        <Link to="/quartos" className={getLinkClass('/quartos')}>
                            <LayoutGrid className="shrink-0"/>
                            <p className="md:block">Quartos</p>
                        </Link>
                    </div>
                    
                </div>
            </div>
            <div className="flex gap-2 items-center mt-auto p-4 border-t border-cyan-900 justify-center md:justify-start">
                <Hospital className="shrink-0"/>
                <div className="hidden lg:block">
                    <p className="font-bold leading-none">PUC Minas</p>
                    <p className="text-slate-300 leading-none">Belo Horizonte</p>
                </div>
            </div>
        </div>
    )
}