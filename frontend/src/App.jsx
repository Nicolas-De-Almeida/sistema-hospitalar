import PainelPrincipal from './pages/PainelPrincipal'
import PainelPacientes from './pages/PainelPacientes'
import PainelProfissionais from './pages/PainelProfissionais'
import PainelConsultas from './pages/PainelConsultas'
import PainelInternacoes from './pages/PainelInternacoes'
import PainelQuartos from './pages/PainelQuartos'




import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'

export default function App() {
  return (

    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<PainelPrincipal/>} />
          <Route path="/pacientes" element={<PainelPacientes/>} />
          <Route path="/profissionais" element={<PainelProfissionais/>} />
          <Route path="/consultas" element={<PainelConsultas/>} />
          <Route path="/internacoes" element={<PainelInternacoes/>} />
          <Route path="/quartos" element={<PainelQuartos/>} />


        </Routes>
      </Layout>
    </BrowserRouter>
  );
}
