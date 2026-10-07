import { X, Calendar } from 'lucide-react';

export default function ModalAgendarConsulta({ isOpen, onClose }) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden flex flex-col">
                <div className="p-6 pb-2">
                    <div className="flex items-start justify-between mb-3">
                        <div className="bg-teal-50 text-teal-800 p-2.5 rounded-xl border border-teal-100">
                            <Calendar className="w-6 h-6"/>
                        </div>
                        <button onClick={onClose}className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                            <X className="w-5 h-5"/>
                        </button>
                    </div>
                    <h2 className="text-xl font-bold text-slate-800">Agendar consulta</h2>
                    <p className="text-xs text-slate-400 mt-1">Preencha as informações para iniciar o cadastro.</p>
                </div>

                <form onSubmit={(e) => e.preventDefault()} className="p-6 pt-4 space-y-4">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-semibold text-slate-700 mb-1">CPF do paciente <span className="text-red-500">*</span></label>
                            <input type="text" placeholder="000.000.000-00" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                        </div>
                        <div>
                            <label className="block text-xs font-semibold text-slate-700 mb-1">Registro profissional <span className="text-red-500">*</span></label>
                            <input type="text" placeholder="Ex: CRM/MG 12345" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-semibold text-slate-700 mb-1">Data da consulta <span className="text-red-500">*</span></label>
                            <input type="date" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors text-slate-700"/>
                        </div>
                        <div>
                            <label className="block text-xs font-semibold text-slate-700 mb-1">Horário <span className="text-red-500">*</span></label>
                            <input type="time"  className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors text-slate-700"/>
                        </div>
                    </div>

                    <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100 mt-6">
                        <button type="button" onClick={onClose} className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 text-sm font-semibold transition-colors cursor-pointer">
                            Cancelar
                        </button>
                        <button type="submit" className="px-5 py-2 bg-cyan-800 hover:bg-cyan-950 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm">Confirmar</button>
                    </div>
                </form>
            </div>
        </div>
    );
}