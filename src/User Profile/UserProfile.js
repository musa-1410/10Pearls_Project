import React, { useState } from 'react';
import './UserProfile.css';
import { FaUserCircle, FaEye, FaEyeSlash, FaLock } from 'react-icons/fa';
// import { GiBackwardTime } from "react-icons/gi"; // ❌ unused, removed

import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const UserProfile = () => {
  const navigate = useNavigate();

  // eslint-disable-next-line no-unused-vars
  const [user, setUser] = useState(() => {
    const storedUser = localStorage.getItem("user");
    return storedUser ? JSON.parse(storedUser) : { name: '', email: '' };
  });

  const [showModal, setShowModal] = useState(false);
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [showCurrent, setShowCurrent] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  // eslint-disable-next-line no-unused-vars
  const [errors, setErrors] = useState({});

  const resetPasswordFields = () => {
    setCurrentPassword('');
    setNewPassword('');
    setConfirmPassword('');
    setErrors({});
    setShowCurrent(false);
    setShowNew(false);
    setShowConfirm(false);
  };

  const handlePasswordChange = async () => {
    const newErrors = {};

    if (!currentPassword) newErrors.currentPassword = "Current password is required";
    if (!newPassword) newErrors.newPassword = "New password is required";
    if (!confirmPassword) newErrors.confirmPassword = "Confirm password is required";

    if (newPassword && confirmPassword && newPassword !== confirmPassword) {
      newErrors.confirmPassword = "New password and confirm password do not match";
    }

    setErrors(newErrors);
    if (Object.keys(newErrors).length > 0) return;

    const token = localStorage.getItem("token");
    if (!token) {
      toast.error("Session expired. Please login again.");
      navigate("/login");
      return;
    }

    try {
      const res = await axios.post(
        "http://localhost:8080/api/auth/change-password",
        {
          currentPassword,
          newPassword,
          email: user.email,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const msg = res.data?.message || res.data;

      if (msg === "Password updated successfully." || msg === "Password changed successfully") {
        toast.success("Password changed successfully");

        setTimeout(() => {
          setShowModal(false);
          resetPasswordFields();
        }, 1000);
      } else {
        toast.error(msg || "Failed to change password");
      }
    } catch (error) {
      toast.error(error.response?.data || "Failed to change password");
    }
  };

  return (
    <div className="user-profile">
      <div className="profile-card">
        <FaUserCircle className="avatar-icon" />
        <h2>{user.name}</h2>
        <p><strong>Email:</strong> {user.email}</p>

        <div className="btn-group">
          <button className="btn" onClick={() => setShowModal(true)}>
            Change Password
          </button>
        </div>
      </div>

      {showModal && (
        <div className="modal">
          <div className="modal-content">
            <h3>Change Password</h3>

            {/* Current Password */}
            <div className="input-wrapper">
              <FaLock className="left-icon" />
              <input
                className="input-wrapper-input"
                type={showCurrent ? 'text' : 'password'}
                placeholder="Current Password"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
              />
              <span className="toggle-icon" onClick={() => setShowCurrent(!showCurrent)}>
                {showCurrent ? <FaEye /> : <FaEyeSlash />}
              </span>
            </div>

            {/* New Password */}
            <div className="input-wrapper">
              <FaLock className="left-icon" />
              <input
                className="input-wrapper-input"
                type={showNew ? 'text' : 'password'}
                placeholder="New Password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
              />
              <span className="toggle-icon" onClick={() => setShowNew(!showNew)}>
                {showNew ? <FaEye /> : <FaEyeSlash />}
              </span>
            </div>

            {/* Confirm Password */}
            <div className="input-wrapper">
              <FaLock className="left-icon" />
              <input
                className="input-wrapper-input"
                type={showConfirm ? 'text' : 'password'}
                placeholder="Confirm New Password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
              />
              <span className="toggle-icon" onClick={() => setShowConfirm(!showConfirm)}>
                {showConfirm ? <FaEye /> : <FaEyeSlash />}
              </span>
            </div>

            <div className="button-group">
              <button className="save-btn" onClick={handlePasswordChange}>Update</button>
              <button
                className="cancel-btn"
                onClick={() => {
                  setShowModal(false);
                  resetPasswordFields();
                }}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      <ToastContainer position="top-center" autoClose={2000} />
    </div>
  );
};

export default UserProfile;