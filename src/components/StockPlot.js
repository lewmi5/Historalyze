import React, { useState } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const StockPlot = ({ data }) => {
  const [timeRange, setTimeRange] = useState('all'); // 'all', 'year', 'month', 'week'
  
  // Format the data for the chart and filter by time range
  const getFilteredData = () => {
    if (!data || data.length === 0) return [];
    
    let filteredData = [...data];
    const now = new Date();
    
    if (timeRange === 'year') {
      const oneYearAgo = new Date();
      oneYearAgo.setFullYear(now.getFullYear() - 1);
      filteredData = data.filter(item => new Date(item.date) >= oneYearAgo);
    } else if (timeRange === 'month') {
      const oneMonthAgo = new Date();
      oneMonthAgo.setMonth(now.getMonth() - 1);
      filteredData = data.filter(item => new Date(item.date) >= oneMonthAgo);
    } else if (timeRange === 'week') {
      const oneWeekAgo = new Date();
      oneWeekAgo.setDate(now.getDate() - 7);
      filteredData = data.filter(item => new Date(item.date) >= oneWeekAgo);
    }
    
    return filteredData.map(item => ({
      date: new Date(item.date).toLocaleDateString(),
      price: item.price
    }));
  };
  
  const formattedData = getFilteredData();
  
  // Calculate some statistics
  const calculateStats = () => {
    if (formattedData.length === 0) return { min: 0, max: 0, avg: 0, change: 0 };
    
    const prices = formattedData.map(item => item.price);
    const min = Math.min(...prices);
    const max = Math.max(...prices);
    const avg = prices.reduce((sum, price) => sum + price, 0) / prices.length;
    
    // Calculate percentage change from first to last
    const first = formattedData[0].price;
    const last = formattedData[formattedData.length - 1].price;
    const change = ((last - first) / first) * 100;
    
    return { min, max, avg, change };
  };
  
  const stats = calculateStats();

  return (
    <div style={{ marginTop: '20px' }}>
      <div className="chart-header">
        <h2>Stock Price History</h2>
        <div className="time-range-buttons">
          <button 
            className={timeRange === 'all' ? 'active' : ''} 
            onClick={() => setTimeRange('all')}
          >
            All Time
          </button>
          <button 
            className={timeRange === 'year' ? 'active' : ''} 
            onClick={() => setTimeRange('year')}
          >
            1 Year
          </button>
          <button 
            className={timeRange === 'month' ? 'active' : ''} 
            onClick={() => setTimeRange('month')}
          >
            1 Month
          </button>
          <button 
            className={timeRange === 'week' ? 'active' : ''} 
            onClick={() => setTimeRange('week')}
          >
            1 Week
          </button>
        </div>
      </div>
      
      <div className="stats-container">
        <div className="stat-box">
          <span className="stat-label">Min</span>
          <span className="stat-value">${stats.min.toFixed(2)}</span>
        </div>
        <div className="stat-box">
          <span className="stat-label">Max</span>
          <span className="stat-value">${stats.max.toFixed(2)}</span>
        </div>
        <div className="stat-box">
          <span className="stat-label">Avg</span>
          <span className="stat-value">${stats.avg.toFixed(2)}</span>
        </div>
        <div className="stat-box">
          <span className="stat-label">Change</span>
          <span className={`stat-value ${stats.change >= 0 ? 'positive' : 'negative'}`}>
            {stats.change >= 0 ? '+' : ''}{stats.change.toFixed(2)}%
          </span>
        </div>
      </div>
      
      <div style={{ height: '400px', width: '100%' }}>
        {formattedData.length > 0 ? (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart
              data={formattedData}
              margin={{
                top: 5,
                right: 30,
                left: 20,
                bottom: 5,
              }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis 
                dataKey="date" 
                tickFormatter={(tick) => tick}
                tick={{ fontSize: 12 }}
                interval="preserveStartEnd"
              />
              <YAxis 
                domain={['auto', 'auto']} 
                tick={{ fontSize: 12 }}
                tickFormatter={(tick) => `$${tick}`}
              />
              <Tooltip formatter={(value) => [`$${value.toFixed(2)}`, 'Price']} />
              <Legend />
              <Line 
                type="monotone" 
                dataKey="price" 
                stroke="#8884d8" 
                strokeWidth={2}
                dot={{ r: 1 }}
                activeDot={{ r: 5 }}
              />
            </LineChart>
          </ResponsiveContainer>
        ) : (
          <div className="no-data-message">
            No data available for the selected time range
          </div>
        )}
      </div>
      
      {formattedData.length > 0 && (
        <div className="data-points-info">
          Showing {formattedData.length} data points
        </div>
      )}
    </div>
  );
};

export default StockPlot;