import axios from "axios";

const BASE = "http://localhost:8083/api/recipes/items";

export const addRecipeItem = (data) => axios.post(BASE, data);
export const getRecipeItems = (recipeId) =>
  axios.get(`${BASE}/${recipeId}`);
