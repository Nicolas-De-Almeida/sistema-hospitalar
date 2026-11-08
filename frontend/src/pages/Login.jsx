import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { Mail, Lock, Eye, EyeOff, LogIn, AlertCircle } from 'lucide-react'

import LogoHospital from '../assets/img/logo-hospital.png'
import { login, estaLogado } from '../services/authService'

export default function Login() {
    const navigate = useNavigate()

    const [email, setEmail] = useState('')
    const [senha, setSenha] = useState('')
    const [mostrarSenha, setMostrarSenha] = useState(false)
    const [erro, setErro] = useState('')
    const [carregando, setCarregando] = useState(false)

    // Quem já está logado não precisa ver a tela de login
    if (estaLogado()) {
        return <Navigate to="/" replace />
    }

    async function handleSubmit(e) {
        e.preventDefault()
        setErro('')
        setCarregando(true)

        try {
            await login(email, senha)
            navigate('/', { replace: true })
        } catch (err) {
            setErro(err.message)
        } finally {
            setCarregando(false)
        }
    }

    return (
        <div className="flex min-h-screen">
            {/* Lado esquerdo: só aparece em telas grandes */}
            <div className="hidden lg:flex flex-col justify-between w-1/2 bg-cyan-950 text-white p-12">
                <div className="flex gap-3 items-center">
                    <img className="w-10 h-10 rounded-md border border-slate-600 object-cover" src={LogoHospital} alt="Logo do hospital" />
                    <div>
                        <p className="font-bold leading-none">PUC</p>
                        <p className="text-slate-300 leading-none">Hospital</p>
                    </div>
                </div>

                <div>
                    <h1 className="text-3xl font-bold leading-tight">Sistema de Informação Hospitalar</h1>
                    <p className="text-slate-300 mt-3 max-w-md">
                        Pacientes, profissionais, consultas, internações e quartos em um só lugar.
                    </p>
                </div>

                <p className="text-xs text-slate-400">PUC Minas · Belo Horizonte</p>
            </div>

            {/* Lado direito: formulário */}
            <div className="flex flex-1 items-center justify-center bg-zinc-50 p-6">
                <div className="w-full max-w-md bg-white border border-slate-200 rounded-2xl shadow-sm p-8">
                    <div className="mb-6">
                        <span className="font-bold tracking-wider text-cyan-800 uppercase text-sm">ACESSO AO SISTEMA</span>
                        <h2 className="text-2xl font-bold text-slate-800 mt-1">Entrar</h2>
                        <p className="text-xs text-slate-400 mt-1">Use o e-mail e a senha cadastrados.</p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label htmlFor="email" className="block text-xs font-semibold text-slate-700 mb-1">E-mail</label>
                            <div className="relative">
                                <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
                                <input
                                    id="email"
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    placeholder="email@exemplo.com"
                                    autoComplete="username"
                                    required
                                    className="w-full pl-9 pr-3 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"
                                />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="senha" className="block text-xs font-semibold text-slate-700 mb-1">Senha</label>
                            <div className="relative">
                                <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
                                <input
                                    id="senha"
                                    type={mostrarSenha ? 'text' : 'password'}
                                    value={senha}
                                    onChange={(e) => setSenha(e.target.value)}
                                    placeholder="Sua senha"
                                    autoComplete="current-password"
                                    required
                                    className="w-full pl-9 pr-10 py-2 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-teal-600 transition-colors placeholder:text-slate-300"
                                />
                                <button
                                    type="button"
                                    onClick={() => setMostrarSenha(!mostrarSenha)}
                                    aria-label={mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'}
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors cursor-pointer"
                                >
                                    {mostrarSenha ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                                </button>
                            </div>
                        </div>

                        {erro && (
                            <div role="alert" className="flex items-center gap-2 bg-red-50 border border-red-200 text-red-700 text-xs rounded-lg px-3 py-2">
                                <AlertCircle className="w-4 h-4 shrink-0" />
                                <span>{erro}</span>
                            </div>
                        )}

                        <button
                            type="submit"
                            disabled={carregando}
                            className="w-full flex items-center justify-center gap-2 bg-cyan-800 hover:bg-cyan-950 disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold text-sm px-4 py-2.5 rounded-xl shadow-sm transition-colors cursor-pointer"
                        >
                            <LogIn className="w-4 h-4" />
                            <span>{carregando ? 'Entrando...' : 'Entrar'}</span>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    )
}
