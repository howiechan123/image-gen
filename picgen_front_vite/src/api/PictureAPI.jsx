import SpringAPI from "./SpringAPI";

export const savePicture = async(fileName, filePath) => {
    try{
        const response = await SpringAPI.post("api/Pictures/save", {fileName, filePath});
        
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}

export const getPictures = async() => {
    try{

        const response = await SpringAPI.get("api/Pictures/user");
        
        return response;
    }
    catch(err){

        throw new Error(err);
    }
}