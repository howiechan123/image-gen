// import { useState, useEffect } from "react";
// import axios from "axios";
// import { Navigate, useNavigate } from 'react-router-dom';
// import { useToken } from "./TokenContext"

// function Login() {
    
//     const LOGIN_URL = import.meta.env.VITE_API_LOGIN_URL;

//     const [email, setEmail] = useState();
//     const [password, setPassword] = useState();
//     const { token, changeToken } = useToken();

//     const navigate = useNavigate();

//     const setUser = (usr) => {
//         setEmail(usr.target.value);
//     }

//     const setPass = (usr) => {
//         setPassword(usr.target.value);
//     }

//     const handleLogin = async () => {
//         const data = {
//             "email": email,
//             "password": password
//         } 
//         try{
//             const response = await axios.post(LOGIN_URL, data)
//             if(response.data.success){
//                 changeToken(response.data.token);
//                 window.alert("Success");
//                 navigate('/home');
//                 console.log(response);
//             }
//             else{
//                 window.alert("Wrong username or password!");
//             }
//         }
//         catch(error) {
//             if (error.response) {
//                 if (error.response.status === 401) {
//                     window.alert("Invalid credentials! Please check your username or password.");
//                 } else {
//                     window.alert(error.response.status);
//                 }
//             } else if (error.request) {
//                 window.alert("No response from the server. Please check your connection.");
//             } else {
//                 window.alert("An unexpected error occurred.");
//             }
//         }
//     }

//     const handleSignUp = () => {
//         navigate('/register');
//     }

//     const handleGuest = () => {
//         navigate('/guest');
//     }

//     return(
//         <div>
//             <div>
//                 Generate your own Pixel Art!
//             </div>
//             <header>Login or Sign Up to Begin</header>
//             <header>Email</header>
//             <input className="email_input" type="text" id="userName" onChange={setUser}></input>
//             <header>Password</header>
//             <input className="password_input" type="text" id="password" onChange={setPass}></input>
//             <div></div>
//             <button id="login" onClick={() => handleLogin()}>Login</button>
//             <button id="signUp" onClick = {() => handleSignUp()}>Sign Up</button>
//             <div></div>
//             <button id="guest" onClick = {() => handleGuest()}>Continue as guest</button>
            
//         </div>
        
//     );
// }

// export default Login;

import { useState } from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';
import { useToken } from "./TokenContext"

function Login() {
    const LOGIN_URL = import.meta.env.VITE_API_LOGIN_URL;

    const [email, setEmail] = useState();
    const [password, setPassword] = useState();
    const { changeToken } = useToken();

    const navigate = useNavigate();

    const setUser = (usr) => setEmail(usr.target.value);
    const setPass = (usr) => setPassword(usr.target.value);

    const handleLogin = async () => {
        const data = { email, password };
        try {
            const response = await axios.post(LOGIN_URL, data)
            if (response.data.success) {
                changeToken(response.data.token);
                window.alert("Success");
                navigate('/home');
                console.log(response);
            } else {
                window.alert("Wrong username or password!");
            }
        } catch (error) {
            if (error.response) {
                if (error.response.status === 401) {
                    window.alert("Invalid credentials! Please check your username or password.");
                } else {
                    window.alert(error.response.status);
                }
            } else if (error.request) {
                window.alert("No response from the server. Please check your connection.");
            } else {
                window.alert("An unexpected error occurred.");
            }
        }
    }

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
                        onChange={setUser}
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
