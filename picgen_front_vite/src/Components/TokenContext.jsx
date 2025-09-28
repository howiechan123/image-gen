import { createContext, useState, useContext, useEffect } from "react";
import SpringAPI from "../api/SpringAPI";

const TokenContext = createContext();

export const useToken = () => useContext(TokenContext);

export const TokenProvider = ({children}) => {
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);


    const changeToken = (t) => {
        setToken(t);
    }


    useEffect(() => {
        const tryRefresh = async () => {
            try {
                const res = await SpringAPI.post("/public/login/refresh");
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
