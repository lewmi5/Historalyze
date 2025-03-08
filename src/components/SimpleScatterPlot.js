import React, { useState, useEffect } from 'react';
import { ScatterChart, Scatter, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import Papa from 'papaparse';


const SimpleScatterPlot = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Path to your CSV file in the public folder
    const csvFilePath = process.env.PUBLIC_URL + '/google_stock_history.csv';

    // Load and process the CSV file
    fetch(csvFilePath)
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to fetch CSV file');
        }
        return response.text();
      })
      .then(csvText => {
        Papa.parse(csvText, {
          header: true,
          skipEmptyLines: true,
          complete: (results) => {
            // Skip first two rows
            let processedData = results.data.slice(2);
            
            // Process the data for chart
            processedData = processedData.map((row, index) => {
              const dataPoint = { index };
              
              // Convert all numeric values
              Object.keys(row).forEach(key => {
                if (key !== 'Date' && key !== 'Price') {
                  const value = parseFloat(row[key]);
                  if (!isNaN(value)) {
                    dataPoint[key] = value;
                  }
                }
              });
              
              // Store date for reference
              dataPoint.displayDate = row.Date || row.Price;
              
              return dataPoint;
            });
            
            setData(processedData);
            setLoading(false);
          },
          error: (error) => {
            setError('Error parsing CSV: ' + error.message);
            setLoading(false);
          }
        });
      })
      .catch(err => {
        setError('Error: ' + err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Loading data...</div>;
  if (error) return <div>{error}</div>;
  if (data.length === 0) return <div>No data available</div>;

  // Find the first numeric column to use for Y-axis
  const yKey = Object.keys(data[0]).find(key => 
    typeof data[0][key] === 'number' && key !== 'index'
  );

  return (
   
    <div>
      <h2>Google Stock History</h2>
      <div style={{ width: '100%', height: 400 }}>
        <ResponsiveContainer>
          <ScatterChart margin={{ top: 20, right: 30, bottom: 20, left: 30 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis 
              type="number" 
              dataKey="index" 
              name="Index" 
              label={{ value: "Time", position: "bottom" }} 
            />
            <YAxis 
              type="number" 
              dataKey={yKey} 
              name={yKey} 
              label={{ value: yKey, angle: -90, position: "left" }} 
            />
            <Tooltip 
              cursor={{ strokeDasharray: '3 3' }}
              formatter={(value, name) => [value, name]}
              labelFormatter={(value) => `Date: ${data[value]?.displayDate || value}`}
            />
            <Scatter name={yKey} data={data} fill="#8884d8" />
          </ScatterChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default SimpleScatterPlot;
// import React, { useState, useEffect } from 'react';
// import { ScatterChart, Scatter, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
// import Papa from 'papaparse';

// // Assuming you have a CSV file in your public directory
// const csvFilePath = '/google_stock_history.csv';

// const StockScatterPlot = () => {
//   const [data, setData] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);

//   useEffect(() => {
//     // Function to load and process the CSV file
//     const loadCSV = async () => {
//       try {
//         // Fetch the CSV file
//         const response = await fetch(csvFilePath);
//         const csvText = await response.text();
        
//         // Parse CSV data using PapaParse
//         Papa.parse(csvText, {
//           header: true,
//           skipEmptyLines: true,
//           complete: (results) => {
//             // Skip the first two rows (similar to drop([0, 1]) in pandas)
//             const skippedRows = results.data.slice(2);
            
//             // Process data - convert to proper format for the chart
//             const processedData = skippedRows.map((row, index) => {
//               // Handle the date column - assuming "Price" column should be renamed to "Date"
//               const dateValue = row['Price'] || row['Date'];
              
//               // Convert other numerical columns
//               const dataPoint = {
//                 index: index, // Use as x-axis if you don't have a specific x value
//               };
              
//               // Add all numerical columns to the data point
//               Object.keys(row).forEach(key => {
//                 if (key !== 'Price' && key !== 'Date') {
//                   // Convert string values to numbers
//                   const numValue = parseFloat(row[key]);
//                   if (!isNaN(numValue)) {
//                     dataPoint[key] = numValue;
//                   }
//                 }
//               });
              
//               // Add date as a string for display purposes
//               dataPoint.displayDate = dateValue;
              
//               return dataPoint;
//             });
            
//             setData(processedData);
//             setLoading(false);
//           },
//           error: (error) => {
//             setError('Error parsing CSV: ' + error.message);
//             setLoading(false);
//           }
//         });
//       } catch (err) {
//         setError('Error loading CSV file: ' + err.message);
//         setLoading(false);
//       }
//     };

//     loadCSV();
//   }, []);

//   // Function to determine which columns to plot
//   const getDataKeys = () => {
//     if (data.length === 0) return [];
    
//     // Find numeric columns (exclude index and displayDate)
//     const firstRow = data[0];
//     return Object.keys(firstRow).filter(key => 
//       key !== 'index' && 
//       key !== 'displayDate' && 
//       typeof firstRow[key] === 'number'
//     );
//   };

//   if (loading) return <div>Loading stock data...</div>;
//   if (error) return <div className="error">{error}</div>;
//   if (data.length === 0) return <div>No data available</div>;

//   const dataKeys = getDataKeys();
  
//   return (
//     <div className="stock-chart">
//       <h2>Google Stock History Scatter Plot</h2>
//       <div style={{ width: '100%', height: 400 }}>
//         <ResponsiveContainer>
//           <ScatterChart
//             margin={{ top: 20, right: 20, bottom: 20, left: 20 }}
//           >
//             <CartesianGrid />
//             <XAxis 
//               type="number" 
//               dataKey="index" 
//               name="Index" 
//               tickFormatter={(value) => {
//                 // Display date instead of index if available
//                 return data[value] ? data[value].displayDate : value;
//               }}
//             />
//             <YAxis type="number" dataKey={dataKeys[0]} name={dataKeys[0]} />
//             <Tooltip 
//               cursor={{ strokeDasharray: '3 3' }}
//               formatter={(value, name) => [value, name]}
//               labelFormatter={(value) => {
//                 return data[value] ? `Date: ${data[value].displayDate}` : value;
//               }}
//             />
//             {dataKeys.map((key, index) => (
//               <Scatter 
//                 key={key}
//                 name={key} 
//                 data={data} 
//                 fill={`#${Math.floor(Math.random()*16777215).toString(16)}`} 
//               />
//             ))}
//           </ScatterChart>
//         </ResponsiveContainer>
//       </div>
//     </div>
//   );
// };

// export default StockScatterPlot;