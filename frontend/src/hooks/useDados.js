import { useCallback, useEffect, useState } from 'react'

// Busca uma lista no back end e devolve { dados, carregando, erro, recarregar }.
// "buscar" precisa ser uma função fixa (as do hospitalService já são).
export function useDados(buscar) {
    const [dados, setDados] = useState([])
    const [carregando, setCarregando] = useState(true)
    const [erro, setErro] = useState('')

    const recarregar = useCallback(async () => {
        setCarregando(true)
        setErro('')
        try {
            const resultado = await buscar()
            setDados(Array.isArray(resultado) ? resultado : [])
        } catch (e) {
            setErro(e.message)
        } finally {
            setCarregando(false)
        }
    }, [buscar])

    useEffect(() => {
        recarregar()
    }, [recarregar])

    return { dados, carregando, erro, recarregar }
}