import { useEffect, useState } from 'react';
import { ingredientApi } from '../../api/ingredientApi';

const Ingredients = () => {
  const [ingredients, setIngredients] = useState([]);
  const [form, setForm] = useState({
    name: '',
    quantity: '',
    unit: 'g',
    threshold: '',
  });
  const [loading, setLoading] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const loadIngredients = async () => {
    try {
      const res = await ingredientApi.getAll();
      setIngredients(res.data);
      console.log(res.data);
      
    } catch (err) {
      setError('Erreur lors du chargement des ingrédients');
      console.error(err);
    }
  };

  useEffect(() => {
    loadIngredients();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      if (editingId) {
        // Note:  API ne supporte pas update, seulement create et updateQuantity
        // Pour l'instant, on créer un nouveau
        await ingredientApi.create(form);
        setSuccess('Ingrédient créé avec succès');
      } else {
        await ingredientApi.create(form);
        setSuccess('Ingrédient créé avec succès');
      }
      
      setForm({ name: '', quantity: '', unit: 'g', threshold: '' });
      setEditingId(null);
      await loadIngredients();
    } catch (err) {
      setError('Erreur lors de la sauvegarde');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateQuantity = async (id, currentQty, amount) => {
    try {
      await ingredientApi.updateQuantity(id, currentQty + amount);
      setSuccess('Quantité mise à jour');
      await loadIngredients();
    } catch (err) {
      setError('Erreur lors de la mise à jour');
      console.error(err);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Supprimer cet ingrédient ?')) {
      try {
        // Note:  API ne supporte pas DELETE pour l'instant
        setError('Suppression non implémentée dans l\'API');
      } catch (err) {
        setError('Erreur lors de la suppression');
        console.error(err);
      }
    }
  };

  const handleEdit = (ingredient) => {
    setForm({
      name: ingredient.name,
      quantity: ingredient.quantity,
      unit: ingredient.unit,
      threshold: ingredient.threshold,
    });
    setEditingId(ingredient.id);
  };

  return (
    <div className="main-content">
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <div className="mb-5">
          <h1 style={{ fontSize: '2rem', color: '#1e293b', marginBottom: '0.5rem' }}>
            🥦 Gestion des Ingrédients
          </h1>
          <p style={{ color: '#64748b' }}>
            Gérez le stock de vos ingrédients
          </p>
        </div>

        {/* Messages d'alerte */}
        {error && (
          <div className="alert alert-error mb-4">
            {error}
          </div>
        )}
        
        {success && (
          <div className="alert alert-success mb-4">
            {success}
          </div>
        )}

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
          {/* Formulaire */}
          <div className="card">
            <h3 className="mb-3" style={{ color: '#1e293b' }}>
              {editingId ? '✏️ Modifier' : '➕ Nouvel ingrédient'}
            </h3>
            
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Nom</label>
                <input
                  type="text"
                  className="form-control"
                  value={form.name}
                  onChange={(e) => setForm({...form, name: e.target.value})}
                  required
                  placeholder="Tomate, Fromage, Pain..."
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">Quantité initiale</label>
                  <input
                    type="number"
                    className="form-control"
                    value={form.quantity}
                    onChange={(e) => setForm({...form, quantity: e.target.value})}
                    required
                    min="0"
                    step="0.01"
                  />
                </div>
                
                <div className="form-group">
                  <label className="form-label">Unité</label>
                  <select
                    className="form-control"
                    value={form.unit}
                    onChange={(e) => setForm({...form, unit: e.target.value})}
                  >
                    <option value="g">g (grammes)</option>
                    <option value="kg">kg (kilogrammes)</option>
                    <option value="ml">ml (millilitres)</option>
                    <option value="L">L (litres)</option>
                    <option value="unité">Unité</option>
                    <option value="pcs">Pièces</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Seuil d'alerte</label>
                <input
                  type="number"
                  className="form-control"
                  value={form.threshold}
                  onChange={(e) => setForm({...form, threshold: e.target.value})}
                  required
                  min="0"
                  step="0.01"
                  placeholder="Quantité minimum avant alerte"
                />
              </div>

              <button
                type="submit"
                className="btn btn-success"
                disabled={loading}
                style={{ width: '100%', marginBottom: '0.5rem' }}
              >
                {loading ? 'Chargement...' : editingId ? 'Mettre à jour' : 'Ajouter'}
              </button>
              
              {editingId && (
                <button
                  type="button"
                  className="btn"
                  onClick={() => {
                    setEditingId(null);
                    setForm({ name: '', quantity: '', unit: 'g', threshold: '' });
                  }}
                  style={{ width: '100%' }}
                >
                  Annuler
                </button>
              )}
            </form>
          </div>

          {/* Liste */}
          <div>
            <div className="card" style={{ overflow: 'hidden' }}>
              <div style={{ 
                display: 'flex', 
                justifyContent: 'space-between', 
                alignItems: 'center',
                marginBottom: '1rem'
              }}>
                <h3 style={{ color: '#1e293b' }}>
                  📋 Stock ({ingredients.length} ingrédients)
                </h3>
                <button
                  className="btn"
                  onClick={loadIngredients}
                >
                  🔄 Actualiser
                </button>
              </div>

              {ingredients.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '2rem', color: '#94a3b8' }}>
                  Aucun ingrédient dans le stock
                </div>
              ) : (
                <div style={{ overflowX: 'auto' }}>
                  <table className="table">
                    <thead>
                      <tr>
                        <th>Ingrédient</th>
                        <th>Stock</th>
                        <th>Seuil</th>
                        <th>Statut</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {ingredients.map(ingredient => {
                        const isLow = ingredient.quantity <= ingredient.threshold;
                        const isCritical = ingredient.quantity <= ingredient.threshold * 0.5;
                        
                        return (
                          <tr key={ingredient.id}>
                            <td>
                              <div style={{ fontWeight: '500' }}>
                                {ingredient.name}
                              </div>
                              <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                                ID: {ingredient.id}
                              </div>
                            </td>
                            <td>
                              <div style={{ fontWeight: '600' }}>
                                {ingredient.quantity} {ingredient.unit}
                              </div>
                            </td>
                            <td>
                              {ingredient.threshold} {ingredient.unit}
                            </td>
                            <td>
                              <span className={isCritical ? 'badge badge-danger' : isLow ? 'badge badge-warning' : 'badge badge-success'}>
                                {isCritical ? 'CRITIQUE' : isLow ? 'FAIBLE' : 'OK'}
                              </span>
                            </td>
                            <td>
                              <div style={{ display: 'flex', gap: '0.25rem' }}>
                                <button
                                  className="btn btn-success"
                                  style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                                  onClick={() => handleUpdateQuantity(ingredient.id, ingredient.quantity, 10)}
                                >
                                  +10
                                </button>
                                <button
                                  className="btn btn-primary"
                                  style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                                  onClick={() => handleUpdateQuantity(ingredient.id, ingredient.quantity, 50)}
                                >
                                  +50
                                </button>
                                <button
                                  className="btn btn-warning"
                                  style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                                  onClick={() => handleEdit(ingredient)}
                                >
                                  ✏️
                                </button>
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
      </div>
    </div>
  );
};

export default Ingredients;