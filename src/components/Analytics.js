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
            list="stockOptions"
            required
          />
          <datalist id="stockOptions">
            {availableStrategies.map((strategy, index) => (
              <option key={index} value={strategy} />
            ))}
          </datalist>
        </div>
        <button type="submit" disabled={loading}>Get Strategy Data</button>
      </form>
      
      {loading && <p>Loading available stocks...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      
      {availableStrategies.length > 0 && (
        <div className="available-stocks">
          <p>Available strategies: </p>
          <div className="strategy-tag-container">
            {availableStrategies.slice(0, 10).map((strategy, index) => (
              <span 
                key={index} 
                className="strategy-tag"
                onClick={() => setStrategyName(strategy)}
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