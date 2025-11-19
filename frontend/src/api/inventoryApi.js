import axios from "axios";

const BASE = "http://localhost:8083/api/ingredients";

export const getIngredients = () => axios.get(BASE);
export const createIngredient = (data) => axios.post(BASE, data);
export const updateQuantity = (id, qty) =>
  axios.patch(`${BASE}/${id}/quantity`, { newQuantity: qty });
