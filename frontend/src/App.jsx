
import PainelQuartos from './pages/PainelQuartos'




import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'

export default function App() {
  return (

    <BrowserRouter>
      <Layout>
        <Routes>

          <Route path="/" element={<PainelQuartos/>} />


        </Routes>
      </Layout>
    </BrowserRouter>
  );
}
