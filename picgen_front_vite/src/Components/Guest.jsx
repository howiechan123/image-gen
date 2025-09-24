import { useState } from "react";
import ImageModal from "./ImageModal";
import pog from "./pog.png";
import loadgif from "./loading.gif";
import axios from "axios";
import { useSelector, useDispatch } from "react-redux";
import ButtonWrapper from "./ButtonWrapper";
import { useNavigate } from "react-router-dom";


function Guest({isGuest = true}) {
    const navigate = useNavigate();
    const [prompt, setPrompt] = useState();
    const updatePrompt = (usr) => {
        setPrompt(usr.target.value);
    }

    const [imageModalOpen, setImageModalOpen] = useState(false);
    const openModal = () => {
        setImageModalOpen(true);
    }
    const closeModal = () => {
        setImageModalOpen(false);
        setLoading(false);
    }

    const [image, setImage] = useState(loadgif);
    const [loading, setLoading] = useState(false);

    const handleGenerate = async () => {
        console.log(prompt);
        if(prompt === undefined || prompt === "") {
            return window.alert("Please enter a prompt");
        }
        setLoading(true);
        openModal();
        setImage(loadgif);
        const data = {
            "prompt": prompt
        }
        try{
            const response = await axios.post("http://127.0.0.1:8000/generate-image",data)
            if(response.data.success){
                setImage(`data:image/png;base64,${response.data.image}`)
                openModal();
            }
        }
        catch(error){
            window.alert(error);
        }
    }

    const handleLogin = () => {
        navigate('/login');
    }

    const handleSignUp = () => {
        navigate('/register');
    }

    return(
        <div>
            {isGuest && <div>
                <button onClick={() => handleLogin()}>Log In</button>
                <button onClick={() => handleSignUp()}>Sign Up</button>
            </div>}
            <header>What image would you like to create?</header>
            <textarea id="prompt" type="text" onChange={updatePrompt}></textarea>
            <div></div>
            <ButtonWrapper clickable={!loading}>
                <button id="generate" onClick={() => handleGenerate()}>Generate!</button>
            </ButtonWrapper>
            <ImageModal isOpen={imageModalOpen} onClose={() => closeModal()} image={image}/>
        </div>
    );
}

export default Guest;