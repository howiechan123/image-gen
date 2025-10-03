import { useState } from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';
import { useToken } from "./TokenContext"
import { login } from "../api/AuthAPI";
import { useUser } from "./UserContext";

function Login() {

    const [email, setEmail] = useState();
    const [password, setPassword] = useState();
    const { changeToken } = useToken();
    const {setUser} = useUser();


    const navigate = useNavigate();

    const setUsername = (usr) => setEmail(usr.target.value);
    const setPass = (usr) => setPassword(usr.target.value);

    const handleLogin = async () => {
        try {
            const response = await login(email, password);

            if (response.data.success) {
            changeToken(response.data.token, response.data.user);
            navigate("/home");
            console.log(response);
            setUser(response.data.user);
            } else {
            window.alert("Wrong username or password!");
            }
        } catch (error) {
            window.alert(error.message);
        }
    };

    const handleSignUp = () => navigate('/register');
    const handleGuest = () => navigate('/guest');

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4">
            <div className="w-full max-w-md bg-gray-900/80 backdrop-blur-md rounded-2xl shadow-2xl p-8">
                <h1 className="text-2xl font-bold text-center mb-6">Generate your own Pixel Art!</h1>
                <h2 className="text-lg font-semibold text-center mb-8">Login or Sign Up to Begin</h2>

                <div className="mb-4">
                    <label htmlFor="userName" className="block text-sm font-medium mb-2">Email</label>
                    <input 
                        className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        type="text"
                        id="userName"
                        onChange={setUsername}
                    />
                </div>

                <div className="mb-6">
                    <label htmlFor="password" className="block text-sm font-medium mb-2">Password</label>
                    <input 
                        className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        type="password"
                        id="password"
                        onChange={setPass}
                    />
                </div>

                <div className="flex flex-col space-y-3">
                    <button 
                        id="login" 
                        onClick={handleLogin}
                        className="w-full py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 transition-colors shadow-md"
                    >
                        Login
                    </button>
                    <button 
                        id="signUp" 
                        onClick={handleSignUp}
                        className="w-full py-2 rounded-lg bg-green-600 hover:bg-green-500 active:bg-green-700 transition-colors shadow-md"
                    >
                        Sign Up
                    </button>
                    <button 
                        id="guest" 
                        onClick={handleGuest}
                        className="w-full py-2 rounded-lg bg-gray-700 hover:bg-gray-600 active:bg-gray-800 transition-colors shadow-md"
                    >
                        Continue as Guest
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Login;
