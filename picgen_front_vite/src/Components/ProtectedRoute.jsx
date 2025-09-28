import { useContext, useEffect } from "react";
import { useToken } from "./TokenContext";
import Loading from "./Loading.jsx";
import { Navigate } from "react-router-dom";


function ProtectedRoute({children}) {
    const { token, loading } = useToken();
    
    if (loading) return <Loading/>;
    if (!token) return <Navigate to="/login" replace />;

    return children;
    
}

export default ProtectedRoute;