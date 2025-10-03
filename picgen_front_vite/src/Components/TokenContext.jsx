import { createContext, useState, useContext, useEffect } from "react";
import SpringAPI from "../api/SpringAPI";
import axios from "axios";
import { useUser } from "./UserContext";

const TokenContext = createContext();
export const useToken = () => useContext(TokenContext);

export const TokenProvider = ({children}) => {
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);
    const {setUser} = useUser();


    const changeToken = (t, u) => {
        setToken(t);
        setUser(u);
    }

    const noInterceptor = axios.create({
        baseURL: import.meta.env.VITE_API_SPRING_BASE_URL,
        withCredentials: true,
    });

    useEffect(() => {
        const tryRefresh = async () => {
            try {
                //dont use interceptor for initial call, avoid refresh loop
                const res = await noInterceptor.post("/public/auth/refresh");
                const newToken = res.data.token;
                setToken(newToken);
                setUser(res.data.user);
            } catch (err) {
                setToken(null);
                setUser(null);
            } finally {
                setLoading(false);
            }
        };
        tryRefresh();
    }, []);


    return (
        <TokenContext.Provider value = {{token, changeToken, loading, setLoading}}>
            {children}
        </TokenContext.Provider>
    );
}
