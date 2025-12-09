import { inventoryAxios } from './axiosConfig';

export const recipeApi = {
  // Créer une recette
  create: (data) => inventoryAxios.post('/recipes', data),
  
  // Récupérer toutes les recettes
  getAll: () => inventoryAxios.get('/recipes'),
  
  // Récupérer une recette par ID
  getById: (id) => inventoryAxios.get(`/recipes/${id}`),
};