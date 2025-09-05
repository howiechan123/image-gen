import './App.css';
import Login from './Components/Login.js';
import Register from './Components/Register.js';
import Home from './Components/Home.js';
import SavedPics from './Components/SavedPics.js';
import Guest from './Components/Guest.js';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import { TokenProvider } from './Components/TokenContext.js';
import ProtectedRoute from './Components/ProtectedRoute.js';

function App() {
  return (
    <TokenProvider>
      <Router>
        <div className='App'>
          <Routes>
            <Route path="/guest" element={<Guest />} />
            <Route path="/home" element={<ProtectedRoute><Home /></ProtectedRoute>} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/savedPics" element={<ProtectedRoute><SavedPics /></ProtectedRoute>} />
            <Route path="*" element={<h1>404 - Page Not Found</h1>} />
          </Routes>
        </div>
      </Router>
    </TokenProvider>
  );
}

export default App;
