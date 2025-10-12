import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import { useToken } from "./TokenContext";
import { login } from "../api/AuthAPI";
import { useUser } from "./UserContext";
import Loading from "./Loading";

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const { changeToken, loading, setLoading } = useToken();
    const { setUser } = useUser();
    const navigate = useNavigate();

    const handleLogin = async () => {
        setLoading(true);
        try {
            const response = await login(email, password);

            if (response.data.success) {
                changeToken(response.data.token, response.data.user);
                setUser(response.data.user);
                navigate("/home");
            } else {
                window.alert("Wrong username or password!");
            }
        } catch (error) {
            window.alert(error.message);
        }
        finally{
            setLoading(false);
        }
    };

    const handleSignUp = () => navigate('/register');
    const handleGuest = () => navigate('/guest');

    const linkStyle = "text-indigo-400 font-medium hover:text-indigo-600 cursor-pointer transition-colors duration-100";

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4">
            {loading && <Loading/>}
            <div className="w-full max-w-md bg-gray-900/80 backdrop-blur-md rounded-2xl shadow-2xl p-8">
                <h1 className="text-2xl font-bold text-center mb-6">Generate AI art</h1>
                <h2 className="text-lg font-semibold text-center mb-8">Login or Sign Up to Save Images</h2>

                <div className="mb-4">
                    <label htmlFor="email" className="block text-sm font-medium mb-2">Email</label>
                    <input 
                        className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        type="text"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </div>

                <div className="mb-6">
                    <label htmlFor="password" className="block text-sm font-medium mb-2">Password</label>
                    <input 
                        className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>

                <button 
                    onClick={handleLogin}
                    className="w-full py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 transition-colors shadow-md mb-6"
                >
                    Login
                </button>

                <div className="text-center text-sm space-y-2">
                    <div>
                        <span>Don't have an account? </span>
                        <span onClick={handleSignUp} className={linkStyle}>Sign Up</span>
                    </div>
                    <div>
                        <span>Or continue as </span>
                        <span onClick={handleGuest} className={linkStyle}>Guest</span>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Login;
