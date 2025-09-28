import React, { useState } from 'react';
import axios from 'axios';
import { useLocation, useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './ResetPassword.css'; // Import external CSS

const ResetPassword = () => {
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const location = useLocation();
  const navigate = useNavigate();

  const queryParams = new URLSearchParams(location.search);
  const email = queryParams.get('email');

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!password || !confirmPassword) {
      toast.error('Both fields are required.');
      return;
    }

    if (password !== confirmPassword) {
      toast.error('Passwords do not match.');
      return;
    }

    try {
     await axios.post('http://localhost:8080/api/auth/reset-password', {
  email: email,
  password: password
});

      toast.success('Password reset successful!');
      setTimeout(() => {
        navigate('/');
      }, 2000);
    } catch (err) {
      console.error(err);
      toast.error('Reset failed');
    }
  };

  return (
    <div className="reset-wrapper">
      <form className="reset-form" onSubmit={handleSubmit}>
        <h2 className="reset-title">Reset Your Password</h2>

        <input
          type="password"
          placeholder="New Password"
          className="reset-input"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <input
          type="password"
          placeholder="Confirm New Password"
          className="reset-input"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />

        <button type="submit" className="reset-button">
          Reset Password
        </button>
      </form>

      <ToastContainer position="top-center" />
    </div>
  );
};

export default ResetPassword;