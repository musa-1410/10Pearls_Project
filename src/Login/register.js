import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './register.css';
import axios from 'axios';
import { FaUser, FaEnvelope, FaLock, FaEye, FaEyeSlash } from 'react-icons/fa';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const Register = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isAdmin, setIsAdmin] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const navigate = useNavigate();

  const togglePassword = () => setShowPassword(prev => !prev);
  const toggleConfirmPassword = () => setShowConfirmPassword(prev => !prev);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!name || !email || !password || !confirmPassword) {
      setErrorMsg('All fields are required.');
      return;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setErrorMsg('Please enter a valid email.');
      return;
    }

    if (password !== confirmPassword) {
      setErrorMsg('Passwords do not match.');
      return;
    }

    try {
      const response = await axios.post('http://localhost:8080/api/auth/register', {
        name,
        email,
        password,
        isAdmin,
      });

      if (response.data === 'User registered successfully.') {
        toast.success('Registered successfully!');
        setTimeout(() => {
          navigate('/');
        }, 2000);
      } else {
        toast.error(response.data || 'Registration failed.');
      }

      setErrorMsg('');
      setName('');
      setEmail('');
      setPassword('');
      setConfirmPassword('');
      setIsAdmin(false);

    } catch (err) {
      console.error('Error occurred:', err);
      const errorMessage = err.response?.data || 'Registration failed.';
      setErrorMsg(errorMessage);
      toast.error(errorMessage);
    }
  };

  return (
    <div className="register-container">
      <form className="register-form" onSubmit={handleSubmit}>
        <h2>Register</h2>

        {errorMsg && <p className="error-msg">{errorMsg}</p>}

        <div className="input-wrapper">
          <FaUser className="icon" />
          <input
            type="text"
            placeholder="Full Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        <div className="input-wrapper">
          <FaEnvelope className="icon" />
          <input
            type="email"
            placeholder="Email Address"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>

        <div className="input-wrapper position-relative">
          <FaLock className="icon" />
          <input
            type={showPassword ? 'text' : 'password'}
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <span className="toggle-icon" onClick={togglePassword}>
            {showPassword ? <FaEye/> : <FaEyeSlash  />}
          </span>
        </div>

        <div className="input-wrapper position-relative">
          <FaLock className="icon" />
          <input
            type={showConfirmPassword ? 'text' : 'password'}
            placeholder="Confirm Password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />
          <span className="toggle-icon" onClick={toggleConfirmPassword}>
            {showConfirmPassword ? <FaEye/> : <FaEyeSlash  />}
          </span>
        </div>

        <div className="checkbox-wrapper">
          <input
            type="checkbox"
            id="adminCheckbox"
            checked={isAdmin}
            onChange={(e) => setIsAdmin(e.target.checked)}
          />
          <label htmlFor="adminCheckbox">Register as Admin</label>
        </div>

        <button type="submit">Register</button>

        <p className="login-link">
          Already have an account? <Link to="/">Login</Link>
        </p>
      </form>

      <ToastContainer position="top-center" autoClose={2000} />
    </div>
  );
};

export default Register;


