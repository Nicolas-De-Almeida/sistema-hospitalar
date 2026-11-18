// Botão pequeno só com ícone, usado na coluna "AÇÕES" das tabelas.
// perigo = true deixa o hover vermelho (cancelar, excluir).
export default function BotaoAcao({ titulo, onClick, desabilitado, perigo, children }) {
    const hover = perigo ? 'hover:text-red-600' : 'hover:text-slate-700'

    return (
        <button
            type="button"
            title={titulo}
            onClick={onClick}
            disabled={desabilitado}
            className={`${hover} p-1 transition-colors cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed`}>
            {children}
        </button>
    )
}