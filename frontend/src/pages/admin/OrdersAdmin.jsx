import { useEffect, useState } from "react";
import { getOrders, updateOrderStatus } from "../../api/orderApi";

export default function OrdersAdmin() {
  const [orders, setOrders] = useState([]);

  const load = async () => {
    const res = await getOrders();
    setOrders(res.data);
  };

  useEffect(() => {
    load();
  }, []);

  return (
    <div style={{ padding: 20 }}>
      <h2>📦 Commandes</h2>

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>ID</th><th>Client</th><th>Status</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {orders.map(o => (
            <tr key={o.id}>
              <td>{o.id}</td>
              <td>{o.customerName}</td>
              <td>{o.status}</td>
              <td>
                <button onClick={() => updateOrderStatus(o.id, "CANCELLED")}>❌ Annuler</button>
                <button onClick={() => updateOrderStatus(o.id, "PREPARING")}>🍳 En cuisine</button>
                <button onClick={() => updateOrderStatus(o.id, "READY")}>✅ Prêt</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
