import { useContext, useEffect, useState } from "react";
import axios from "axios";
import { getPictures } from "../api/PictureAPI";
import Header from "./Header";

function SavedPics() {
    const PICS_URL = import.meta.env.VITE_API_PICTURES_URL;
    const[pics, setPics] = useState(["No Pictures"]);


    const getPics = async() => {
        try{
            const response = await getPictures();
            console.log("PP", response);
            if(response.data.success){
                let pics = response.data.dto.pics
                let paths = pics.map(pics => pics.filePath);
                setPics(paths);
            }
            console.log(response);
        }
        catch(error) {
        }
    };

    useEffect(() => {
        getPics();
    }, [])

    return(
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4 py-8">
            <Header isGuest={false}/>
            {pics}
        </div>
    );
}

export default SavedPics;