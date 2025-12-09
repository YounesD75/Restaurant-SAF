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
        const [ingredientsRes, recipesRes, ordersRes] = await Promise.all([
          ingredientApi.getAll(),
          recipeApi.getAll(),
          orderApi.getAll(),
        ]);

        const lowStock = ingredientsRes.data.filter(
          ing => ing.quantity <= ing.threshold
        ).length;

        setStats({
          ingredients: ingredientsRes.data.length,
          recipes: recipesRes.data.length,
          orders: ordersRes.data.length,
          lowStock,
        });

        // Prendre les 5 dernières commandes
        setRecentOrders(ordersRes.data.slice(0, 5));
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
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <div className="mb-5">
          <h1 style={{ fontSize: '2rem', color: '#1e293b', marginBottom: '0.5rem' }}>
            Tableau de Bord
          </h1>
          <p style={{ color: '#64748b' }}>
            Bienvenue dans l'administration du Restaurant SAF
          </p>
        </div>

        {/* Statistiques */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
          gap: '1rem',
          marginBottom: '2rem'
        }}>
          {statCards.map((stat, index) => (
            <div key={index} style={{
              background: 'white',
              borderRadius: '0.75rem',
              padding: '1.5rem',
              boxShadow: '0 4px 6px rgba(0, 0, 0, 0.05)',
              border: '1px solid #e2e8f0'
            }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                <div style={{
                  background: stat.color.includes('green') ? '#dcfce7' : 
                             stat.color.includes('orange') ? '#ffedd5' : 
                             stat.color.includes('blue') ? '#dbeafe' : '#fee2e2',
                  padding: '0.75rem',
                  borderRadius: '0.5rem',
                  fontSize: '1.5rem'
                }}>
                  {stat.icon}
                </div>
                <div style={{ fontSize: '2rem', fontWeight: 'bold', color: '#1e293b' }}>
                  {stat.value}
                </div>
              </div>
              <div style={{ fontWeight: '600', color: '#334155' }}>
                {stat.title}
              </div>
              <div style={{ fontSize: '0.875rem', color: '#64748b' }}>
                {stat.description}
              </div>
            </div>
          ))}
        </div>

        {/* Dernières commandes et actions rapides */}
        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '2rem' }}>
          {/* Dernières commandes */}
          <div style={{ background: 'white', borderRadius: '0.75rem', padding: '1.5rem', boxShadow: '0 4px 6px rgba(0, 0, 0, 0.05)' }}>
            <h3 style={{ fontSize: '1.25rem', color: '#1e293b', marginBottom: '1rem' }}>
              📋 Dernières commandes
            </h3>
            {recentOrders.length > 0 ? (
              <table className="table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Client</th>
                    <th>Statut</th>
                    <th>Date</th>
                  </tr>
                </thead>
                <tbody>
                  {recentOrders.map(order => (
                    <tr key={order.id}>
                      <td>#{order.id}</td>
                      <td>{order.customerName || 'Non spécifié'}</td>
                      <td>
                        <span style={{
                          padding: '0.25rem 0.5rem',
                          borderRadius: '9999px',
                          fontSize: '0.75rem',
                          fontWeight: '600',
                          background: order.status === 'PREPARING' ? '#fef3c7' :
                                     order.status === 'READY' ? '#d1fae5' :
                                     order.status === 'CANCELLED' ? '#fee2e2' : '#e0e7ff',
                          color: order.status === 'PREPARING' ? '#92400e' :
                                 order.status === 'READY' ? '#065f46' :
                                 order.status === 'CANCELLED' ? '#991b1b' : '#3730a3'
                        }}>
                          {order.status}
                        </span>
                      </td>
                      <td style={{ fontSize: '0.875rem', color: '#64748b' }}>
                        {new Date().toLocaleDateString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <div style={{ textAlign: 'center', padding: '2rem', color: '#94a3b8' }}>
                Aucune commande récente
              </div>
            )}
          </div>

          {/* Actions rapides */}
          <div style={{ background: 'white', borderRadius: '0.75rem', padding: '1.5rem', boxShadow: '0 4px 6px rgba(0, 0, 0, 0.05)' }}>
            <h3 style={{ fontSize: '1.25rem', color: '#1e293b', marginBottom: '1rem' }}>
              ⚡ Actions rapides
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              <button className="btn btn-primary">
                + Ajouter un ingrédient
              </button>
              <button className="btn btn-success">
                + Créer une recette
              </button>
              <button className="btn">
                📊 Voir les statistiques
              </button>
              <button className="btn">
                🛒 Nouvelle commande
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;