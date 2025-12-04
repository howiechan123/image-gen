import SpringAPI from "./SpringAPI";
import axios from "axios";

export const createPost = async(picture, user, caption) => {
    try{
        const response = await SpringAPI.post("api/posts/create", {picture, user, caption});
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}

export const deletePost = async(postId) => {
    try{
        const response = await SpringAPI.delete(`api/posts/delete/${postId}`);
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}

export const getUserPosts = async() => {
    try{
        const response = await SpringAPI.get("api/posts/userPosts");
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}

export const getFeed = async() => {
    try{
        const response = await SpringAPI.get("api/posts/getFeed");
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}

//param likeOrDislike: 1 = like, -1 = dislike
export const updatePostLikes = async(postId, likeOrDislike) => {
    try{
        const response = await SpringAPI.post(`api/posts/delete/${postId}`, likeOrDislike);
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}

export const updatePostCaption = async(postId, caption) => {
    try{
        const response = await SpringAPI.post(`api/posts/delete/${postId}`, caption);
        return response;
    }
    catch(err){
        throw new Error(err);
    }
}





