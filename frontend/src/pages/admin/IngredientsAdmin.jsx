import { useEffect, useState } from "react";
import { createIngredient, getIngredients, updateQuantity } from "../../api/inventoryApi";

export default function IngredientsAdmin() {
  const [ingredients, setIngredients] = useState([]);
  const [name, setName] = useState("");
  const [quantity, setQuantity] = useState("");
  const [unit, setUnit] = useState("g");
  const [threshold, setThreshold] = useState("");

  const load = async () => {
    const res = await getIngredients();
    setIngredients(res.data);
  };

  useEffect(() => {
    load();
  }, []);

  const submit = async () => {
    await createIngredient({ name, quantity, unit, threshold });
    await load();
    setName(""); setQuantity(""); setThreshold("");
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>🥦 Gestion des ingrédients</h2>

      <div>
        <input placeholder="Nom" value={name} onChange={e => setName(e.target.value)} />
        <input placeholder="Quantité" value={quantity} onChange={e => setQuantity(e.target.value)} />
        <input placeholder="Unité (g, ml...)" value={unit} onChange={e => setUnit(e.target.value)} />
        <input placeholder="Seuil alerte" value={threshold} onChange={e => setThreshold(e.target.value)} />
        <button onClick={submit}>➕ Ajouter</button>
      </div>

      <h3>Liste</h3>
      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Nom</th><th>Quantité</th><th>Seuil</th><th>Action</th>
          </tr>
        </thead>
        <tbody>
          {ingredients.map(i => (
            <tr key={i.id}>
              <td>{i.name}</td>
              <td>{i.quantity}</td>
              <td>{i.threshold}</td>
              <td>
                <button onClick={() => updateQuantity(i.id, i.quantity + 10)}>
                  ➕ Réapprovisionner (+10)
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
