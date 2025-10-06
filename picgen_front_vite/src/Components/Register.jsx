import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import { register } from "../api/AuthAPI";
import { useToken } from "./TokenContext";
import { isValidEmail, isValidPassword } from "../Util/validateStrings";

function Register() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [reenter, setReenter] = useState("");

    const navigate = useNavigate();
    const { changeToken } = useToken();

    const handleRegister = async () => {
        if(!isValidEmail(email)){
            return window.alert("Enter a valid email");
        }
        if(!isValidEmail(password)){
            return window.alert("Enter a valid password");
        }
        if (!name || !email || !password || !reenter) {
            return window.alert("Please fill out all fields to sign up");
        }

        if (password !== reenter) {
            return window.alert("Passwords do not match");
        }

        try {
            const response = await register(name, email, password);

            if (response.data.success) {
                navigate("/home");
            } else {
                console.log(response);
                window.alert("User already exists");
            }
        } catch (error) {
            window.alert(error.message);
        }
    };

    const linkStyle = "text-indigo-400 font-medium hover:text-indigo-600 cursor-pointer transition-colors duration-100";

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4">
            <div className="w-full max-w-md bg-gray-900/80 backdrop-blur-md rounded-2xl shadow-2xl p-8 space-y-6">
                <h1 className="text-2xl font-bold text-center mb-4">Sign Up</h1>

                <div className="space-y-4">
                    <div>
                        <label htmlFor="name" className="block text-lg font-medium mb-1">Username</label>
                        <input
                            id="name"
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        />
                    </div>

                    <div>
                        <label htmlFor="email" className="block text-lg font-medium mb-1">Email</label>
                        <input
                            id="email"
                            type="text"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        />
                    </div>

                    <div>
                        <label htmlFor="password" className="block text-lg font-medium mb-1">Password</label>
                        <div className="block text-xs font-medium mb-1">*Passwords must be at least 8 characters long and contain a number, uppercase letter, and lowercase letter.</div>
  
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        />
                    </div>

                    <div>
                        <label htmlFor="reenter" className="block text-lg font-medium mb-1">Re-enter Password</label>
                        <input
                            id="reenter"
                            type="password"
                            value={reenter}
                            onChange={(e) => setReenter(e.target.value)}
                            className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        />
                    </div>
                </div>

                <button
                    onClick={handleRegister}
                    className="w-full py-2 rounded-lg bg-green-600 hover:bg-green-500 active:bg-green-700 transition-colors shadow-md"
                >
                    Register
                </button>

                <div className="text-center text-lg mt-4">
                    <span>Already have an account? </span>
                    <span onClick={() => navigate("/login")} className={linkStyle}>Login</span>
                </div>
            </div>
        </div>
    );
}

export default Register;
