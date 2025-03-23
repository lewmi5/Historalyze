# Historalyze

Historalyze is a web application that allows users to analyze historical stock data and simulate various investment strategies to see potential returns.

https://github.com/user-attachments/assets/f2aa8985-d36f-48d6-80ab-920e9ba61c8b

## 📊 Overview

Historalyze helps investors evaluate different trading strategies using historical stock data. The application retrieves stock price information, visualizes it, and allows users to apply various trading strategies with customizable parameters to calculate potential profits.

## ✨ Features

- **Stock Data Visualization**: Display historical stock price data in an interactive chart
- **Multiple Strategy Support**: Choose from various trading strategies to analyze performance
- **Customizable Parameters**: Adjust strategy parameters to optimize results
- **Profit Simulation**: Calculate how much profit an investment would have generated using the selected strategy
- **Dynamic Strategy Loading**: Backend automatically discovers and loads available strategy implementations
- **Factory Design Pattern**: Factory identifies and load strategies dynamically based on user-friendly names

## 🛠️ Tech Stack

### Backend
- Java Spring Boot for the REST API
- Dynamic class loading for strategy implementations
- CSV data processing for stock information

### Frontend
- React.js for the user interface
- Interactive charts for data visualization
- Responsive design for desktop and mobile use

## 🏗️ Architecture

The application follows a client-server architecture:

1. **Backend (Java Spring Boot)**
   - Handles API requests
   - Downloads and processes historical stock data
   - Implements trading strategies
   - Performs calculations for profit analysis

2. **Frontend (React)**
   - User interface for selecting stocks and strategies
   - Visualizes stock data and analysis results
   - Communicates with backend via REST API

## 📋 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/data` | GET | Connection test endpoint |
| `/api/stock_names` | GET | Retrieves list of available stock names |
| `/api/strategy_names` | GET | Gets names of available trading strategies |
| `/api/strategy_descritption` | POST | Fetches description for a specified strategy |
| `/api/prices` | POST | Gets historical price data for a specific stock |
| `/api/analyze` | POST | Calculates profit based on selected strategy and parameters |

## Contributors
- [Kuba Siwiec](https://github.com/jakub-siwiec-3)
- [Michał Lewandowski](https://github.com/lewmi5)
