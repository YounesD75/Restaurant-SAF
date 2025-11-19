import { useState } from "react";
import { addIngredient } from "../../api/inventoryApi";

export default function AddIngredient() {
  const [form, setForm] = useState({
    name: "",
    quantity: 0,
    unit: "",
    threshold: 0,
  });

  const submit = async () => {
    await addIngredient(form);
    alert("Ingrédient ajouté !");
  };

  return (
    <div>
      <h1>Ajouter un ingrédient</h1>

      <input placeholder="Nom" onChange={(e) => setForm({...form, name: e.target.value})}/>
      <input placeholder="Quantité" type="number" onChange={(e) => setForm({...form, quantity: e.target.value})}/>
      <input placeholder="Unité" onChange={(e) => setForm({...form, unit: e.target.value})}/>
      <input placeholder="Seuil" type="number" onChange={(e) => setForm({...form, threshold: e.target.value})}/>

      <button onClick={submit}>Ajouter</button>
    </div>
  );
}
