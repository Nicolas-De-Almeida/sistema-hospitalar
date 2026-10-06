import { X } from 'lucide-react';

export default function ModalNovoPaciente({ isOpen, onClose }) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-2xl w-full max-w-2xl shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
                <div className="flex items-start justify-between p-6 border-b border-slate-100">
                    <div>
                        <span className="text-xs font-bold tracking-wider text-teal-700 uppercase">NOVO CADASTRO</span>
                        <h2 className="text-xl font-bold text-slate-800 mt-0.5">Novo paciente</h2>
                        <p className="text-xs text-slate-400 mt-0.5">Preencha as informações para iniciar o cadastro.</p>
                    </div>
                    <button onClick={onClose} className="text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                        <X className="w-5 h-5"/>
                    </button>
                </div>

                <form onSubmit={(e) => e.preventDefault()} className="overflow-y-auto flex-1">
                    <div className="p-6 space-y-6">
                        <div>
                            <div className="flex items-center gap-2 mb-3">
                                <span className="bg-teal-50 text-teal-700 font-bold text-xs px-2 py-0.5 rounded-md">01</span>
                                <div>
                                    <h3 className="text-sm font-bold text-slate-800">Dados pessoais</h3>
                                    <p className="text-xs text-slate-400">Informações de identificação e contato.</p>
                                </div>
                            </div>

                            <div className="space-y-3">
                                <div>
                                    <p className="block text-xs font-semibold text-slate-700 mb-1">Nome completo <span className="text-red-500">*</span></p>
                                    <input type="text" placeholder="Nome do paciente" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">CPF <span className="text-red-500">*</span></label>
                                        <input type="text" placeholder="000.000.000-00" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Data de nascimento <span className="text-red-500">*</span></label>
                                        <input type="text" placeholder="DD / MM / AAAA" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                    </div>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Telefone <span className="text-red-500">*</span></label>
                                        <input type="text" placeholder="(00) 00000-0000" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">E-mail</label>
                                        <input type="email" placeholder="email@exemplo.com" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div>
                            <div className="flex items-center gap-2 mb-3">
                                <span className="bg-teal-50 text-teal-700 font-bold text-xs px-2 py-0.5 rounded-md">02</span>
                                <div>
                                    <h3 className="text-sm font-bold text-slate-800">Endereço</h3>
                                    <p className="text-xs text-slate-400">Localização residencial do paciente.</p>
                                </div>
                            </div>

                            <div className="space-y-3">
                                <div className="grid grid-cols-1 md:grid-cols-6 gap-3">
                                    <div className="md:col-span-2">
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">CEP</label>
                                        <input type="text" placeholder="00000-000" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                    </div>
                                    <div className="md:col-span-3">
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Logradouro</label>
                                        <input type="text" placeholder="Rua, avenida..." className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                    </div>
                                    <div className="md:col-span-1">
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Número</label>
                                        <input type="text" placeholder="Nº" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                    </div>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Complemento</label>
                                        <input type="text" placeholder="Apto, bloco..." className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Bairro</label>
                                        <input type="text" placeholder="Bairro" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-semibold text-slate-700 mb-1">Cidade</label>
                                        <input type="text" placeholder="Cidade" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                    </div>
                                </div>

                                <div className="w-full md:w-1/3">
                                    <label className="block text-xs font-semibold text-slate-700 mb-1">UF</label>
                                    <input type="text" placeholder="UF" className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"/>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="flex items-center justify-between p-4 bg-slate-50 border-t border-slate-100">
                        <button type="button"onClick={onClose}className="px-4 py-2 border border-slate-200 rounded-xl text-slate-600 hover:bg-white text-sm font-semibold transition-colors cursor-pointer">Cancelar</button>
                        <button type="submit"className="px-5 py-2 bg-teal-800 hover:bg-teal-900 text-white rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm">Salvar</button>
                    </div>
                </form>
            </div>
        </div>
    );
}