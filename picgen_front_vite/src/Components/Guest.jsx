// import { useState } from "react";
// import ImageModal from "./ImageModal";
// import pog from "./pog.png";
// import loadgif from "./loading.gif";
// import axios from "axios";
// import { useSelector, useDispatch } from "react-redux";
// import ButtonWrapper from "./ButtonWrapper";
// import { useNavigate } from "react-router-dom";


// function Guest({isGuest = true}) {
//     const navigate = useNavigate();
//     const [prompt, setPrompt] = useState();
//     const updatePrompt = (usr) => {
//         setPrompt(usr.target.value);
//     }

//     const [imageModalOpen, setImageModalOpen] = useState(false);
//     const openModal = () => {
//         setImageModalOpen(true);
//     }
//     const closeModal = () => {
//         setImageModalOpen(false);
//         setLoading(false);
//     }

//     const [image, setImage] = useState(loadgif);
//     const [loading, setLoading] = useState(false);

//     const handleGenerate = async () => {
//         console.log(prompt);
//         if(prompt === undefined || prompt === "") {
//             return window.alert("Please enter a prompt");
//         }
//         setLoading(true);
//         openModal();
//         setImage(loadgif);
//         const data = {
//             "prompt": prompt
//         }
//         try{
//             const response = await axios.post("http://127.0.0.1:8000/generate-image",data)
//             if(response.data.success){
//                 setImage(`data:image/png;base64,${response.data.image}`)
//                 openModal();
//             }
//         }
//         catch(error){
//             window.alert(error);
//         }
//     }

//     const handleLogin = () => {
//         navigate('/login');
//     }

//     const handleSignUp = () => {
//         navigate('/register');
//     }

//     return(
//         <div>
//             {isGuest && <div>
//                 <button onClick={() => handleLogin()}>Log In</button>
//                 <button onClick={() => handleSignUp()}>Sign Up</button>
//             </div>}
//             <header>What image would you like to create?</header>
//             <textarea id="prompt" type="text" onChange={updatePrompt}></textarea>
//             <div></div>
//             <ButtonWrapper clickable={!loading}>
//                 <button id="generate" onClick={() => handleGenerate()}>Generate!</button>
//             </ButtonWrapper>
//             <ImageModal isOpen={imageModalOpen} onClose={() => closeModal()} image={image}/>
//         </div>
//     );
// }

// export default Guest;
import { useState } from "react";
import ImageModal from "./ImageModal";
import loadgif from "./loading.gif";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import ButtonWrapper from "./ButtonWrapper";

function Guest({ isGuest = true }) {
    const navigate = useNavigate();
    const [prompt, setPrompt] = useState();
    const [imageModalOpen, setImageModalOpen] = useState(false);
    const [image, setImage] = useState(loadgif);
    const [loading, setLoading] = useState(false);

    const updatePrompt = (usr) => setPrompt(usr.target.value);
    const openModal = () => setImageModalOpen(true);
    const closeModal = () => {
        setImageModalOpen(false);
        setLoading(false);
    };

    const handleGenerate = async () => {
        if (!prompt) return window.alert("Please enter a prompt");
        setLoading(true);
        openModal();
        setImage(loadgif);
        const data = { prompt };
        try {
            const response = await axios.post("http://127.0.0.1:8000/generate-image", data);
            if (response.data.success) {
                setImage(`data:image/png;base64,${response.data.image}`);
                openModal();
            }
        } catch (error) {
            window.alert(error);
        }
    };

    const handleLogin = () => navigate('/login');
    const handleSignUp = () => navigate('/register');

    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4">

            <ImageModal isOpen={imageModalOpen} onClose={closeModal} image={image} />
            {isGuest && (
                <header className="w-full max-w-4xl mx-auto flex justify-end space-x-4 py-4 border-b border-gray-700">
                    <button 
                        onClick={handleLogin}
                        className="px-4 py-2 rounded-lg bg-gray-700 hover:bg-gray-600 active:bg-gray-800 transition-colors shadow-md"
                    >
                        Log In
                    </button>
                    <button 
                        onClick={handleSignUp}
                        className="px-4 py-2 rounded-lg bg-green-600 hover:bg-green-500 active:bg-green-700 transition-colors shadow-md"
                    >
                        Sign Up
                    </button>
                </header>
            )}

            
            <div className="flex items-center justify-center mt-8">
                <div className="w-full max-w-lg bg-gray-900/80 backdrop-blur-md rounded-2xl shadow-2xl p-8 space-y-6">
                    <header className="text-xl font-semibold text-center">
                        What image would you like to create?
                    </header>

                    <textarea
                        id="prompt"
                        onChange={updatePrompt}
                        className="w-full h-32 px-4 py-3 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none resize-none"
                        placeholder="Describe your pixel art idea..."
                    ></textarea>

                    <div className="flex justify-center">
                        <ButtonWrapper clickable={!loading}>
                            <button
                                id="generate"
                                onClick={handleGenerate}
                                className="px-6 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 transition-colors shadow-md disabled:opacity-50"
                            >
                                {loading ? "Generating..." : "Generate!"}
                            </button>
                        </ButtonWrapper>
                    </div>

                    
                </div>
            </div>
        </div>
    );
}

export default Guest;
