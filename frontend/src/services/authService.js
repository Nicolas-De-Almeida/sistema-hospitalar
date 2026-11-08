// Fala com o back end e guarda quem está logado.
// Por enquanto o usuário fica no localStorage. Quando o back end passar a
// devolver um token (JWT), é aqui que ele vai ser guardado também.

const CHAVE = 'usuarioLogado'

export async function login(email, senha) {
    let resposta

    try {
        resposta = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha }),
        })
    } catch {
        throw new Error('Não foi possível conectar ao servidor. Verifique se o back end está rodando.')
    }

    const dados = await resposta.json().catch(() => ({}))

    if (!resposta.ok) {
        throw new Error(dados.mensagem || 'Erro ao fazer login.')
    }

    localStorage.setItem(CHAVE, JSON.stringify(dados))
    return dados
}

export function logout() {
    localStorage.removeItem(CHAVE)
}

export function getUsuarioLogado() {
    try {
        const texto = localStorage.getItem(CHAVE)
        return texto ? JSON.parse(texto) : null
    } catch {
        return null
    }
}

export function estaLogado() {
    return getUsuarioLogado() !== null
}
