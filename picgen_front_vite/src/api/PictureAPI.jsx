import SpringAPI from "./SpringAPI";

export const savePicture = async(fileName, filePath) => {
    try{
        const response = await SpringAPI.post("api/Pictures/save", {fileName, filePath});
        
        return response;
    }
    catch(err){
        throw new Error(err.response?.data?.message || "Error saving picture");
    }
}

export const getPictures = async() => {
    console.log("getpics");
    try{
        console.log("VVVVV");
        const response = await SpringAPI.get("api/Pictures/user");
        
        return response;
    }
    catch(err){
        console.log("PPPP");
        throw new Error(err);
    }
}