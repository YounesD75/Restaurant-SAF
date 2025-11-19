import { useEffect, useState } from "react";
import { getRecipes, createRecipe } from "../../api/recipeApi";

export default function RecipesAdmin() {
  const [recipes, setRecipes] = useState([]);
  const [dishName, setDishName] = useState("");

  const load = async () => {
    const res = await getRecipes();
    setRecipes(res.data);
  };

  useEffect(() => {
    load();
  }, []);

  const submit = async () => {
    await createRecipe({ dishName });
    setDishName("");
    load();
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>🍔 Gestion des Recettes</h2>

      <input placeholder="Nom du plat" value={dishName} onChange={e => setDishName(e.target.value)} />
      <button onClick={submit}>➕ Ajouter</button>

      <h3>Liste des Recettes</h3>
      <ul>
        {recipes.map(r => <li key={r.id}>{r.dishName}</li>)}
      </ul>
    </div>
  );
}
