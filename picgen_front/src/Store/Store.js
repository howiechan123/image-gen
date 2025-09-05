import { configureStore } from '@reduxjs/toolkit';
import RootReducer from '../Reducers/RootReducer';

const Store = configureStore({
    reducer: RootReducer
});

export default Store;
