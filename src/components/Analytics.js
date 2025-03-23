import React, { useState, useEffect } from 'react';
import { fetchStrategyNames } from '../services/apiService';

const Analytics = ({ onSubmit }) => {
  const [strategyName, setStrategyName] = useState('');
  const [availableStrategies, setAvailableStrategies] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Fetch available strategies names when component mounts
    const getStrategyNames = async () => {
      setLoading(true);
      try {
        const names = await fetchStrategyNames();
        setAvailableStrategies(names || []);
        setError(null);
      } catch (err) {
        console.error('Error fetching strategy names:', err);
        setError('Failed to load available stocks');
      } finally {
        setLoading(false);
      }
    };

    getStrategyNames();
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (strategyName.trim()) {
      onSubmit(strategyName);
    }
  };


  const handleStrategyClick = (strategy) => {
    setStrategyName(strategy);
    setTimeout(() => onSubmit(strategy), 0); // Automatically submit after selecting a strategy
  };


  return (
    <div>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="strategyName">Strategy Symbol:</label>
          <input
            type="text"
            id="strategyName"
            value={strategyName}
            onChange={(e) => setStrategyName(e.target.value)}
            placeholder="Enter strategy symbol"
            list="strategyOptions"
            required
          />
          <datalist id="strategyOptions">
            {availableStrategies.map((strategy, index) => (
              <option key={index} value={strategy} />
            ))}
          </datalist>
        </div>
      </form>
      
      {loading && <p>Loading available stocks...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      
      {availableStrategies.length > 0 && (
        <div className="available-strategies">
          <p>Available strategies: </p>
        
        <div className="strategy-tag-container">
  
            {availableStrategies.slice(0, 10).map((strategy, index) => (
              <span 
                key={index} 
                className="strategy-tag"
                onClick={() => handleStrategyClick(strategy)}
                style={{ cursor: 'pointer', textDecoration: 'underline', marginRight: '10px' }}
              >
                {strategy}
              </span>
            ))}
            {availableStrategies.length > 10 && <span>and {availableStrategies.length - 10} more...</span>}
        </div>
        </div>

      )}
    </div>
  );
};

export default Analytics;