import axios from "axios";

const BASE = "http://localhost:8081/api/orders";

export const getOrders = () => axios.get(BASE);
export const updateOrderStatus = (id, status) =>
  axios.patch(`${BASE}/${id}/status?status=${status}`);
