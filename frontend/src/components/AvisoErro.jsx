// Faixa vermelha com a mensagem de erro de uma ação (ex: "Esta internação já foi encerrada.")
export default function AvisoErro({ mensagem }) {
    if (!mensagem) return null

    return (
        <div className="mb-4 p-3 bg-red-50/70 border border-red-100 rounded-xl text-red-600 text-xs font-semibold">
            {mensagem}
        </div>
    )
}