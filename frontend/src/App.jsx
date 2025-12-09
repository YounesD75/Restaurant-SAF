import { BrowserRouter, Routes, Route } from 'react-router-dom';
import NavBar from './components/NavBar';
import Dashboard from './pages/admin/Dashboard';
import Ingredients from './pages/admin/Ingredients';
import Recipes from './pages/admin/Recipes';
import Orders from './pages/admin/Orders';


function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <NavBar />
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/admin" element={<Dashboard />} />
          <Route path="/admin/ingredients" element={<Ingredients />} />
          <Route path="/admin/recipes" element={<Recipes />} />
          <Route path="/admin/orders" element={<Orders />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;