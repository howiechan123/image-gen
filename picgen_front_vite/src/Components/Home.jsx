
import { useState } from "react";
import Guest from "./Guest";
import { useNavigate } from "react-router-dom";
import SavedPicsModal from "./SavedPicsModal";


function Home() {
    const [openPics, setOpenPics] = useState(false);

    const openPicsModal = () => setOpenPics(true);
    const closePicsModal = () => setOpenPics(false);

    return (
        <div>
            
            <Guest isGuest={false} />
            

            <SavedPicsModal isOpen={openPics} onClose={closePicsModal} />
        </div>
    );
}

export default Home;
