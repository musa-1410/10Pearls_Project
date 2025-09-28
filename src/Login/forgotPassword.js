import React, { useState } from 'react';
import './forgotPassword.css';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Show loading toast
    const toastId = toast.loading("Sending reset link...");

    try {
      await axios.post('http://localhost:8080/api/auth/forgot-password', {
        email: email
      });

      // Update toast on success
      toast.update(toastId, {
        render: "Reset link sent to your Gmail!",
        type: "success",
        isLoading: false,
        autoClose: 3000,
        closeOnClick: true
      });

      setEmail('');
    } catch (err) {
      // Update toast on error
      toast.update(toastId, {
        render: err.response?.data || "Failed to send reset link",
        type: "error",
        isLoading: false,
        autoClose: 3000,
        closeOnClick: true
      });
    }
  };

  return (
    <div className="forgot-container">
      <form className="forgot-card" onSubmit={handleSubmit}>
        <h2>Forgot Password</h2>
        <p>Enter your email address and we’ll send you a reset link.</p>

        <input
          type="email"
          placeholder="Enter your email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <button type="submit">Send Reset Link</button>

        <div className="back-to-login">
          <Link to="/">← Back to Login</Link>
        </div>
      </form>

      {/* Toast container */}
      <ToastContainer position="top-center" autoClose={3000} />
    </div>
  );
};

export default ForgotPassword;