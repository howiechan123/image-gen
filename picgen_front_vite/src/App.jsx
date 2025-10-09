import './App.css';
import Login from './Components/Login.jsx';
import Register from './Components/Register.jsx';
import Home from './Components/Home.jsx';
import SavedPics from './Components/SavedPics.jsx';
import Guest from './Components/Guest.jsx';
import Account from './Components/Account.jsx';
import { BrowserRouter as Router, Route, Routes, Link, useNavigate } from 'react-router-dom';
import { TokenProvider, useToken } from './Components/TokenContext.jsx';
import ProtectedRoute from './Components/ProtectedRoute.jsx';
import { setupInterceptors } from './api/SpringAPI';
import { useEffect } from 'react';
import { UserProvider } from './Components/UserContext.jsx';

const AppContent = () => {
  const tokenContext = useToken();
  const navigate = useNavigate();

  useEffect(() => {
    setupInterceptors(tokenContext, navigate);
  }, [tokenContext]);

  return (
    <Routes>
      <Route path="/guest" element={<Guest />} />
      <Route path="/home" element={<ProtectedRoute><Home /></ProtectedRoute>} />
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/savedPics" element={<ProtectedRoute><SavedPics /></ProtectedRoute>} />
      <Route path="/account" element={<ProtectedRoute><Account /></ProtectedRoute>} />
      <Route path="*" element={<h1>404 - Page Not Found</h1>} />
    </Routes>
  );
};

function App() {
  return (
    <UserProvider>
    <TokenProvider>
      
      <Router>
        <div className='App'>
          <AppContent />
        </div>
      </Router>
      
    </TokenProvider>
    </UserProvider>
  );
}

export default App;
