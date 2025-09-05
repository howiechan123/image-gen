import { useState } from "react";
import { Navigate, useNavigate } from 'react-router-dom';
import axios from "axios";



function Register() {
    const REGISTER_URL = process.env.REACT_APP_API_REGISTER_URL;

    const [name, setName] = useState();
    const [email, setEmail] = useState();
    const [password, setPassword] = useState();
    const [reenter, setReenter] = useState();

    const navigate = useNavigate();

    const updateName = (usr) => {
        setName(usr.target.value);
    }
    const updateEmail = (usr) => {
        setEmail(usr.target.value);
    }
    const updatePassword = (usr) => {
        setPassword(usr.target.value);
    }
    const updateReenter = (usr) => {
        setReenter(usr.target.value);
    }


    const handleRegister = async () => {

        if(name === undefined || email === undefined || password === undefined || reenter === undefined) {
            window.alert("please fill out all sections to sign up");
            return;
        }
        
        if(name === "" || email === "" || password === "" || reenter === "") {
            window.alert("please fill out all areas to sign up");
            return;
        }
        if(password !== reenter){
            window.alert("passwords do not match");
            return;
        }

        const data = {
            "name": name,
            "email": email,
            "password": password
        }
        try{
            const response = await axios.post(REGISTER_URL, data)
            if(response.data.success){
                window.alert("Succesfully Registered");
                navigate('/login');
            }
            else{
                window.alert("User already exists");
            }
        }
        catch(error){
            window.alert(error);
        }
    }

    return(
        <div>
            <header>Sign Up</header>
            <div>Name</div>
            <input id="name" type="text" onChange={updateName}></input>
            <div>Email</div>
            <input id="email" type="text" onChange={updateEmail}></input>
            <div>Password</div>
            <input id="password" type="text" onChange={updatePassword}></input>
            <div>Re-enter password</div>
            <input id="reenter" type="text" onChange={updateReenter}></input>
            <div></div>
            <button id="register" onClick={() => handleRegister()}>Register</button>
        </div>
    );
}

export default Register;