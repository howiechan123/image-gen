import { createSlice } from "@reduxjs/toolkit";


const initialState = {
    loading: false
}

const loadSlice = createSlice({
    name: "loading",
    initialState,
    reducers: {
        LOADING: (state) => {
            state.loading = true;
        },
        NOTLOADING: (state) => {
            state.loading = false;
        }
    }
});

export const {LOADING, NOTLOADING} = loadSlice.actions;
export default loadSlice;