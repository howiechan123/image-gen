import SpringAPI from "./SpringAPI";

export const login = async (email, password) => {
  try {
    const response = await SpringAPI.post("public/login", { email, password });
    return response;
  } catch (err) {
    throw new Error(err.response?.data?.message || "Login failed");
  }
};

export const register = async (name, email, password) => {
  try {
    const response = await SpringAPI.post("public/register", { name, email, password });
    return response;
  } catch (err) {
    throw new Error(err.response?.data?.message || "Registration failed");
  }
};