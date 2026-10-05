
import LogoHospital from '../assets/img/logoHospital.png'
import { Link } from 'react-router-dom';
import { Home, Users, Stethoscope, Calendar, Bed, LayoutGrid, Hospital } from 'lucide-react';

export default function SideBar() {
    
    return(
        <div className="text-white h-screen bg-cyan-950 w-56 flex flex-col">
            <div className="flex gap-2 items-center p-5 border-b border-cyan-900">
                <img className="w-8 h-8 rounded-md border border-slate-600" src={LogoHospital} alt="Logo do hospital" />
                <div>
                    <p className="font-bold leading-none">PUC</p>
                    <p className="text-slate-300 leading-none">Hospital</p>
                </div>
            </div>
            <div className="text-slate-400 font-bold p-5">
                <h1>MENU</h1>
                <h1>PRINCIPAL</h1>

                {/*Links de navegação para as outras páginas*/}
                <div className="flex flex-col gap-2 mt-4">
                    <div className="flex items-center gap-3 px-4 py-2 hover:bg-cyan-800 hover:text-white rounded-lg transition-colors">
                        <Home/>
                        <p>Início</p>
                    </div>
                    <div className="flex items-center gap-3 px-4 py-2 hover:bg-cyan-800 hover:text-white rounded-lg transition-colors">
                        <Users/>
                        <p>Pacientes</p>
                    </div>
                    <div className="flex items-center gap-3 px-4 py-2 hover:bg-cyan-800 hover:text-white rounded-lg transition-colors">
                        <Stethoscope/>
                        <p>Profissionais</p>
                    </div>
                    <div className="flex items-center gap-3 px-4 py-2 hover:bg-cyan-800 hover:text-white rounded-lg transition-colors">
                        <Calendar/>
                        <p>Consultas</p>
                    </div>
                    <div className="flex items-center gap-3 px-4 py-2 hover:bg-cyan-800 hover:text-white rounded-lg transition-colors">
                        <Bed/>
                        <p>Internações</p>
                    </div>
                    <div className="flex items-center gap-3 px-4 py-2 hover:bg-cyan-800 hover:text-white rounded-lg transition-colors">
                        <LayoutGrid/>
                        <p>Quartos</p>
                    </div>
                    
                </div>
            </div>
            <div className="flex gap-2 items-center mt-auto p-5 border-t border-cyan-900">
                <Hospital></Hospital>
                <div>
                    <p className="font-bold leading-none">PUC Minas</p>
                    <p className="text-slate-300 leading-none">Belo Horizonte</p>
                </div>
            </div>
        </div>
    )
}