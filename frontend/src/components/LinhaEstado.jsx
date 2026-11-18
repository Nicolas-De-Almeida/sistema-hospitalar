// Linha de tabela que mostra "Carregando...", o erro ou "lista vazia".
// Devolve null quando há dados para mostrar.
export default function LinhaEstado({ colunas, carregando, erro, vazio, quantidade }) {
    let mensagem = null
    let cor = 'text-slate-500'

    if (carregando) {
        mensagem = 'Carregando...'
    } else if (erro) {
        mensagem = erro
        cor = 'text-red-600'
    } else if (quantidade === 0) {
        mensagem = vazio
    }

    if (!mensagem) return null

    return (
        <tr>
            <td colSpan={colunas} className={`py-10 text-center text-sm ${cor}`}>
                {mensagem}
            </td>
        </tr>
    )
}