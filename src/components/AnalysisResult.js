import React, { useEffect } from 'react';

const analysisResult = ({ analysisResult }) => {
  useEffect(() => {
    if (analysisResult !== null) {
      // Scroll to the analysis result div
      const analysisDiv = document.getElementById('analysis-result');
      if (analysisDiv) {
        analysisDiv.scrollIntoView({ behavior: 'smooth' });
      }
    }
  }, [analysisResult]);

  return (
    <>
      {analysisResult !== null && (
        <div id="analysis-result" className="analysis-result">
          <h3>Analysis Result</h3>
          <p className={`result-value ${analysisResult < 1 ? 'negative' : 'positive'}`}>
            Result: {analysisResult.toFixed(2)}
          </p>
          <p className={`investment-value ${analysisResult < 1 ? 'negative' : 'positive'}`}>
            If you had invested $10,000, now you would have ${new Intl.NumberFormat().format((10000 * analysisResult).toFixed(2))}.
          </p>
        </div>
      )}
    </>
  );
};

export default analysisResult;