import { useState, useEffect } from "react";
import axios from "axios";
import { Navigate, useNavigate } from 'react-router-dom';
import { useToken } from "./TokenContext"

function Login() {
    
    const LOGIN_URL = process.env.REACT_APP_API_LOGIN_URL;

    const [email, setEmail] = useState();
    const [password, setPassword] = useState();
    const { token, changeToken } = useToken();

    const navigate = useNavigate();

    const setUser = (usr) => {
        setEmail(usr.target.value);
    }

    const setPass = (usr) => {
        setPassword(usr.target.value);
    }

    const handleLogin = async () => {
        const data = {
            "email": email,
            "password": password
        } 
        try{
            const response = await axios.post(LOGIN_URL, data)
            if(response.data.success){
                changeToken(response.data.token);
                window.alert("Success");
                navigate('/home');
            }
            else{
                window.alert("Wrong username or password!");
            }
        }
        catch(error) {
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

    const handleSignUp = () => {
        navigate('/register');
    }

    const handleGuest = () => {
        navigate('/guest');
    }

    return(
        <div>
            <div>
                Generate your own Pixel Art!
            </div>
            <header>Login or Sign Up to Begin</header>
            <header>Email</header>
            <input className="email_input" type="text" id="userName" onChange={setUser}></input>
            <header>Password</header>
            <input className="password_input" type="text" id="password" onChange={setPass}></input>
            <div></div>
            <button id="login" onClick={() => handleLogin()}>Login</button>
            <button id="signUp" onClick = {() => handleSignUp()}>Sign Up</button>
            <div></div>
            <button id="guest" onClick = {() => handleGuest()}>Continue as guest</button>
            
        </div>
        
    );
}

export default Login;