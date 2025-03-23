import React, { useState } from 'react';
import StockForm from './components/StockForm';
import StockPlot from './components/StockPlot';
import Analytics from './components/Analytics';
import Parameters from './components/Parameters';
import { fetchStockData } from './services/apiService';
import { fetchStrategyDescription } from './services/apiService';
import { fetchAnalysisResult } from './services/apiService';

const App = () => {
  const [stockData, setStockData] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [currentStock, setCurrentStock] = useState('');
  const [currentStrategy, setCurrentStrategy] = useState('');
  const [currentStrategyDescription, setCurrentStrategyDescription] = useState('');
  const [analysisResult, setAnalysisResult] = useState(null);
  
  const handleStockSubmit = async (stockName) => {
    try {
      setError(null);
      setLoading(true);
      setCurrentStock(stockName);
      
      const data = await fetchStockData(stockName);
      
      if (data.historicalPrices.length === 0) {
        setError(`No valid price data found for ${stockName}. The CSV file may be empty or in an unsupported format.`);
        setStockData(null);
      } else {
        setStockData(data.historicalPrices);
      }
    } catch (error) {
      setError(`Failed to fetch stock data: ${error.message}`);
      setStockData(null);
    } finally {
      setLoading(false);
    }
  };

  const handleStrategySubmit = async (strategyName) => {
    try {
      setError(null);
      setLoading(true);
      setCurrentStrategy(strategyName);

      // const data = await fetchStockData(strategyName);
      const description = await fetchStrategyDescription(strategyName);

      if (description.length === 0) {
        setError(`${strategyName} is not valid stategy name.`);
        setCurrentStrategyDescription(null);
      } else {
        setCurrentStrategyDescription(description);
      }
    } catch (error) {
      setError(`Failed to fetch stock data: ${error.message}`);
      setCurrentStrategyDescription(null);
    } finally {
      setLoading(false);
    }
  };

  const handleParametersSubmit = async (parameters) => {
    try {
      setError(null);
      setLoading(true);

      // const data = await fetchStockData(strategyName);
      const response = await fetchAnalysisResult(parameters);

      if (response.length === 0) {
        setError(`${strategyName} is not valid stategy name.`);
        setAnalysisResult(null);
        // setCurrentStrategyDescription(null);
      } else {
        // setCurrentStrategyDescription(description);
        setAnalysisResult(response);
      }
    } catch (error) {
      setError(`Failed to fetch stock data: ${error.message}`);
      setAnalysisResult(null);
    } finally {
      setLoading(false);
    }
  };

  
  return (
    <div className="App">
      <h1>Stock Data Analyzer</h1>
      <p className="subtitle">Fetch historical stock data from the Spring backend</p>

      <StockForm onSubmit={handleStockSubmit} />
      
      {loading && <div className="loading">Loading stock data...</div>}
      
      {error && (
        <div className="error-container">
          <p className="error-message">{error}</p>
          <p className="error-help">
            Check that the CSV file for "{currentStock}" exists on the server and has the correct format.
          </p>
        </div>
      )}

{stockData && stockData.length > 0 ? (
  <StockPlot data={stockData}/>
) : stockData && stockData.length === 0 ? (
  <p className="no-data-message">No data available for this stock</p>
) : null}

{stockData && stockData.length > 0 ? (
  <Analytics onSubmit={handleStrategySubmit}/>
) : stockData && stockData.length === 0 ? (
  <p className="no-data-message">No data available for this strategy</p>
) : null}

{currentStrategyDescription && currentStrategyDescription.length > 0 ? (
  <div>
    <div className="strategy-description-wrapper">
    {currentStrategyDescription}
    </div>
<Parameters onSubmit={handleParametersSubmit}/>
  </div>
) : currentStrategyDescription && currentStrategyDescription.length === 0 ? (
  <p className="no-data-message">No data available for this strategy</p>
) : null}



{analysisResult ? (
        <div className="analysis-result">
          <h3>Analysis Result</h3>
          <p className="result-value">Result: {analysisResult}</p>
        </div>
      ) : analysisResult ? (
        <p className="no-data-message">No result value available for this analysis</p>
      ) : null}
    </div>
  );
};

export default App;