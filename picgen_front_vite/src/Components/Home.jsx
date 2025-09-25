// import { useState, useEffect } from "react";
// import "./Guest";
// import Guest from "./Guest";
// import { useNavigate } from "react-router-dom";
// import SavedPicsModal from "./SavedPicsModal";
// import { useToken } from "./TokenContext"

// function Home() {
//     const [openPics, setOpenPics] = useState(false);
//     const navigate = useNavigate();
//     const { token, changeToken } = useToken();
    
//     const handleLogOut = () => {
//         changeToken(null);
//         navigate('/login');
//     }
//     const openPicsModal = () => {
//         setOpenPics(true);
//     }
//     const closePicsModal = () => {
//         setOpenPics(false);
//     }
//     const openSavedPics = () => {
//         navigate('/savedPics');
//     }

//     return(
//         <div>
//             <div>
//                 <button onClick={() => openSavedPics()}>Saved Pictures</button>
//                 <button onClick={() => handleLogOut()}>Log Out</button>
//             </div>
//             <Guest isGuest={false}></Guest>
//             <SavedPicsModal isOpen={openPics} onClose={closePicsModal}></SavedPicsModal>
//         </div>
//     );
// }

// export default Home;

import { useState } from "react";
import Guest from "./Guest";
import { useNavigate } from "react-router-dom";
import SavedPicsModal from "./SavedPicsModal";
import { useToken } from "./TokenContext";

function Home() {
    const [openPics, setOpenPics] = useState(false);
    const navigate = useNavigate();
    const { changeToken } = useToken();

    const handleLogOut = () => {
        changeToken(null);
        navigate('/login');
    };
    const openPicsModal = () => setOpenPics(true);
    const closePicsModal = () => setOpenPics(false);
    const openSavedPics = () => navigate('/savedPics');

    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4 py-8">
            <header className="w-full max-w-4xl mx-auto flex justify-end space-x-4 py-4 border-b border-gray-700">
                <button
                    onClick={openSavedPics}
                    className="px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 transition-colors shadow-md"
                >
                    Saved Pictures
                </button>
                <button
                    onClick={handleLogOut}
                    className="px-4 py-2 rounded-lg bg-gray-700 hover:bg-gray-600 active:bg-gray-800 transition-colors shadow-md"
                >
                    Log Out
                </button>
        </header>

            
        <Guest isGuest={false} />
        

        <SavedPicsModal isOpen={openPics} onClose={closePicsModal} />
        </div>
    );
}

export default Home;
