import { useNavigate } from "react-router-dom";
import { useToken } from "./TokenContext";
import { logout } from "../api/AuthAPI";
import { useUser } from "./UserContext";

const Header = ({ isGuest }) => {
    const navigate = useNavigate();
    const { changeToken } = useToken();
    const { setUser } = useUser();

    const handleLogin = () => navigate('/login');
    const handleSignUp = () => navigate('/register');

    const handleLogOut = async () => {
        await logout();
        changeToken(null, null);
        navigate('/login');
    };

    const openSavedPics = () => navigate('/savedPics');
    const openAccount = () => navigate("/account");
    const goHome = () => navigate("/home");

    const linkButton = `
        px-3 py-1 rounded transition-all duration-200
        text-white font-medium
        hover:border-b hover:border-indigo-400 hover:text-indigo-400
        focus:outline-none
    `;

    return (
        <div>
            {isGuest && (
                <header className="w-full max-w-4xl mx-auto flex justify-end space-x-4 py-4 border-b border-gray-700">
                    <button onClick={handleLogin} className={linkButton}>
                        Log In
                    </button>
                    <button onClick={handleSignUp} className={linkButton}>
                        Sign Up
                    </button>
                </header>
            )}

            {!isGuest && (
                <header className="w-full max-w-4xl mx-auto flex justify-end space-x-4 py-4 border-b border-gray-700">
                    <button onClick={goHome} className={linkButton}>
                        Home
                    </button>
                    <button onClick={openAccount} className={linkButton}>
                        Account
                    </button>
                    <button onClick={openSavedPics} className={linkButton}>
                        Saved Pictures
                    </button>
                    <button onClick={handleLogOut} className={linkButton}>
                        Log Out
                    </button>
                </header>
            )}
        </div>
    );
}

export default Header;
