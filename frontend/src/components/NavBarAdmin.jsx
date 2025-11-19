import { Link } from "react-router-dom";

export default function NavBarAdmin() {
  return (
    <nav style={{ padding: 20, background: "#111", color: "#fff" }}>
      <Link to="/admin" style={{ marginRight: 20, color: "#fff" }}>Dashboard</Link>
      <Link to="/admin/ingredients" style={{ marginRight: 20, color: "#fff" }}>Ingrédients</Link>
      <Link to="/admin/recipes" style={{ marginRight: 20, color: "#fff" }}>Recettes</Link>
      <Link to="/admin/recipe-items" style={{ marginRight: 20, color: "#fff" }}>Items Recette</Link>
      <Link to="/admin/orders" style={{ color: "#fff" }}>Commandes</Link>
    </nav>
  );
}
