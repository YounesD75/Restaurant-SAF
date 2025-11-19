import { useState } from "react";
import { addRecipeItem } from "../../api/recipeItemApi";

export default function RecipeItemsAdmin() {
  const [recipeId, setRecipeId] = useState("");
  const [ingredientId, setIngredientId] = useState("");
  const [quantity, setQuantity] = useState("");

  const submit = async () => {
    await addRecipeItem({
      recipeId,
      ingredientId,
      quantityNeeded: quantity
    });
    alert("Item ajouté !");
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>🧩 Ajouter un ingrédient à une recette</h2>

      <input placeholder="ID Recette" value={recipeId} onChange={e => setRecipeId(e.target.value)} />
      <input placeholder="ID Ingrédient" value={ingredientId} onChange={e => setIngredientId(e.target.value)} />
      <input OrdersAdminplaceholder="Quantité nécessaire" value={quantity} onChange={e => setQuantity(e.target.value)} />

      <button onClick={submit}>➕ Ajouter à la recette</button>
    </div>
  );
}
