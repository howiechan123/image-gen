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

export const deletePicture = async(picId) => {
    try{
        const response = await SpringAPI.delete(`api/Pictures/${picId}`);
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}

export const editPictureName = async(picId, newName) => {
    try{
        const response = await SpringAPI.put(`api/Pictures/${picId}`, {newName});
        return response;
    }
    catch(err) {
        throw new Error(err);
    }
}

export const generateImage = async(prompt, dimensions, inference_steps, guidance_scale) => {
    try{
        const response = await SpringAPI.post("api/Pictures/generate_image", {prompt, dimensions, inference_steps, guidance_scale});
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}

export const pollHF = async(event_id) => {
    try{
        const response = await SpringAPI.post("api/Pictures/pollHF", {event_id});
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}

