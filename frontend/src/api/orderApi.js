import { orderAxios } from './axiosConfig';

export const orderApi = {
  // Passer une commande
  create: (data) => orderAxios.post('/orders', data),

  // Voir les reçus (commandes validées)
  getReceipts: () => orderAxios.get('/receipts'),

  // Détail d’un reçu
  getReceiptByOrderId: (orderId) =>
    orderAxios.get(`/receipts/${orderId}`),

  // Menu
  getMenu: () => orderAxios.get('/menu'),

  // Trésorerie
  getTreasury: () => orderAxios.get('/treasury'),
};
