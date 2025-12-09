import { inventoryAxios } from './axiosConfig';

export const recipeItemApi = {
  // Ajouter un ingrédient à une recette
  addItem: (data) => inventoryAxios.post('/recipes/items', data),
  
  // Récupérer les ingrédients d'une recette
  getItemsByRecipe: (recipeId) => inventoryAxios.get(`/recipes/items/${recipeId}`),
};