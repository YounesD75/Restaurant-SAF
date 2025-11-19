import { BrowserRouter, Routes, Route } from "react-router-dom";
import NavBarAdmin from "./components/NavBarAdmin";

import AdminDashboard from "./pages/admin/AdminDashboard";
import IngredientsAdmin from "./pages/admin/IngredientsAdmin";
import RecipesAdmin from "./pages/admin/RecipesAdmin";
import RecipeItemsAdmin from "./pages/admin/RecipeItemsAdmin";
import OrdersAdmin from "./pages/admin/OrdersAdmin";

export default function App() {
  return (
    <BrowserRouter>
      <NavBarAdmin />
      <Routes>
        <Route path="/admin" element={<AdminDashboard />} />
        <Route path="/admin/ingredients" element={<IngredientsAdmin />} />
        <Route path="/admin/recipes" element={<RecipesAdmin />} />
        <Route path="/admin/recipe-items" element={<RecipeItemsAdmin />} />
        <Route path="/admin/orders" element={<OrdersAdmin />} />
      </Routes>
    </BrowserRouter>
  );
}
