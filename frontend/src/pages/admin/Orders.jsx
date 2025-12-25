import { useEffect, useState } from 'react';
import { orderApi } from '../../api/orderApi';

const Orders = () => {
  const [receipts, setReceipts] = useState([]);
  const [menu, setMenu] = useState([]);
  const [treasury, setTreasury] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [newOrder, setNewOrder] = useState({
    clientName: '',
    tableNumber: '',
    instructions: '',
    items: [{ dishName: '', quantity: 1 }]
  });

  /* =========================
     Chargement initial
  ========================= */
  useEffect(() => {
    loadReceipts();
    loadMenu();
    loadTreasury();
  }, []);

  const loadReceipts = async () => {
    try {
      const res = await orderApi.getReceipts();
      setReceipts(res.data);
    } catch (err) {
      console.error(err);
      setError('Erreur lors du chargement des reçus');
    }
  };

  const loadMenu = async () => {
    try {
      const res = await orderApi.getMenu();
      setMenu(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const loadTreasury = async () => {
    try {
      const res = await orderApi.getTreasury();
      setTreasury(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  /* =========================
     Création commande
  ========================= */
  const handleCreateOrder = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await orderApi.create(newOrder);
      setSuccess('Commande envoyée avec succès');

      setNewOrder({
        clientName: '',
        tableNumber: '',
        instructions: '',
        items: [{ dishName: '', quantity: 1 }]
      });

      setTimeout(() => {
        loadReceipts();
        loadTreasury();
      }, 1000);
    } catch (err) {
      console.error(err);
      setError('Erreur lors de la création de la commande');
    } finally {
      setLoading(false);
    }
  };

  /* =========================
     Gestion items
  ========================= */
  const addItem = () => {
    setNewOrder({
      ...newOrder,
      items: [...newOrder.items, { dishName: '', quantity: 1 }]
    });
  };

  const updateItem = (index, field, value) => {
    const updated = [...newOrder.items];
    updated[index][field] = value;
    setNewOrder({ ...newOrder, items: updated });
  };

  const removeItem = (index) => {
    setNewOrder({
      ...newOrder,
      items: newOrder.items.filter((_, i) => i !== index)
    });
  };

  /* =========================
     UI
  ========================= */
  return (
    <div className="main-content">
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <h1 style={{ fontSize: '2rem', marginBottom: '1rem' }}>
          🧾 Commandes / Reçus
        </h1>

        {error && <div className="alert alert-error mb-3">{error}</div>}
        {success && <div className="alert alert-success mb-3">{success}</div>}

        {/* =======================
            Trésorerie
        ======================= */}
        {treasury && (
          <div className="card mb-5">
            <h3>Trésorerie</h3>
            <p>Total encaissé : {treasury.totalRevenue} €</p>
            <p>Commandes réglées : {treasury.settledOrders}</p>
          </div>
        )}

        {/* =======================
            Nouvelle commande
        ======================= */}
        <div className="card mb-5">
          <h3>Nouvelle commande</h3>
          <form onSubmit={handleCreateOrder}>
            <div className="form-group">
              <label>Nom du client</label>
              <input
                className="form-control"
                value={newOrder.clientName}
                onChange={(e) =>
                  setNewOrder({ ...newOrder, clientName: e.target.value })
                }
                required
              />
            </div>

            <div className="form-group">
              <label>Table</label>
              <input
                className="form-control"
                value={newOrder.tableNumber}
                onChange={(e) =>
                  setNewOrder({ ...newOrder, tableNumber: e.target.value })
                }
              />
            </div>

            <div className="form-group">
              <label>Instructions</label>
              <input
                className="form-control"
                value={newOrder.instructions}
                onChange={(e) =>
                  setNewOrder({ ...newOrder, instructions: e.target.value })
                }
              />
            </div>

            <div className="mb-3">
              <label>Plats</label>

              {newOrder.items.map((item, index) => (
                <div
                  key={index}
                  style={{ display: 'flex', gap: '1rem', marginBottom: '0.5rem' }}
                >
                  <select
                    className="form-control"
                    value={item.dishName}
                    onChange={(e) =>
                      updateItem(index, 'dishName', e.target.value)
                    }
                    required
                  >
                    <option value="">-- Choisir un plat --</option>
                    {menu.map((m) => (
                      <option key={m.name} value={m.dishName}>
                        {m.name} - {m.price} €
                      </option>
                    ))}
                  </select>

                  <input
                    type="number"
                    min="1"
                    className="form-control"
                    value={item.quantity}
                    onChange={(e) =>
                      updateItem(index, 'quantity', Number(e.target.value))
                    }
                    style={{ width: '120px' }}
                  />

                  {newOrder.items.length > 1 && (
                    <button
                      type="button"
                      className="btn btn-danger"
                      onClick={() => removeItem(index)}
                    >
                      ×
                    </button>
                  )}
                </div>
              ))}

              <button type="button" className="btn" onClick={addItem}>
                + Ajouter un plat
              </button>
            </div>

            <button className="btn btn-success" disabled={loading}>
              {loading ? 'Envoi...' : 'Passer la commande'}
            </button>
          </form>
        </div>

        {/* =======================
            Liste des reçus
        ======================= */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <h3>Reçus ({receipts.length})</h3>
            <button className="btn" onClick={loadReceipts}>
              🔄 Actualiser
            </button>
          </div>

          {receipts.length === 0 ? (
            <p style={{ color: '#94a3b8' }}>Aucun reçu pour le moment</p>
          ) : (
            <table className="table">
              <thead>
                <tr>
                  <th>Commande</th>
                  <th>Client</th>
                  <th>Plats</th>
                  <th>Total</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {receipts.map((r) => (
                  <tr key={r.orderId}>
                    <td>#{r.orderId}</td>
                    <td>{r.clientName || '—'}</td>
                    <td>
                      {r.items.map((i, idx) => (
                        <div key={idx}>
                          {i.quantity} × {i.dishName}
                        </div>
                      ))}
                    </td>
                    <td>{r.total} €</td>
                    <td>{new Date(r.issuedAt).toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
};

export default Orders;
