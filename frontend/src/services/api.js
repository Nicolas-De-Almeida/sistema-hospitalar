// Função única para falar com o back end.
// Todo o resto do front usa ela, então, se um dia o endereço ou o token mudar, só esse arquivo precisa ser alterado.

export async function api(caminho, opcoes = {}) {
    let resposta

    try {
        resposta = await fetch(`/api${caminho}`, {
            headers: { 'Content-Type': 'application/json' },
            ...opcoes,
        })
    } catch {
        throw new Error('Não foi possível conectar ao servidor. Verifique se o back end está rodando.')
    }

    // Respostas sem corpo (ex: 999) não têm JSON para ler
    const dados = await resposta.json().catch(() => null)

    if (!resposta.ok) {
        // O back end devolve os erros no formato { "mensagem": "bla bla bla" }
        throw new Error(dados?.mensagem || 'Erro ao comunicar com o servidor.')
    }

    return dados
}