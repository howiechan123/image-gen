
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
