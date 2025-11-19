import { useState } from "react";
import { getOrderStatus } from "../../api/orderApi";

export default function OrderStatus() {
  const [id, setId] = useState("");
  const [order, setOrder] = useState(null);

  const load = async () => {
    const res = await getOrderStatus(id);
    setOrder(res.data);
  };

  return (
    <div>
      <h1>Status commande</h1>
      <input type="number" onChange={(e) => setId(e.target.value)} />
      <button onClick={load}>Chercher</button>

      {order && (
        <div>
          <p>Status: {order.status}</p>
          <p>Total: {order.totalAmount} €</p>
        </div>
      )}
    </div>
  );
}
