import { X } from 'lucide-react'

export default function ModalNovoProfissional({ isOpen, onClose }) {
    if (!isOpen) return null

    return (
        <div className="fixed inset-0 bg-slate-900/50 flex items-center justify-center z-50 p-4">
            
            <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg overflow-hidden flex flex-col">
                
                <div className="flex items-start justify-between p-6 border-b border-slate-100">
                    <div>
                        <h2 className="text-xs font-bold tracking-widest text-cyan-800 uppercase">NOVO PROFISSIONAL</h2>
                        <p className="text-xs text-slate-400 mt-0.5">Preencha as informações para iniciar o cadastro.</p>
                    </div>
                    <button onClick={onClose} className="text-slate-400 hover:text-slate-600 transition-colors cursor-pointer p-1">
                        <X className="w-5 h-5" />
                    </button>
                </div>

                <div className="p-6 overflow-y-auto">
                    
                    <div className="flex gap-3 mb-6">
                        <div>
                            <h3 className="font-semibold text-slate-800">Dados profissionais</h3>
                            <p className="text-sm text-slate-500">Informações de identificação, registro e contato.</p>
                        </div>
                    </div>

                    <form className="space-y-4">
                        
                        <div>
                            <label className="block text-sm font-semibold text-slate-700 mb-1.5">Nome completo <span className="text-red-500">*</span></label>
                            <input type="text" placeholder="Ex.: Dr. Carlos Lima"className="w-full px-3 py-2.5 border border-slate-200 rounded-lg text-sm text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600 transition-colors"/>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-semibold text-slate-700 mb-1.5">Registro profissional <span className="text-red-500">*</span></label>
                                <input type="text" placeholder="Ex.: CRM-MG 12345"className="w-full px-3 py-2.5 border border-slate-200 rounded-lg text-sm text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600 transition-colors"/>
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-slate-700 mb-1.5">Especialidade <span className="text-red-500">*</span></label>
                                <input type="text" placeholder="Ex.: Cardiologia"className="w-full px-3 py-2.5 border border-slate-200 rounded-lg text-sm text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600 transition-colors"/>
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-semibold text-slate-700 mb-1.5">Telefone <span className="text-red-500">*</span></label>
                                <input type="text" placeholder="(00) 00000-0000"className="w-full px-3 py-2.5 border border-slate-200 rounded-lg text-sm text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600 transition-colors"/>
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-slate-700 mb-1.5">E-mail <span className="text-red-500">*</span></label>
                                <input type="email" placeholder="nome@hospital.com"className="w-full px-3 py-2.5 border border-slate-200 rounded-lg text-sm text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600 transition-colors"/>
                            </div>
                        </div>

                    </form>
                </div>

                <div className="p-5 border-t border-slate-100 bg-slate-50/50 flex items-center justify-between">
                    <button onClick={onClose} className="px-5 py-2.5 text-sm font-semibold text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors cursor-pointer">Cancelar</button>
                    <button className="px-5 py-2.5 text-sm font-semibold text-white bg-teal-700 rounded-lg hover:bg-teal-800 shadow-sm transition-colors cursor-pointer">Salvar</button>
                </div>
            </div>
        </div>
    )
}