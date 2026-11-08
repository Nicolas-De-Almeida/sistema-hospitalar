import { Navigate } from 'react-router-dom'
import { estaLogado } from '../services/authService'

// Se não tiver ninguém logado, manda para a tela de login
export default function RotaProtegida({ children }) {
    if (!estaLogado()) {
        return <Navigate to="/login" replace />
    }

    return children
}
