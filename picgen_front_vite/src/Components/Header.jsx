import { useState, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useToken } from "./TokenContext";
import { logout } from "../api/AuthAPI";
import { useUser } from "./UserContext";

// Font Awesome imports
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBars, faX } from "@fortawesome/free-solid-svg-icons";

const Header = ({ isGuest }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { changeToken } = useToken();
  const { setUser } = useUser();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const touchStartX = useRef(0);
  const touchEndX = useRef(0);

  const handleLogin = () => navigate('/login');
  const handleSignUp = () => navigate('/register');
  const handleLogOut = async () => {
    await logout();
    changeToken(null, null);
    navigate('/login');
  };
  const openSavedPics = () => navigate('/savedPics');
  const openAccount = () => navigate("/account");
  const goHome = () => navigate("/home");

  // Smooth menu closing handler
  const handleNavClick = (callback) => {
    setMobileMenuOpen(false); // start closing animation
    setTimeout(callback, 50); // wait for transition to finish before navigating
  };

  const linkButton = `
    px-4 py-2 rounded transition-all duration-100
    text-white font-medium
    border-b-2 border-transparent
    hover:border-indigo-400 hover:text-indigo-400
    focus:outline-none
  `;

  const pageTitles = {
    "/login": "Log In",
    "/register": "Sign Up",
    "/home": "Home",
    "/account": "Account",
    "/savedPics": "Saved Pictures",
  };
  const currentPage = pageTitles[location.pathname] || "App";

  // Swipe handlers
  const handleTouchStart = (e) => { touchStartX.current = e.touches[0].clientX; };
  const handleTouchMove = (e) => { touchEndX.current = e.touches[0].clientX; };
  const handleTouchEnd = () => {
    const deltaX = touchStartX.current - touchEndX.current;
    if (deltaX > 50) { // swipe left
        setMobileMenuOpen(false);
    } else if (deltaX < -50) { // swipe right
        setMobileMenuOpen(false);
    }
  };

  return (
<header className="sticky top-0 z-50 w-full max-w-4xl mx-auto
                   bg-gray-950
                   flex flex-col md:flex-row md:justify-end md:space-x-5 py-2">

      
      {/* Mobile title and hamburger */}
      {!isGuest && (
        <div className="flex justify-between items-center md:hidden px-4 mb-3">
          <h1 className="text-white font-bold">{currentPage}</h1>
          <button onClick={() => setMobileMenuOpen(!mobileMenuOpen)} className="text-white text-xl">
            <FontAwesomeIcon icon={mobileMenuOpen ? faX : faBars} />
          </button>
        </div>
      )}

      {/* Mobile Menu */}
      <div
        className={`
            fixed top-0 right-0 h-full w-64 bg-gray-900 z-50
            transform transition-transform duration-100 ease-in-out
            ${mobileMenuOpen ? "translate-x-0" : "translate-x-full"}
            md:static md:translate-x-0 md:w-auto md:h-auto md:bg-transparent md:flex
            flex-col md:flex-row md:items-center space-y-4 md:space-y-0 md:space-x-5 px-4 py-9 md:py-0
        `}
        onTouchStart={handleTouchStart}
        onTouchMove={handleTouchMove}
        onTouchEnd={handleTouchEnd}
      >
        {isGuest ? (
          <>
            <div className="border-b border-gray-700 md:border-none last:border-b-0">
              <button onClick={() => handleNavClick(handleLogin)} className={linkButton}>Log In</button>
            </div>
            <div className="border-b border-gray-700 md:border-none last:border-b-0">
              <button onClick={() => handleNavClick(handleSignUp)} className={linkButton}>Sign Up</button>
            </div>
          </>
        ) : (
          <>
            <div className="border-b border-gray-700 md:border-none last:border-b-0">
              <button onClick={() => handleNavClick(goHome)} className={linkButton}>Home</button>
            </div>
            
            <div className="border-b border-gray-700 md:border-none last:border-b-0">
              <button onClick={() => handleNavClick(openSavedPics)} className={linkButton}>Saved Pictures</button>
            </div>
            <div className="border-b border-gray-700 md:border-none last:border-b-0">
              <button onClick={() => handleNavClick(openAccount)} className={linkButton}>Account</button>
            </div>
            <div className="border-b border-gray-700 md:border-none last:border-b-0">
              <button onClick={() => handleNavClick(handleLogOut)} className={linkButton}>Log Out</button>
            </div>
          </>
        )}
      </div>

    </header>
  );
};

export default Header;
