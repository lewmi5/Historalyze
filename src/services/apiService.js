// Real API implementation connecting to Spring Boot backend
const API_BASE_URL = 'http://localhost:8080/api';

export const fetchStockData = async (stockName) => {
  try {
    // First check if we can connect to the backend
    const healthCheck = await fetch(`${API_BASE_URL}/data`);
    if (!healthCheck.ok) {
      throw new Error('Backend server is not available');
    }
    
    // Make the request for stock prices
    const response = await fetch(`${API_BASE_URL}/prices`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ name: stockName }),
    });

    if (!response.ok) {
      throw new Error('Failed to fetch stock data');
    }

    const data = await response.json();
    
    // Check if we received CSV content from the backend
    const csvContent = data.content;
    if (!csvContent || csvContent.startsWith('Error')) {
      throw new Error(csvContent || 'No data returned from server');
    }
    
    // Parse CSV to get historical prices using the custom format parser
    const historicalPrices = parseCustomCSVFormat(csvContent);
    
    return {
      stockName: stockName.toUpperCase(),
      historicalPrices
    };
  } catch (error) {
    console.error('Error fetching stock data:', error);
    throw error;
  }
};

export const fetchStrategyDescription = async (strategyName) => {
  try {
    // First check if we can connect to the backend
    const healthCheck = await fetch(`${API_BASE_URL}/data`);
    if (!healthCheck.ok) {
      throw new Error('Backend server is not available');
    }
    
    // Make the request for stock prices
    const response = await fetch(`${API_BASE_URL}/prices`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ name: strategyName }),
    });

    if (!response.ok) {
      throw new Error('Failed to fetch stock data');
    }

    const data = await response.json();
    
    // Check if we received CSV content from the backend
    const csvContent = data.content;
    if (!csvContent || csvContent.startsWith('Error')) {
      throw new Error(csvContent || 'No data returned from server');
    }
        
    return {
      strategyName: strategyName,
    };
  } catch (error) {
    console.error('Error fetching stock data:', error);
    throw error;
  }
};

// Helper function to fetch available stock names
export const fetchStockNames = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/names`);
    if (!response.ok) {
      throw new Error('Failed to fetch stock names');
    }
    
    const data = await response.json();
    return data.names;
  } catch (error) {
    console.error('Error fetching stock names:', error);
    throw error;
  }
};

export const fetchStrategyNames = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/strategy_names`);
    if (!response.ok) {
      throw new Error('Failed to fetch stock names');
    }

    const data = await response.json();
    return data.names;
  } catch (error) {
    console.error('Error fetching stock names:', error);
    throw error;
  }
};

// Custom parser for the specific CSV format provided
const parseCustomCSVFormat = (csvContent) => {
  // Split the CSV content into lines
  const lines = csvContent.trim().split('\n');
  
  if (lines.length <= 3) {
    console.warn("CSV file appears to be missing data rows");
    return [];
  }
  
  // Parse the headers from the first row
  const headers = lines[0].split(',').map(header => header.trim().toLowerCase());
  
  // Identify the columns we need
  const dateColumnIndex = 0; // Assuming date is always the first column in each data row
  
  // Look for a price column - prioritize 'close' or 'price'
  let priceColumnIndex = headers.findIndex(header => header === 'close');
  if (priceColumnIndex === -1) {
    priceColumnIndex = headers.findIndex(header => header === 'price');
  }
  
  // If still not found, use the first numeric column
  if (priceColumnIndex === -1) {
    priceColumnIndex = 1; // Default to second column if no price column is found
  }
  
  // Skip the headers and ticker rows and start processing from the data rows
  // In your example, data would start from the 4th row (index 3)
  const dataStartIndex = 3;
  
  // Parse the data rows
  const historicalPrices = [];
  
  for (let i = dataStartIndex; i < lines.length; i++) {
    const row = lines[i].trim();
    
    // Skip empty rows
    if (!row) continue;
    
    const values = row.split(',').map(value => value.trim());
    
    // Extract date and price
    // For the specific format, we assume the date is in the first column
    // and we use the price column identified earlier
    const date = values[dateColumnIndex];
    const priceStr = values[priceColumnIndex];
    
    // Skip rows with missing data
    if (!date || !priceStr) continue;
    
    // Try to parse the price
    const price = parseFloat(priceStr);
    
    // Only add valid data points
    if (!isNaN(price) && date) {
      historicalPrices.push({
        date,
        price
      });
    }
  }
  
  // If we couldn't parse any data using the standard approach,
  // try an alternative approach for transposed data
  if (historicalPrices.length === 0 && lines.length > 3) {
    console.warn("Trying alternative parsing for transposed data format");
    return parseTransposedCSVFormat(csvContent);
  }
  
  // Sort by date
  historicalPrices.sort((a, b) => {
    return new Date(a.date) - new Date(b.date);
  });
  
  return historicalPrices;
};

// Alternative parser for transposed data (where dates might be in columns, not rows)
const parseTransposedCSVFormat = (csvContent) => {
  const lines = csvContent.trim().split('\n');
  
  // Find the date row - usually has "Date" in the first column
  const dateRowIndex = lines.findIndex(line => 
    line.toLowerCase().split(',')[0].trim() === 'date');
  
  if (dateRowIndex === -1) {
    console.error("Could not find date row");
    return [];
  }
  
  // Find price data rows - look for rows with "Price" or "Close" in first column
  const priceRowIndex = lines.findIndex(line => {
    const firstCol = line.toLowerCase().split(',')[0].trim();
    return firstCol === 'price' || firstCol === 'close';
  });
  
  if (priceRowIndex === -1) {
    console.error("Could not find price row");
    return [];
  }
  
  // Extract dates from the date row
  const dateEntries = lines[dateRowIndex].split(',');
  
  // Extract prices from the price row
  const priceEntries = lines[priceRowIndex].split(',');
  
  // Create data points starting from index 1 (skipping the header column)
  const historicalPrices = [];
  
  for (let i = 1; i < dateEntries.length; i++) {
    const date = dateEntries[i].trim();
    const priceStr = priceEntries[i].trim();
    
    if (!date || !priceStr) continue;
    
    const price = parseFloat(priceStr);
    
    if (!isNaN(price) && date) {
      historicalPrices.push({
        date,
        price
      });
    }
  }
  
  // Sort by date
  historicalPrices.sort((a, b) => {
    return new Date(a.date) - new Date(b.date);
  });
  
  return historicalPrices;
};