import React, { useState, useEffect } from 'react';
import { fetchStockNames } from '../services/apiService';

const StockForm = ({ onSubmit }) => {
  const [stockName, setStockName] = useState('');
  const [availableStocks, setAvailableStocks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Fetch available stock names when component mounts
    const getStockNames = async () => {
      setLoading(true);
      try {
        const names = await fetchStockNames();
        setAvailableStocks(names || []);
        setError(null);
      } catch (err) {
        console.error('Error fetching stock names:', err);
        setError('Failed to load available stocks');
      } finally {
        setLoading(false);
      }
    };

    getStockNames();
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (stockName.trim()) {
      onSubmit(stockName);
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="stockName">Stock Symbol:</label>
          <input
            type="text"
            id="stockName"
            value={stockName}
            onChange={(e) => setStockName(e.target.value)}
            placeholder="Enter stock symbol"
            list="stockOptions"
            required
          />
          <datalist id="stockOptions">
            {availableStocks.map((stock, index) => (
              <option key={index} value={stock} />
            ))}
          </datalist>
        </div>
        <button type="submit" disabled={loading}>Get Stock Data</button>
      </form>
      
      {loading && <p>Loading available stocks...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      
      {availableStocks.length > 0 && (
        <div className="available-stocks">
          <p>Available stocks: </p>
          <div className="stock-tag-container">
            {availableStocks.slice(0, 10).map((stock, index) => (
              <span 
                key={index} 
                className="stock-tag"
                onClick={() => setStockName(stock)}
              >
                {stock}
              </span>
            ))}
            {availableStocks.length > 10 && <span>and {availableStocks.length - 10} more...</span>}
          </div>
        </div>
      )}
    </div>
  );
};

export default StockForm;