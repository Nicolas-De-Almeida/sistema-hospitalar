
import PainelConsultas from './pages/PainelConsultas'


import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'

export default function App() {
  return (

    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<PainelConsultas/>} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}
