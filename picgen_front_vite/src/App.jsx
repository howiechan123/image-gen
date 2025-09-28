import './App.css';
import Login from './Components/Login.jsx';
import Register from './Components/Register.jsx';
import Home from './Components/Home.jsx';
import SavedPics from './Components/SavedPics.jsx';
import Guest from './Components/Guest.jsx';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import { TokenProvider, useToken } from './Components/TokenContext.jsx';
import ProtectedRoute from './Components/ProtectedRoute.jsx';
import { setupInterceptors } from './api/SpringAPI';
import { useEffect } from 'react';

const AppContent = () => {
  const tokenContext = useToken();

  useEffect(() => {
    setupInterceptors(tokenContext);
  }, [tokenContext]);

  return (
    <Routes>
      <Route path="/guest" element={<Guest />} />
      <Route path="/home" element={<ProtectedRoute><Home /></ProtectedRoute>} />
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/savedPics" element={<ProtectedRoute><SavedPics /></ProtectedRoute>} />
      <Route path="*" element={<h1>404 - Page Not Found</h1>} />
    </Routes>
  );
};

function App() {
  return (
    <TokenProvider>
      <Router>
        <div className='App'>
          <AppContent />
        </div>
      </Router>
    </TokenProvider>
  );
}

export default App;
