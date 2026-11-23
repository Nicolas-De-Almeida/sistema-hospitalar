import { useEffect, useState } from 'react'

// Igual ao useDados, mas só busca quando "aberto" fica true (ex: quando um modal abre).
// Assim a lista está sempre atualizada (um quarto que acabou de lotar não aparece mais)
export function useDadosAoAbrir(aberto, buscar) {
    const [dados, setDados] = useState([])
    const [carregando, setCarregando] = useState(false)
    const [erro, setErro] = useState('')
    const [versao, setVersao] = useState(0)

    useEffect(() => {
        if (!aberto) return

        let cancelado = false
        setCarregando(true)
        setErro('')

        buscar()
            .then((resultado) => { if (!cancelado) setDados(Array.isArray(resultado) ? resultado : []) })
            .catch((e) => { if (!cancelado) setErro(e.message) })
            .finally(() => { if (!cancelado) setCarregando(false) })

        // Se o modal fechar antes da resposta chegar, ignora a resposta
        return () => { cancelado = true }
    }, [aberto, buscar, versao])

    const recarregar = () => setVersao((v) => v + 1)

    return { dados, carregando, erro, recarregar }
}