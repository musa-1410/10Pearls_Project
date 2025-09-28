import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import './ContactList.css';

import { FaEdit, FaTrash } from 'react-icons/fa';
import { IoPersonAddSharp } from 'react-icons/io5';
import { toast, ToastContainer } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

const Contacts = () => {
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  const [contacts, setContacts] = useState([]);
  const [selectedContact, setSelectedContact] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [role, setRole] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    title: '',
    email: '',
    emailLabel: 'Work',
    phone: '',
    phoneLabel: 'Work',
    address: '',
    isAdmin: false,
  });
  const [currentPage, setCurrentPage] = useState(1);
  const contactsPerPage = 10;

  const fetchContacts = useCallback(async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/contacts', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setContacts(res.data);
    } catch (error) {
      toast.error('Failed to fetch contacts');
    }
  }, [token]);

  useEffect(() => {
    if (!token) {
      navigate('/login');
    } else {
      try {
        const decoded = jwtDecode(token);
        setRole(decoded.role || '');
        fetchContacts();
      } catch (err) {
        toast.error('Invalid token');
        navigate('/login');
      }
    }
  }, [token, navigate, fetchContacts]);

  const handleDelete = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/contacts/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success('Contact deleted');
      fetchContacts();
    } catch (error) {
      toast.error('Delete failed');
    }
  };

  const handleAddContact = async () => {
    try {
      await axios.post('http://localhost:8080/api/contacts', formData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success('Contact added');
      setShowAddModal(false);
      resetForm();
      fetchContacts();
    } catch (err) {
      toast.error('Add failed');
    }
  };

  const handleEditContact = async () => {
    try {
      await axios.put(`http://localhost:8080/api/contacts/${selectedContact.id}`, formData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success('Contact updated');
      setShowEditModal(false);
      setSelectedContact(null);
      resetForm();
      fetchContacts();
    } catch (err) {
      toast.error('Update failed');
    }
  };

  const openEditModal = (contact) => {
    setSelectedContact(contact);
    setFormData({
      firstName: contact.firstName || '',
      lastName: contact.lastName || '',
      title: contact.title || '',
      email: contact.email || '',
      emailLabel: contact.emailLabel || 'Work',
      phone: contact.phone || '',
      phoneLabel: contact.phoneLabel || 'Work',
      address: contact.address || '',
      // isAdmin: contact.isAdmin || false,
    });
    setShowEditModal(true);
  };

  const resetForm = () => {
    setFormData({
      firstName: '',
      lastName: '',
      title: '',
      email: '',
      emailLabel: 'Work',
      phone: '',
      phoneLabel: 'Work',
      address: '',
      isAdmin: false,
    });
  };

  const handleCloseAddModal = () => {
    setShowAddModal(false);
    resetForm();
  };

  const handleCloseEditModal = () => {
    setShowEditModal(false);
    resetForm();
    setSelectedContact(null);
  };

  const filteredContacts = contacts.filter((c) =>
    (c.firstName?.toLowerCase() || '').includes(searchTerm) ||
    (c.lastName?.toLowerCase() || '').includes(searchTerm) ||
    (c.email?.toLowerCase() || '').includes(searchTerm) ||
    (c.phone?.toLowerCase() || '').includes(searchTerm)
  );

  const indexOfLastContact = currentPage * contactsPerPage;
  const indexOfFirstContact = indexOfLastContact - contactsPerPage;
  const currentContacts = filteredContacts.slice(indexOfFirstContact, indexOfLastContact);
  const totalPages = Math.ceil(filteredContacts.length / contactsPerPage);

  const goToNextPage = () => {
    if (currentPage < totalPages) setCurrentPage(currentPage + 1);
  };

  const goToPrevPage = () => {
    if (currentPage > 1) setCurrentPage(currentPage - 1);
  };

  const handlePageClick = (number) => {
    setCurrentPage(number);
  };

  return (
    
    <div className="contacts-container">
      <div className="top-bar">
        {role === 'ROLE_ADMIN' && (
          <button className="add-btn" onClick={() => setShowAddModal(true)} title="Add Contact">
            <IoPersonAddSharp />
          </button>
        )}
        <input
          type="text"
          className="search-input"
          placeholder="Search contacts..."
          onChange={(e) => setSearchTerm(e.target.value.toLowerCase())}
        />
      </div>

      <table className="contacts-table">
  <thead>
    <tr>
      <th>Name</th>
      {/* <th>Last</th> */}
      {/* <th>Email Label</th> */}
      {/* <th>Email</th> */}
      {/* <th>Phone Label</th> */}
      <th>Phone</th>
      {role === 'ROLE_ADMIN' && <th>Actions</th>}
    </tr>
  </thead>
  <tbody>
    {currentContacts.map((c) => (
      <tr
        key={c.id}
        onClick={() => navigate(`/contacts/${c.id}`)}
        style={{ cursor: 'pointer' }}
      >
        <td>{c.firstName}</td>
        {/* <td>{c.lastName}</td> */}
        {/* <td>{c.email}</td> */}
        <td>{c.phone}</td>
        {role === 'ROLE_ADMIN' && (
          <td>
            <button
              className="icon-btn edit-btn"
              onClick={(e) => {
                e.stopPropagation();
                openEditModal(c);
              }}
            >
              <FaEdit />
            </button>
            <button
              className="icon-btn delete-btn"
              onClick={(e) => {
                e.stopPropagation();
                handleDelete(c.id);
              }}
            >
              <FaTrash />
            </button>
          </td>
        )}
      </tr>
    ))}
  </tbody>
</table>


      {/* Add Modal */}
      {showAddModal && (
        <div className="modal">
          <div className="modal-content">
            <h3>Add Contact</h3>
            <form onSubmit={(e) => { e.preventDefault(); handleAddContact(); }}>
              <input type="text" value={formData.firstName} onChange={(e) => setFormData({ ...formData, firstName: e.target.value })} placeholder="First Name" required />
              <input type="text" value={formData.lastName} onChange={(e) => setFormData({ ...formData, lastName: e.target.value })} placeholder="Last Name" required />
              {/* <input type="text" value={formData.title} onChange={(e) => setFormData({ ...formData, title: e.target.value })} placeholder="Title" /> */}
              <input type="email" value={formData.email} onChange={(e) => setFormData({ ...formData, email: e.target.value })} placeholder="Email" required />
              <select value={formData.emailLabel} onChange={(e) => setFormData({ ...formData, emailLabel: e.target.value })}>
                <option value="Work">Work</option>
                <option value="Personal">Personal</option>
              </select>
              <input type="text" value={formData.phone} onChange={(e) => setFormData({ ...formData, phone: e.target.value })} placeholder="Phone" required />
              <select value={formData.phoneLabel} onChange={(e) => setFormData({ ...formData, phoneLabel: e.target.value })}>
                <option value="Work">Work</option>
                <option value="Personal">Personal</option>
              </select>
              <input type="text" value={formData.address} onChange={(e) => setFormData({ ...formData, address: e.target.value })} placeholder="Address" />
              {/* <label>
                <input type="checkbox" checked={formData.isAdmin} onChange={(e) => setFormData({ ...formData, isAdmin: e.target.checked })} />
                Is Admin
              </label> */}
              <div className="button-group">
                <button type="submit" className="save-btn">Save</button>
                <button type="button" onClick={handleCloseAddModal} className="cancel-btn">Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      {showEditModal && selectedContact && (
        <div className="modal">
          <div className="modal-content">
            <h3>Edit Contact</h3>
            <form onSubmit={(e) => { e.preventDefault(); handleEditContact(); }}>
              <input type="text" value={formData.firstName} onChange={(e) => setFormData({ ...formData, firstName: e.target.value })} placeholder="First Name" required />
              <input type="text" value={formData.lastName} onChange={(e) => setFormData({ ...formData, lastName: e.target.value })} placeholder="Last Name" required />
              {/* <input type="text" value={formData.title} onChange={(e) => setFormData({ ...formData, title: e.target.value })} placeholder="Title" /> */}
              <input type="email" value={formData.email} onChange={(e) => setFormData({ ...formData, email: e.target.value })} placeholder="Email" required />
              <select value={formData.emailLabel} onChange={(e) => setFormData({ ...formData, emailLabel: e.target.value })}>
                <option value="Work">Work</option>
                <option value="Personal">Personal</option>
              </select>
              <input type="text" value={formData.phone} onChange={(e) => setFormData({ ...formData, phone: e.target.value })} placeholder="Phone" required />
              <select value={formData.phoneLabel} onChange={(e) => setFormData({ ...formData, phoneLabel: e.target.value })}>
                <option value="Work">Work</option>
                <option value="Personal">Personal</option>
              </select>
              <input type="text" value={formData.address} onChange={(e) => setFormData({ ...formData, address: e.target.value })} placeholder="Address" />
              {/* <label>
                <input type="checkbox" checked={formData.isAdmin} onChange={(e) => setFormData({ ...formData, isAdmin: e.target.checked })} />
                Is Admin
              </label> */}
              <div className="button-group">
                <button type="submit" className="save-btn">Save</button>
                <button type="button" onClick={handleCloseEditModal} className="cancel-btn">Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Pagination */}
      <div className="pagination">
        <button onClick={goToPrevPage} disabled={currentPage === 1} className="page-btn">Previous</button>
        {[...Array(totalPages)].map((_, index) => (
          <button key={index} onClick={() => handlePageClick(index + 1)} className={`page-btn ${currentPage === index + 1 ? 'active' : ''}`}>
            {index + 1}
          </button>
        ))}
        <button onClick={goToNextPage} disabled={currentPage === totalPages} className="page-btn">Next</button>
      </div>

      <ToastContainer position="top-center" autoClose={3000} />
    </div>
  );
};

export default Contacts;