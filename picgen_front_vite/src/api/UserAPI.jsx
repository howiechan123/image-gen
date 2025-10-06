import SpringAPI from "./SpringAPI";

export const updateUser = async(userId, name, email, password) => {
    try{
        const response = await SpringAPI.put(`api/Users/${userId}`, {name, email, password});
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}

