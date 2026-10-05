import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/layout';

export default function App() {
  return (

    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<h1>Início</h1>} />
          <Route path="/pacientes" element={<h1>Pacientes</h1>} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}
