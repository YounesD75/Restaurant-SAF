import { orderAxios } from './axiosConfig';

export const orderApi = {
  // Créer une commande
  create: (data) => orderAxios.post('/orders', data),
  
  // Récupérer toutes les commandes
  getAll: () => orderAxios.get('/orders'),
  
  // Récupérer une commande par ID
  getById: (id) => orderAxios.get(`/orders/${id}`),
  
  // Mettre à jour le statut
  updateStatus: (id, status) => 
    orderAxios.patch(`/orders/${id}/status?status=${status}`),
  
  // Supprimer une commande
  delete: (id) => orderAxios.delete(`/orders/${id}`),
  
  // Démarrer le paiement
  startPayment: (id) => orderAxios.post(`/orders/${id}/pay`),
  
  // Confirmer le paiement
  confirmPayment: (paymentId) => 
    orderAxios.post(`/orders/payments/confirm/${paymentId}`),
};