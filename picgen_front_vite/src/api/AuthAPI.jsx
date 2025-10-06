import SpringAPI from "./SpringAPI";

export const login = async (email, password) => {
  try {
    const response = await SpringAPI.post("public/auth/login", { email, password });
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
    const response = await SpringAPI.post("public/auth/logout");
    console.log("logged out", response);
    return response;
  }
  catch(err){
    throw new Error(err.response?.data?.message || "Log out failed");
  }
}

export const deleteUser = async(userId) => {
    try{
        const response = await SpringAPI.delete(`public/auth/${userId}`);
        return response;
        
    }
    catch(err){
        throw new Error(err);
    }
}

export const updateUser = async(userId, name, email, password) => {
    try{
        const response = await SpringAPI.put(`public/auth/${userId}`, {name, email, password});
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}