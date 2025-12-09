import { useEffect, useState } from 'react';
import { orderApi } from '../../api/orderApi';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [newOrder, setNewOrder] = useState({
    customerName: '',
    items: [{ dishName: '', quantity: 1 ,unitPrice:0}]
  });


  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const loadOrders = async () => {
    try {
      const res = await orderApi.getAll();
      setOrders(res.data);
    } catch (err) {
      setError('Erreur lors du chargement des commandes');
      console.error(err);
    }
  };

  useEffect(() => {
    loadOrders();
  }, []);

  const handleCreateOrder = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await orderApi.create(newOrder);
      setSuccess('Commande créée avec succès');
      setNewOrder({
        customerName: '',
        items: [{ dishName: '', quantity: 1 ,unitPrice:0}]
      });
      // Recharger après un délai pour laisser le temps au backend
      setTimeout(loadOrders, 1000);
    } catch (err) {
      setError('Erreur lors de la création de la commande');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (orderId, status) => {
    try {
      await orderApi.updateStatus(orderId, status);
      setSuccess(`Statut mis à jour: ${status}`);
      await loadOrders();
    } catch (err) {
      setError('Erreur lors de la mise à jour du statut');
      console.error(err);
    }
  };

  const addItem = () => {
    setNewOrder({
      ...newOrder,
      items: [...newOrder.items, { dishName: '', quantity: 1, unitPrice:0 }]
    });
  };

  const updateItem = (index, field, value) => {
    const newItems = [...newOrder.items];
    newItems[index][field] = value;
    setNewOrder({ ...newOrder, items: newItems });
  };

  const removeItem = (index) => {
    const newItems = newOrder.items.filter((_, i) => i !== index);
    setNewOrder({ ...newOrder, items: newItems });
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return { bg: '#fef3c7', text: '#92400e' };
      case 'PREPARING': return { bg: '#dbeafe', text: '#1e40af' };
      case 'READY': return { bg: '#d1fae5', text: '#065f46' };
      case 'CANCELLED': return { bg: '#fee2e2', text: '#991b1b' };
      default: return { bg: '#f3f4f6', text: '#4b5563' };
    }
  };

  return (
    <div className="main-content">
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <div className="mb-5">
          <h1 style={{ fontSize: '2rem', color: '#1e293b', marginBottom: '0.5rem' }}>
            📦 Gestion des Commandes
          </h1>
          <p style={{ color: '#64748b' }}>
            Gérez les commandes des clients
          </p>
        </div>

        {/* Messages */}
        {error && <div className="alert alert-error mb-4">{error}</div>}
        {success && <div className="alert alert-success mb-4">{success}</div>}

        {/* Formulaire de nouvelle commande */}
        <div className="card mb-5">
          <h3 className="mb-3">Nouvelle commande</h3>
          <form onSubmit={handleCreateOrder}>
            <div className="form-group">
              <label className="form-label">Nom du client</label>
              <input
                type="text"
                className="form-control"
                value={newOrder.customerName}
                onChange={(e) => setNewOrder({ ...newOrder, customerName: e.target.value })}
                required
                placeholder="Nom du client"
              />
            </div>

            <div className="mb-3">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <label className="form-label">Plats commandés</label>
                <button
                  type="button"
                  className="btn"
                  onClick={addItem}
                >
                  + Ajouter un plat
                </button>
              </div>
              
              {newOrder.items.map((item, index) => (
                <div key={index} style={{ 
                  display: 'flex', 
                  gap: '1rem', 
                  alignItems: 'center',
                  marginBottom: '0.5rem'
                }}>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Nom du plat"
                    value={item.dishName}
                    onChange={(e) => updateItem(index, 'dishName', e.target.value)}
                    required
                    style={{ flex: 2 }}
                  />
                  <input
                    type="number"
                    className="form-control"
                    placeholder="Quantité"
                    value={item.quantity}
                    onChange={(e) => updateItem(index, 'quantity', parseInt(e.target.value) || 1)}
                    min="1"
                    style={{ width: '100px' }}
                  />

                  <input
                    type="number"
                    className="form-control"
                    placeholder="Prix Unitaire"
                    value={item.unitPrice}
                    onChange={(e) => updateItem(index, 'unitPrice', parseInt(e.target.value) || 1)}
                    min="0"
                    style={{ width: '100px' }}
                  />
                  {newOrder.items.length > 1 && (
                    <button
                      type="button"
                      className="btn btn-danger"
                      onClick={() => removeItem(index)}
                      style={{ padding: '0.25rem 0.5rem' }}
                    >
                      ×
                    </button>
                  )}
                </div>
              ))}
            </div>

            <button
              type="submit"
              className="btn btn-success"
              disabled={loading}
            >
              {loading ? 'Création...' : 'Créer la commande'}
            </button>
          </form>
        </div>

        {/* Liste des commandes */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <h3>Commandes en cours ({orders.length})</h3>
            <button
              className="btn"
              onClick={loadOrders}
            >
              🔄 Actualiser
            </button>
          </div>

          {orders.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '2rem', color: '#94a3b8' }}>
              Aucune commande pour le moment
            </div>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table className="table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Client</th>
                    <th>Plats</th>
                    <th>Statut</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map(order => {
                    const statusColor = getStatusColor(order.status);
                    return (
                      <tr key={order.id}>
                        <td>
                          <div style={{ fontWeight: '500' }}>
                            #{order.id}
                          </div>
                        </td>
                        <td>
                          {order.customerName || 'Non spécifié'}
                        </td>
                        <td>
                          {order.items?.length > 0 ? (
                            <div style={{ fontSize: '0.875rem' }}>
                              {order.items.map((item, idx) => (
                                <div key={idx}>
                                  {item.quantity}x {item.dishName} avec {item.unitPrice}$ l'unité
                                </div>
                              ))}
                            </div>
                          ) : (
                            <span style={{ color: '#94a3b8' }}>Aucun détail</span>
                          )}
                        </td>
                        <td>
                          <span style={{
                            padding: '0.25rem 0.75rem',
                            borderRadius: '9999px',
                            fontSize: '0.75rem',
                            fontWeight: '600',
                            background: statusColor.bg,
                            color: statusColor.text
                          }}>
                            {order.status}
                          </span>
                        </td>
                        <td>
                          <div style={{ display: 'flex', gap: '0.25rem', flexWrap: 'wrap' }}>
                            {order.status !== 'CANCELLED' && order.status !== 'READY' && (
                              <>
                                <button
                                  className="btn btn-warning"
                                  onClick={() => handleStatusUpdate(order.id, 'PREPARING')}
                                  style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                                >
                                  En cuisine
                                </button>
                                <button
                                  className="btn btn-success"
                                  onClick={() => handleStatusUpdate(order.id, 'READY')}
                                  style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                                >
                                  Prêt
                                </button>
                              </>
                            )}
                            {order.status !== 'CANCELLED' && (
                              <button
                                className="btn btn-danger"
                                onClick={() => handleStatusUpdate(order.id, 'CANCELLED')}
                                style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                              >
                                Annuler
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Orders;