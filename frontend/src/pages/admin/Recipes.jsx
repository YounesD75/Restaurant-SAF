import { useEffect, useState } from 'react';
import { recipeApi } from '../../api/recipeApi';
import { recipeItemApi } from '../../api/recipeItemApi';
import { ingredientApi } from '../../api/ingredientApi';

const Recipes = () => {
  const [recipes, setRecipes] = useState([]);
  const [ingredients, setIngredients] = useState([]);
  const [recipeItems, setRecipeItems] = useState({});
  const [showForm, setShowForm] = useState(false);
  const [showAddItem, setShowAddItem] = useState(null);
  const [form, setForm] = useState({ dishName: '' });
  const [itemForm, setItemForm] = useState({
    recipeId: '',
    ingredientId: '',
    quantityNeeded: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const loadRecipes = async () => {
    try {
      const res = await recipeApi.getAll();
      setRecipes(res.data);
    } catch (err) {
      setError('Erreur lors du chargement des recettes');
      console.error(err);
    }
  };

  const loadIngredients = async () => {
    try {
      const res = await ingredientApi.getAll();
      setIngredients(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const loadRecipeItems = async (recipeId) => {
    try {
      const res = await recipeItemApi.getItemsByRecipe(recipeId);
      setRecipeItems(prev => ({ ...prev, [recipeId]: res.data }));
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadRecipes();
    loadIngredients();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await recipeApi.create(form);
      setSuccess('Recette créée avec succès');
      setForm({ dishName: '' });
      setShowForm(false);
      await loadRecipes();
    } catch (err) {
      setError('Erreur lors de la création');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddItem = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await recipeItemApi.addItem(itemForm);
      setSuccess('Ingrédient ajouté à la recette');
      setItemForm({
        recipeId: '',
        ingredientId: '',
        quantityNeeded: '',
      });
      setShowAddItem(null);
      await loadRecipeItems(itemForm.recipeId);
    } catch (err) {
      setError('Erreur lors de l\'ajout');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="main-content">
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
          <div>
            <h1 style={{ fontSize: '2rem', color: '#1e293b', marginBottom: '0.5rem' }}>
              🍔 Gestion des Recettes
            </h1>
            <p style={{ color: '#64748b' }}>
              Créez et gérez vos recettes
            </p>
          </div>
          
          <button
            className="btn btn-success"
            onClick={() => setShowForm(!showForm)}
          >
            {showForm ? '− Annuler' : '+ Nouvelle recette'}
          </button>
        </div>

        {/* Messages */}
        {error && <div className="alert alert-error mb-4">{error}</div>}
        {success && <div className="alert alert-success mb-4">{success}</div>}

        {/* Formulaire de création */}
        {showForm && (
          <div className="card mb-4">
            <h3 className="mb-3">Nouvelle recette</h3>
            <form onSubmit={handleSubmit} style={{ display: 'flex', gap: '1rem' }}>
              <input
                type="text"
                className="form-control"
                placeholder="Nom de la recette (ex: Pizza Margherita)"
                value={form.dishName}
                onChange={(e) => setForm({ dishName: e.target.value })}
                required
                style={{ flex: 1 }}
              />
              <button
                type="submit"
                className="btn btn-primary"
                disabled={loading}
              >
                {loading ? 'Création...' : 'Créer'}
              </button>
            </form>
          </div>
        )}

        {/* Liste des recettes */}
        <div className="card">
          <h3 className="mb-3">📋 Liste des recettes ({recipes.length})</h3>
          
          {recipes.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '2rem', color: '#94a3b8' }}>
              Aucune recette créée
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {recipes.map(recipe => (
                <div key={recipe.id} style={{
                  background: '#f8fafc',
                  borderRadius: '0.5rem',
                  padding: '1rem',
                  borderLeft: '4px solid #3b82f6'
                }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                    <div>
                      <h4 style={{ fontSize: '1.125rem', color: '#1e293b' }}>
                        {recipe.dishName}
                      </h4>
                      <div style={{ fontSize: '0.875rem', color: '#64748b' }}>
                        ID: {recipe.id}
                      </div>
                    </div>
                    
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <button
                        className="btn"
                        onClick={() => {
                          setShowAddItem(showAddItem === recipe.id ? null : recipe.id);
                          setItemForm({ ...itemForm, recipeId: recipe.id });
                          loadRecipeItems(recipe.id);
                        }}
                      >
                        {showAddItem === recipe.id ? '− Annuler' : '+ Ajouter ingrédient'}
                      </button>
                      <button
                        className="btn btn-danger"
                      >
                        Supprimer
                      </button>
                    </div>
                  </div>

                  {/* Formulaire pour ajouter un ingrédient */}
                  {showAddItem === recipe.id && (
                    <div style={{ margin: '1rem 0', padding: '1rem', background: 'white', borderRadius: '0.375rem' }}>
                      <h5 className="mb-2">Ajouter un ingrédient à cette recette</h5>
                      <form onSubmit={handleAddItem} style={{ display: 'flex', gap: '1rem' }}>
                        <select
                          className="form-control"
                          value={itemForm.ingredientId}
                          onChange={(e) => setItemForm({ ...itemForm, ingredientId: e.target.value })}
                          required
                          style={{ flex: 1 }}
                        >
                          <option value="">Sélectionner un ingrédient</option>
                          {ingredients.map(ing => (
                            <option key={ing.id} value={ing.id}>
                              {ing.name} ({ing.quantity} {ing.unit})
                            </option>
                          ))}
                        </select>
                        <input
                          type="number"
                          className="form-control"
                          placeholder="Quantité nécessaire"
                          value={itemForm.quantityNeeded}
                          onChange={(e) => setItemForm({ ...itemForm, quantityNeeded: e.target.value })}
                          required
                          min="0"
                          step="0.01"
                          style={{ width: '150px' }}
                        />
                        <button
                          type="submit"
                          className="btn btn-primary"
                          disabled={loading}
                        >
                          Ajouter
                        </button>
                      </form>
                    </div>
                  )}

                  {/* Liste des ingrédients de la recette */}
                  {recipeItems[recipe.id] && recipeItems[recipe.id].length > 0 && (
                    <div style={{ marginTop: '1rem' }}>
                      <div style={{ fontSize: '0.875rem', color: '#64748b', marginBottom: '0.5rem' }}>
                        Ingrédients nécessaires:
                      </div>
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                        {recipeItems[recipe.id].map(item => {
                          const ingredient = ingredients.find(i => i.id === item.ingredientId);
                          return (
                            <div key={item.id} style={{
                              background: '#e2e8f0',
                              padding: '0.25rem 0.5rem',
                              borderRadius: '0.25rem',
                              fontSize: '0.75rem',
                              color: '#475569'
                            }}>
                              {ingredient?.name || 'Inconnu'}: {item.quantityNeeded} {ingredient?.unit || ''}
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Recipes;