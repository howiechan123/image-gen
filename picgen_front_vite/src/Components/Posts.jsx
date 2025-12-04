import Header from "./Header";
import { getUserPosts } from "../api/PostsAPI";
import { useEffect } from "react";

let Posts = () => {

    let loadPosts = async() => {
        let response = await getUserPosts();
        
    }

    useEffect = () => {() => {
        loadPosts();
    }
    
    , []}


    return (
        <div className="min-h-screen bg-gray-950 text-white px-4 py-8">
            <Header isGuest={false}/>
            hello posts
        </div>
    );
}

export default Posts;