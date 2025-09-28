import React from 'react';
import { Outlet, useLocation } from 'react-router-dom';

const Layout = () => {
  const location = useLocation();

  const handleLogout = () => {
    localStorage.clear();
    sessionStorage.clear();
    window.location.href = "/login";
  };

  // Check if it's the profile page
  const isProfilePage = location.pathname === "/profile";

  return (
    <>
      <Header onLogout={handleLogout} variant={isProfilePage ? 'profile' : 'default'} />
      <Outlet />
    </>
  );
};

export default Layout;