import { inventoryAxios } from './axiosConfig';

export const ingredientApi = {
  // Créer un ingrédient
  create: (data) => inventoryAxios.post('/ingredients', data),
  
  // Récupérer tous les ingrédients
  getAll: () => inventoryAxios.get('/ingredients'),
  
  // Récupérer un ingrédient par ID
  getById: (id) => inventoryAxios.get(`/ingredients/${id}`),
  
  // Mettre à jour la quantité
  updateQuantity: (id, newQuantity) => 
    inventoryAxios.patch(`/ingredients/${id}/quantity`, { newQuantity }),
};