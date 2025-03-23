import React, { useState } from 'react';

const Parameters = ({ onSubmit, isDisabled }) => {
  const [parameters, setInputValue] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (parameters.trim()) {
      onSubmit(parameters);
      setInputValue('');
    }
  };

  return (
    <div className="analyze-form-container">
      <h3>Analysis Parameters</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="analyzeInput">Enter parameters for analysis:</label>
          <input
            type="text"
            id="analyzeInput"
            value={parameters}
            onChange={(e) => setInputValue(e.target.value)}
            disabled={isDisabled}
            placeholder="Enter analysis parameters..."
            className="form-control"
          />
        </div>
        <button 
          type="submit" 
          disabled={isDisabled || !parameters.trim()} 
          className="btn btn-primary"
        >
          Analyze
        </button>
      </form>
    </div>
  );
};

export default Parameters;
