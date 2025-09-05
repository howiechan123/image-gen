import { createContext, useState, useContext } from "react";

const TokenContext = createContext();

export const useToken = () => useContext(TokenContext);

export const TokenProvider = ({children}) => {
    const [token, setToken] = useState(null);
    const changeToken = (t) => {
        setToken(t);
    }
    return (
        <TokenContext.Provider value = {{token, changeToken}}>
            {children}
        </TokenContext.Provider>
    );
}
