import { useNavigate } from "react-router-dom";
import { useToken } from "./TokenContext";

const Header = ({isGuest}) => {
    let navigate = useNavigate();
    let {changeToken} = useToken();

    const handleLogin = () => navigate('/login');
    const handleSignUp = () => navigate('/register');

    const handleLogOut = () => {
        changeToken(null);
        navigate('/login');
    };

    const openSavedPics = () => navigate('/savedPics');

    return(
        <div>
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

            {!isGuest && (
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
            )}

        </div>
    );
}

export default Header;