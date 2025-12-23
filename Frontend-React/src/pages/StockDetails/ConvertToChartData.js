export function convertToUnixTimestamp(data) {
    const convertedData = [];
    for (const [key, value] of Object.entries(data)) {
        const timestamp = new Date(key).getTime();
        convertedData.push([timestamp, parseFloat(value['1. open'])]);
    }
    return convertedData;
}

export const normalizeCandlesFromBackend = (candlesArray) => {
  // expects [{ period: <iso|ms>, open, high, low, close }, ...]
  if (!Array.isArray(candlesArray)) return { line: [], candle: [] };
  const candle = candlesArray.map(c => ({
    x: new Date(c.period).getTime(),
    y: [parseFloat(c.open), parseFloat(c.high), parseFloat(c.low), parseFloat(c.close)]
  }));
  const line = candlesArray.map(c => ({ x: new Date(c.period).getTime(), y: parseFloat(c.close) }));
  return { line, candle };
};



