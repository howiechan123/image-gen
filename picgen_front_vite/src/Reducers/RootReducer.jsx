import { combineReducers } from 'redux';
import AuthSlice from './AuthReducer';
import loadSlice from './LoadingReducer';

const RootReducer = combineReducers({
  auth: AuthSlice,
  loading: loadSlice
});

export default RootReducer;