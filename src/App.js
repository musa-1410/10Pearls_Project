import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './Login/login';  
import ForgotPassword from './Login/forgotPassword';
import Register from './Login/register';
import ResetPassword from './Login/resetPassword';
import Contacts from './Contact Managment screen/ContactList';
import ContactDetails from './Contact Managment screen/ContactDetails'; 
import UserProfile from './User Profile/UserProfile';
import Layout from './layout/layout'; 
import AdminDashboard from './Admin/AdminDashboard';

function App() {
  return (
    <Router>
      <Routes>

        {/* Routes WITHOUT Header */}
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/forgot" element={<ForgotPassword />} />
        <Route path="/register" element={<Register />} />
        <Route path="/reset-password" element={<ResetPassword />} />
        <Route path="/admin-dashboard" element={<AdminDashboard />} />

        {/* Routes */}
        <Route element={<Layout />}>
          <Route path="/contacts" element={<Contacts />} />
          <Route path="/contacts/:id" element={<ContactDetails />} /> {/* NEW ROUTE */}
          <Route path="/profile" element={<UserProfile />} />
          
        </Route>

      </Routes>
    </Router>
  );
}

export default App;