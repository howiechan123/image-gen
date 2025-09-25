import { useContext, useEffect, useState } from "react";
import axios from "axios";
import {useToken} from "./TokenContext";

function SavedPics() {
    const PICS_URL = import.meta.env.VITE_API_PICTURES_URL;
    const {token, changeToken} = useToken();
    const[pics, setPics] = useState(["No Pictures"]);


    const getPics = async(token) => {
        try{
            const data = {
                headers: {Authorization: `Bearer ${token}`}
            }
            const response = await axios.get(PICS_URL, data);
            if(response.data.success){
                let pics = response.data.dto.pics
                let paths = pics.map(pics => pics.filePath);
                setPics(paths);
            }
            console.log(response.data.dto.pics);
        }
        catch(error) {
            window.alert(error);
        }
    };

    useEffect(() => {
        getPics(token);
    }, [])

    return(
        <div>
            {pics}
        </div>
    );
}

export default SavedPics;