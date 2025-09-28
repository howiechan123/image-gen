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


function Home() {
    const [openPics, setOpenPics] = useState(false);

    const openPicsModal = () => setOpenPics(true);
    const closePicsModal = () => setOpenPics(false);

    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4 py-8">
            
            <Guest isGuest={false} />
            

            <SavedPicsModal isOpen={openPics} onClose={closePicsModal} />
        </div>
    );
}

export default Home;
