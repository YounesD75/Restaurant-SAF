import axios from "axios";

const BASE = "http://localhost:8083/api/recipes";

export const getRecipes = () => axios.get(BASE);
export const createRecipe = (data) => axios.post(BASE, data);
