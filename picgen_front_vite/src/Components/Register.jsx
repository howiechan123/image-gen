// import { useState } from "react";
// import { Navigate, useNavigate } from 'react-router-dom';
// import axios from "axios";



// function Register() {
//     const REGISTER_URL = import.meta.env.VITE_API_REGISTER_URL;

//     const [name, setName] = useState();
//     const [email, setEmail] = useState();
//     const [password, setPassword] = useState();
//     const [reenter, setReenter] = useState();

//     const navigate = useNavigate();

//     const updateName = (usr) => {
//         setName(usr.target.value);
//     }
//     const updateEmail = (usr) => {
//         setEmail(usr.target.value);
//     }
//     const updatePassword = (usr) => {
//         setPassword(usr.target.value);
//     }
//     const updateReenter = (usr) => {
//         setReenter(usr.target.value);
//     }


//     const handleRegister = async () => {

//         if(name === undefined || email === undefined || password === undefined || reenter === undefined) {
//             window.alert("please fill out all sections to sign up");
//             return;
//         }
        
//         if(name === "" || email === "" || password === "" || reenter === "") {
//             window.alert("please fill out all areas to sign up");
//             return;
//         }
//         if(password !== reenter){
//             window.alert("passwords do not match");
//             return;
//         }

//         const data = {
//             "name": name,
//             "email": email,
//             "password": password
//         }
//         try{
//             const response = await axios.post(REGISTER_URL, data)
//             if(response.data.success){
//                 window.alert("Succesfully Registered");
//                 navigate('/login');
//             }
//             else{
//                 window.alert("User already exists");
//             }
//         }
//         catch(error){
//             window.alert(error);
//         }
//     }

//     return(
//         <div>
//             <header>Sign Up</header>
//             <div>Name</div>
//             <input id="name" type="text" onChange={updateName}></input>
//             <div>Email</div>
//             <input id="email" type="text" onChange={updateEmail}></input>
//             <div>Password</div>
//             <input id="password" type="text" onChange={updatePassword}></input>
//             <div>Re-enter password</div>
//             <input id="reenter" type="text" onChange={updateReenter}></input>
//             <div></div>
//             <button id="register" onClick={() => handleRegister()}>Register</button>
//         </div>
//     );
// }

// export default Register;

import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import axios from "axios";

function Register() {
    const REGISTER_URL = import.meta.env.VITE_API_REGISTER_URL;

    const [name, setName] = useState();
    const [email, setEmail] = useState();
    const [password, setPassword] = useState();
    const [reenter, setReenter] = useState();

    const navigate = useNavigate();

    const updateName = (usr) => setName(usr.target.value);
    const updateEmail = (usr) => setEmail(usr.target.value);
    const updatePassword = (usr) => setPassword(usr.target.value);
    const updateReenter = (usr) => setReenter(usr.target.value);

    const handleRegister = async () => {
        if (!name || !email || !password || !reenter) {
            return window.alert("Please fill out all fields to sign up");
        }
        if (password !== reenter) {
            return window.alert("Passwords do not match");
        }

        const data = { name, email, password };
        try {
            const response = await axios.post(REGISTER_URL, data);
            if (response.data.success) {
                window.alert("Successfully Registered");
                navigate('/login');
            } else {
                window.alert("User already exists");
            }
        } catch (error) {
            window.alert(error);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white px-4">
            <div className="w-full max-w-md bg-gray-900/80 backdrop-blur-md rounded-2xl shadow-2xl p-8 space-y-6">
                <h1 className="text-2xl font-bold text-center mb-4">Sign Up</h1>

                <div className="space-y-4">
                    <div>
                        <label htmlFor="name" className="block text-sm font-medium mb-1">Name</label>
                        <input
                            id="name"
                            type="text"
                            onChange={updateName}
                            className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        />
                    </div>

                    <div>
                        <label htmlFor="email" className="block text-sm font-medium mb-1">Email</label>
                        <input
                            id="email"
                            type="text"
                            onChange={updateEmail}
                            className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        />
                    </div>

                    <div>
                        <label htmlFor="password" className="block text-sm font-medium mb-1">Password</label>
                        <input
                            id="password"
                            type="password"
                            onChange={updatePassword}
                            className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        />
                    </div>

                    <div>
                        <label htmlFor="reenter" className="block text-sm font-medium mb-1">Re-enter Password</label>
                        <input
                            id="reenter"
                            type="password"
                            onChange={updateReenter}
                            className="w-full px-4 py-2 rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        />
                    </div>
                </div>

                <button
                    id="register"
                    onClick={handleRegister}
                    className="w-full py-2 rounded-lg bg-green-600 hover:bg-green-500 active:bg-green-700 transition-colors shadow-md"
                >
                    Register
                </button>
            </div>
        </div>
    );
}

export default Register;
