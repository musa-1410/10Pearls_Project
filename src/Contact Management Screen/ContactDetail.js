import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './ContactDetail.css';
import { Card } from 'react-bootstrap';

const ContactDetail = () => {
  const { id } = useParams();
  const [contact, setContact] = useState(null);
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  useEffect(() => {
    const fetchContact = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(
          `http://localhost:8080/api/contacts/${id}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setContact(response.data);
      } catch (error) {
        console.error('Error fetching contact details:', error);
      }
    };

    fetchContact();
  }, [id]);

  if (!contact) {
    return <div>Loading...</div>;
  }

  return (
    <>
      {/* Pass variant so that header shows "Contact Details" */}
    

      <div className="contact-detail-container">
        <Card className="contact-card p-4 shadow-sm">
          <Card.Body>
            <p><strong>First Name:</strong> {contact.firstName}</p>
            <p><strong>Last Name:</strong> {contact.lastName}</p>
            <p><strong>Title:</strong> {contact.title}</p>
            <p>
              <strong>Email:</strong> {contact.email} 
              {contact.emailLabel && (
                <span style={{ marginLeft: '8px', color: '#555' }}>
                  ({contact.emailLabel})
                </span>
              )}
            </p>
            <p>
              <strong>Phone:</strong> {contact.phone} 
              {contact.phoneLabel && (
                <span style={{ marginLeft: '8px', color: '#555' }}>
                  ({contact.phoneLabel})
                </span>
              )}
            </p>
          </Card.Body>
        </Card>
      </div>
    </>
  );
};

export default ContactDetail;