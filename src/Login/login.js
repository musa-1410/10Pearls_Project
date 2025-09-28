import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Login.css';
import axios from 'axios';
import { FaEnvelope, FaLock, FaEye, FaEyeSlash } from 'react-icons/fa';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { jwtDecode } from 'jwt-decode'; // ✅ correct import

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [showPassword, setShowPassword] = useState(true);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg('');

    if (!email || !password) {
      setErrorMsg('All fields are required.');
      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email)) {
      setErrorMsg('Enter a valid email address.');
      return;
    }

    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        email,
        password,
      });

      const token = response.data.token;
      localStorage.setItem("token", token);

      const decoded = jwtDecode(token); // ✅ correct usage
      const roles = decoded?.authorities || [];

      localStorage.setItem("user", JSON.stringify({
        name: response.data.name,
        email: response.data.email,
        roles: roles,
      }));

      console.log('Login success:', decoded);
      toast.success('Login successful!');

      setTimeout(() => {
        if (roles.includes("ROLE_ADMIN")) {
          navigate('/contacts');
        } else {
          navigate('/contacts');
        }
      }, 2000);

    } catch (err) {
      console.error('Login failed:', err);
      if (err.response?.status === 401) {
        setErrorMsg('Invalid credentials.');
      } else {
        setErrorMsg('Login failed. Try again.');
      }
    }
  };

  const togglePassword = () => {
    setShowPassword((prev) => !prev);
  };

  return (
    <div className="login-container">
      <video autoPlay muted loop className="video-bg">
        <source
          src="https://d1jj76g3lut4fe.cloudfront.net/processed/thumb/mTFw20T58n60rF7t2Y.mp4"
          type="video/mp4"
        />
        Your browser does not support the video tag.
      </video>

      <div className="container">
        <div className="row justify-content-end">
          <div className="col-lg-4 col-md-5 col-sm-12" style={{ paddingRight: '40px' }}>
            <form onSubmit={handleSubmit} className="login-form shadow">
              <h2 className="text-center mb-4">Welcome</h2>

              {errorMsg && <p className="error-msg text-danger text-center">{errorMsg}</p>}

              <div className="input-wrapper position-relative mb-3">
                <FaEnvelope className="icon position-absolute top-50 translate-middle-y" />
                <input
                  type="email"
                  className="form-control ps-5"
                  placeholder="Email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>

              <div className="input-wrapper position-relative mb-3">
                <FaLock className="icon position-absolute top-50 translate-middle-y" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  className="form-control ps-5 pe-5"
                  placeholder="Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <span className="toggle-icon position-absolute top-50 end-0 translate-middle-y me-3" onClick={togglePassword}>
                  {showPassword ? <FaEye /> : <FaEyeSlash />}
                </span>
              </div>

              <div className="d-flex justify-content-between align-items-center mb-3">
                <div className="custom-checkbox-wrapper">
                  <input type="checkbox" id="remember" className="custom-checkbox" />
                  <label htmlFor="remember">Remember me</label>
                </div>
                <Link to="/forgot" className="forgot-link">Forgot password?</Link>
              </div>

              <button type="submit" className="btn btn-primary w-100">Login</button>

              <p className="register-link text-center mt-3">
                Don’t have an account? <Link to="/register">Signup</Link>
              </p>
            </form>
          </div>
        </div>
      </div>

      <ToastContainer position="top-center" autoClose={2000} />
    </div>
  );
};

export default Login;