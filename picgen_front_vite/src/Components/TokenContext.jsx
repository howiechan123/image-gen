import { createContext, useState, useContext, useEffect } from "react";
import SpringAPI from "../api/SpringAPI";
import axios from "axios";

const TokenContext = createContext();

export const useToken = () => useContext(TokenContext);

export const TokenProvider = ({children}) => {
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);


    const changeToken = (t) => {
        setToken(t);
    }

    const noInterceptor = axios.create({
        baseURL: import.meta.env.VITE_API_SPRING_BASE_URL,
        withCredentials: true,
    });

    useEffect(() => {
        const tryRefresh = async () => {
            try {
                //dont use interceptor for initial call, avoid refresh loop
                const res = await noInterceptor.post("/public/login/refresh");
                const newToken = res.data.token;
                setToken(newToken);
                console.log("TT", res);
            } catch (err) {
                console.log("Refresh failed, user must log in again");
                setToken(null);
            } finally {
                setLoading(false);
            }
        };
        tryRefresh();
    }, []);


    return (
        <TokenContext.Provider value = {{token, changeToken, loading}}>
            {children}
        </TokenContext.Provider>
    );
}
