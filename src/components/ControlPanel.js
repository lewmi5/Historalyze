import React from "react";

const ControlPanel = () => {
  return (
<div className="api-communication">      
      <div className="data-section">
        <h3>Data from Backend:</h3>
        {backendData && (
          <pre>{JSON.stringify(backendData, null, 2)}</pre>
        )}
      </div>
      
      <div className="form-section">
        <h3>Send Data to Backend:</h3>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="name">Name:</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleInputChange}
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="message">Message:</label>
            <textarea
              id="message"
              name="message"
              value={formData.message}
              onChange={handleInputChange}
              required
            />
          </div>
          
          <button type="submit">Submit to Backend</button>
        </form>
        
        {submitResponse && (
          <div className="response">
            <h4>Response:</h4>
            <pre>{JSON.stringify(submitResponse, null, 2)}</pre>
          </div>
        )}
      </div>
    </div>
  );
};

export default App;
