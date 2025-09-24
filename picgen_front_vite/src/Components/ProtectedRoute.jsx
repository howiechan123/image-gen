import { useContext, useEffect } from "react";
import { useToken } from "./TokenContext";
import { useNavigate } from "react-router-dom";

function ProtectedRoute({children}) {
    const { token } = useToken();
    const navigate = useNavigate();
    
    useEffect(() => {
        console.log(token);
    },[token])
    useEffect(() => {
        if(!token){
            navigate('/login');
            window.alert("Please Login");
        }
    }, [token, navigate]);
    
    return token ? children : null;
}

export default ProtectedRoute;