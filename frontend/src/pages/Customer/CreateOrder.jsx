import { useState } from "react";
import { createOrder } from "../../api/orderApi";

export default function CreateOrder() {
  const [customerName, setCustomerName] = useState("");
  const [items, setItems] = useState([{ dishName: "", quantity: 1 }]);

  const addItem = () => {
    setItems([...items, { dishName: "", quantity: 1 }]);
  };

  const sendOrder = async () => {
    await createOrder({ customerName, items });
    alert("Commande envoyée !");
  };

  return (
    <div>
      <h1>Créer une commande</h1>

      <input
        type="text"
        placeholder="Nom Client"
        onChange={(e) => setCustomerName(e.target.value)}
      />

      {items.map((item, idx) => (
        <div key={idx}>
          <input
            type="text"
            placeholder="Plat"
            onChange={(e) => {
              const newItems = [...items];
              newItems[idx].dishName = e.target.value;
              setItems(newItems);
            }}
          />
          <input
            type="number"
            placeholder="Quantité"
            onChange={(e) => {
              const newItems = [...items];
              newItems[idx].quantity = e.target.value;
              setItems(newItems);
            }}
          />
        </div>
      ))}

      <button onClick={addItem}>Ajouter un plat</button>
      <button onClick={sendOrder}>Envoyer la commande</button>
    </div>
  );
}
