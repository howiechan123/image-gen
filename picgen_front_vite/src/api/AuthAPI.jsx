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

export const logout = async() => {
  try{
    const response = await SpringAPI.post("public/login/logout");
    console.log("logged out", response);
    return response;
  }
  catch(err){
    throw new Error(err.response?.data?.message || "Log out failed");
  }
}