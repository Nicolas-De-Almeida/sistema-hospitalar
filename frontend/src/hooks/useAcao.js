import { useState } from 'react'

// Executa uma ação e se der certo, recarrega a lista.
// Se o back end recusar (regra de negócio), guarda a mensagem em "erroAcao".
export function useAcao(recarregar) {
    const [ocupado, setOcupado] = useState(false)
    const [erroAcao, setErroAcao] = useState('')

    async function executar(acao) {
        setOcupado(true)
        setErroAcao('')
        try {
            await acao()
            await recarregar()
        } catch (e) {
            setErroAcao(e.message)
        } finally {
            setOcupado(false)
        }
    }

    return { executar, ocupado, erroAcao }
}