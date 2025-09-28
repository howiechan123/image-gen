import SpringAPI from "./SpringAPI";

export const savePicture = async() => {
    try{
        
    }
    catch(err){
        throw new Error(err.response?.data?.message || "Error saving picture");
    }
}

export const getPictures = async() => {
    try{
        const response = await SpringAPI.get("api/Pictures/user");
        return response;
    }
    catch(err){
        throw new Error(err.response?.data?.message || "Error getting pictures");
    }
}