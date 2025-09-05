import { useContext, useEffect } from "react";
import axios from "axios";
import {useToken} from "./TokenContext";

function SavedPics() {
    const PICS_URL = process.env.REACT_APP_API_PICTURES_URL;
    const {token, changeToken} = useToken();
    let pics = [];
    const getPics = async(token) => {
        try{
            const data = {
                headers: {Authorization: 'Bearer ${token}'}
            }
            console.log("here");
            const response = await axios.get(PICS_URL, data);
            // if(response.data.success){
            //     pics = response.data;
            //     console.log(response.data);
            // }
            // pics = response.data;
            
        }
        catch(error) {
            window.alert(error);
        }
    };

    useEffect(() => {
        getPics();
    }, [])

    return(
        <div>
            {pics}
        </div>
    );
}

export default SavedPics;