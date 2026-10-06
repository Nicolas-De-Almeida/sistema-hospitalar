import { Trash2, X, AlertTriangle } from 'lucide-react';

export default function ModalExcluir({ isOpen, onClose, onConfirm }) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
            <div className="fixed inset-0" onClick={onClose} />

            <div className="relative w-full max-w-md bg-white rounded-2xl shadow-xl border border-slate-100 p-6 z-10 animate-in fade-in zoom-in-95 duration-150">
                
                <button onClick={onClose} className="absolute top-5 right-5 text-slate-400 hover:text-slate-600 p-1 rounded-lg transition-colors cursor-pointer">
                    <X className="w-5 h-5"/>
                </button>

                <div className="w-10 h-10 rounded-xl bg-red-50 flex items-center justify-center mb-4">
                    <Trash2 className="w-5 h-5 text-red-500"/>
                </div>

                <h2 className="text-lg font-bold text-slate-800">Excluir Informação?</h2>
                
                <p className="text-xs text-slate-500 mt-1 mb-4 leading-relaxed">Você tem certeza que deseja excluir?</p>

                <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl flex items-center gap-2.5 text-red-600 text-xs font-semibold mb-6">
                    <AlertTriangle className="w-4 h-4 shrink-0 text-red-500"/>
                    <span>Esta ação não pode ser desfeita.</span>
                </div>

                <div className="pt-4 border-t border-slate-100 flex items-center justify-between gap-3">
                    <button type="button" onClick={onClose} className="px-4 py-2 border border-slate-200 hover:bg-slate-50 text-slate-700 font-semibold text-xs rounded-xl transition-colors cursor-pointer">Cancelar</button>

                    <button type="button" onClick={onConfirm}className="px-5 py-2 bg-red-600 hover:bg-red-700 text-white font-semibold text-xs rounded-xl transition-colors shadow-xs cursor-pointer">Excluir</button>
                </div>

            </div>
        </div>
    );
}