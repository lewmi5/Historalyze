// import React, { useState, useEffect } from 'react';
// import axios from 'axios';

// const API_BASE_URL = 'http://localhost:8080/api';

// function ApiCommunication() {
//   const [backendData, setBackendData] = useState(null);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const [formData, setFormData] = useState({ name: '', message: '' });
//   const [submitResponse, setSubmitResponse] = useState(null);

//   // Fetch data from Spring backend on component mount
//   useEffect(() => {
//     const fetchData = async () => {
//       try {
//         const response = await axios.get(`${API_BASE_URL}/data`);
//         setLoading(false);
//       } catch (err) {
//         setError('Error fetching data from backend: ' + err.message);
//         setLoading(false);
//       }
//     };

//     fetchData();
//   }, []);

//   // Handle form input changes
//   const handleInputChange = (e) => {
//     const { name, value } = e.target;
//     setFormData({
//       ...formData,
//       [name]: value
//     });
//   };

//   // Submit data to Spring backend
//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     setSubmitResponse(null);
    
//     try {
//       const response = await axios.post(`${API_BASE_URL}/submit`, formData);
//       setSubmitResponse(response.data);
//     } catch (err) {
//       setError('Error submitting data: ' + err.message);
//     }
//   };

//   if (loading) return <div>Loading data from backend...</div>;
//   if (error) return <div className="error">{error}</div>;

//   return (
//     <div className="api-communication">
//       <h2>Spring Backend Communication</h2>
      
//       <div className="data-section">
//         <h3>Data from Backend:</h3>
//         {backendData && (
//           <pre>{JSON.stringify(backendData, null, 2)}</pre>
//         )}
//       </div>
      
//       <div className="form-section">
//         <h3>Send Data to Backend:</h3>
//         <form onSubmit={handleSubmit}>
//           <div className="form-group">
//             <label htmlFor="name">Name:</label>
//             <input
//               type="text"
//               id="name"
//               name="name"
//               value={formData.name}
//               onChange={handleInputChange}
//               required
//             />
//           </div>
          
//           <div className="form-group">
//             <label htmlFor="message">Message:</label>
//             <textarea
//               id="message"
//               name="message"
//               value={formData.message}
//               onChange={handleInputChange}
//               required
//             />
//           </div>
          
//           <button type="submit">Submit to Backend</button>
//         </form>
        
//         {submitResponse && (
//           <div className="response">
//             <h4>Response:</h4>
//             <pre>{JSON.stringify(submitResponse, null, 2)}</pre>
//           </div>
//         )}
//       </div>
//     </div>
//   );
// }

// export default ApiCommunication;