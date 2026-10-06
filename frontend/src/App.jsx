import PainelPrincipal from './pages/PainelPrincipal';

import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';

export default function App() {
  return (

    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<PainelPrincipal/>} />
          <Route path="/pacientes" element={<h1>Pacientes</h1>} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}
