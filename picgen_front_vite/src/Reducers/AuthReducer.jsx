import { createSlice } from '@reduxjs/toolkit';

const initialState = {
    user: {},
    isLoggedIn: false
}

const AuthSlice = createSlice({
    name:'auth',
    initialState,
    reducers: {
        LOGIN: (state, action) => {
            state.user = action.payload;
            state.isLoggedIn = true;
        },
        LOGOUT: (state, action) => {
            state.user = null;
            state.isLoggedIn = false;
        }
    }
});

export const {LOGIN, LOGOUT} = AuthSlice.actions;
export default AuthSlice;