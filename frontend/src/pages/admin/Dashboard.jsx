import { useEffect, useState } from 'react';
import { ingredientApi } from '../../api/ingredientApi';
import { recipeApi } from '../../api/recipeApi';
import { orderApi } from '../../api/orderApi';

const Dashboard = () => {
  const [stats, setStats] = useState({
    ingredients: 0,
    recipes: 0,
    orders: 0,
    lowStock: 0,
  });
  const [recentOrders, setRecentOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
  const fetchDashboardData = async () => {
    try {
      const [ingredientsRes, recipesRes, receiptsRes] = await Promise.all([
        ingredientApi.getAll(),
        recipeApi.getAll(),
        orderApi.getReceipts(),
      ]);

      const lowStock = ingredientsRes.data.filter(
        ing => ing.quantity <= ing.threshold
      ).length;

      setStats({
        ingredients: ingredientsRes.data.length,
        recipes: recipesRes.data.length,
        orders: receiptsRes.data.length,
        lowStock,
      });

      // Prendre les 5 dernières commandes
      setRecentOrders(receiptsRes.data.slice(0, 5));
    } catch (error) {
      console.error('Erreur lors du chargement du dashboard:', error);
    } finally {
      setLoading(false);
    }
  };

  fetchDashboardData();
}, []);

  const statCards = [
    { 
      title: 'Ingrédients', 
      value: stats.ingredients, 
      icon: '🥦',
      color: 'bg-gradient-to-r from-green-500 to-emerald-500',
      description: 'en stock'
    },
    { 
      title: 'Recettes', 
      value: stats.recipes, 
      icon: '🍔',
      color: 'bg-gradient-to-r from-orange-500 to-amber-500',
      description: 'disponibles'
    },
    { 
      title: 'Commandes', 
      value: stats.orders, 
      icon: '📦',
      color: 'bg-gradient-to-r from-blue-500 to-indigo-500',
      description: 'aujourd\'hui'
    },
    { 
      title: 'Stocks bas', 
      value: stats.lowStock, 
      icon: '⚠️',
      color: 'bg-gradient-to-r from-red-500 to-pink-500',
      description: 'à réapprovisionner'
    },
  ];

  if (loading) {
    return (
      <div className="main-content">
        <div className="text-center">
          <div>Chargement du dashboard...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="main-content">
  <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '1rem' }}>
    <div className="mb-6">
      <h1 style={{ fontSize: '2rem', color: '#1e293b', fontWeight: 600 }}>Tableau de Bord</h1>
      <p style={{ color: '#64748b' }}>Bienvenue dans l'administration du Restaurant SAF</p>
    </div>

    {/* Statistiques */}
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1.5rem', marginBottom: '2rem' }}>
      {statCards.map((stat, index) => (
        <div key={index} style={{
          background: 'white',
          borderRadius: '1rem',
          padding: '1.5rem',
          boxShadow: '0 8px 20px rgba(0,0,0,0.08)',
          border: '1px solid #e5e7eb',
          transition: 'transform 0.2s, box-shadow 0.2s',
        }} onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-3px)'; e.currentTarget.style.boxShadow = '0 12px 24px rgba(0,0,0,0.12)'; }}
           onMouseLeave={e => { e.currentTarget.style.transform = 'translateY(0)'; e.currentTarget.style.boxShadow = '0 8px 20px rgba(0,0,0,0.08)'; }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
            <div style={{
              background: stat.color.includes('green') ? '#dcfce7' :
                         stat.color.includes('orange') ? '#ffedd5' :
                         stat.color.includes('blue') ? '#dbeafe' : '#fee2e2',
              padding: '0.75rem',
              borderRadius: '0.75rem',
              fontSize: '1.5rem'
            }}>{stat.icon}</div>
            <div style={{ fontSize: '2rem', fontWeight: 'bold', color: '#1e293b' }}>{stat.value}</div>
          </div>
          <div style={{ fontWeight: 600, color: '#334155' }}>{stat.title}</div>
          <div style={{ fontSize: '0.875rem', color: '#64748b' }}>{stat.description}</div>
        </div>
      ))}
    </div>

    {/* Dernières commandes & actions rapides */}
    <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '2rem' }}>
      {/* Tableau commandes */}
      <div style={{ background: 'white', borderRadius: '1rem', padding: '1.5rem', boxShadow: '0 8px 20px rgba(0,0,0,0.08)' }}>
        <h3 style={{ fontSize: '1.25rem', fontWeight: 600, color: '#1e293b', marginBottom: '1rem' }}>📋 Dernières commandes</h3>
        {recentOrders.length > 0 ? (
          <table className="table" style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ textAlign: 'left', borderBottom: '2px solid #e5e7eb' }}>
                <th>ID</th>
                <th>Client</th>
                <th>Statut</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {recentOrders.map(order => (
                <tr key={order.orderId} style={{ transition: 'background 0.2s', cursor: 'pointer' }} onMouseEnter={e => e.currentTarget.style.background = '#f9fafb'} onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                  <td>#{order.orderId}</td>
                  <td>{order.clientName || 'Non spécifié'}</td>
                  <td>
                    <span style={{
                      padding: '0.25rem 0.5rem',
                      borderRadius: '9999px',
                      fontSize: '0.75rem',
                      fontWeight: 600,
                      background: (order.status || 'COMPLETED') === 'PREPARING' ? '#fef3c7' :
                                 (order.status || 'COMPLETED') === 'READY' ? '#d1fae5' :
                                 (order.status || 'COMPLETED') === 'CANCELLED' ? '#fee2e2' : '#e0e7ff',
                      color: (order.status || 'COMPLETED') === 'PREPARING' ? '#92400e' :
                             (order.status || 'COMPLETED') === 'READY' ? '#065f46' :
                             (order.status || 'COMPLETED') === 'CANCELLED' ? '#991b1b' : '#3730a3'
                    }}>
                      {order.status || 'COMPLETED'}
                    </span>
                  </td>
                  <td style={{ fontSize: '0.875rem', color: '#64748b' }}>
                    {order.issuedAt ? new Date(order.issuedAt).toLocaleDateString() : new Date().toLocaleDateString()}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <div style={{ textAlign: 'center', padding: '2rem', color: '#94a3b8' }}>Aucune commande récente</div>
        )}
      </div>

      {/* Actions rapides */}
      <div style={{ background: 'white', borderRadius: '1rem', padding: '1.5rem', boxShadow: '0 8px 20px rgba(0,0,0,0.08)' }}>
        <h3 style={{ fontSize: '1.25rem', fontWeight: 600, color: '#1e293b', marginBottom: '1rem' }}>⚡ Actions rapides</h3>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
          <button style={{ padding: '0.75rem', borderRadius: '0.75rem', fontWeight: 600, background: '#4f46e5', color: 'white', border: 'none', cursor: 'pointer', transition: 'background 0.2s' }} onMouseEnter={e => e.currentTarget.style.background = '#4338ca'} onMouseLeave={e => e.currentTarget.style.background = '#4f46e5'}>+ Ajouter un ingrédient</button>
          <button style={{ padding: '0.75rem', borderRadius: '0.75rem', fontWeight: 600, background: '#10b981', color: 'white', border: 'none', cursor: 'pointer', transition: 'background 0.2s' }} onMouseEnter={e => e.currentTarget.style.background = '#059669'} onMouseLeave={e => e.currentTarget.style.background = '#10b981'}>+ Créer une recette</button>
          <button style={{ padding: '0.75rem', borderRadius: '0.75rem', fontWeight: 600, background: '#f59e0b', color: 'white', border: 'none', cursor: 'pointer', transition: 'background 0.2s' }} onMouseEnter={e => e.currentTarget.style.background = '#d97706'} onMouseLeave={e => e.currentTarget.style.background = '#f59e0b'}>📊 Voir les statistiques</button>
          <button style={{ padding: '0.75rem', borderRadius: '0.75rem', fontWeight: 600, background: '#3b82f6', color: 'white', border: 'none', cursor: 'pointer', transition: 'background 0.2s' }} onMouseEnter={e => e.currentTarget.style.background = '#2563eb'} onMouseLeave={e => e.currentTarget.style.background = '#3b82f6'}>🛒 Nouvelle commande</button>
        </div>
      </div>
    </div>
  </div>
</div>

  );
};

export default Dashboard;
