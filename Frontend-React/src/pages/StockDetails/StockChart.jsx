import { useEffect, useState } from "react";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import api from "@/Api/api";

const StockChart = ({ coinId: propCoinId }) => {
  const dispatch = useDispatch();
  const { coinId: paramCoinId } = useParams();
  const { auth } = useSelector((store) => store);
  const [chartData, setChartData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedTimeframe, setSelectedTimeframe] = useState("1");

  // ✅ Use prop coinId from Home.jsx, fallback to param coinId from URL
  const coinId = propCoinId || paramCoinId;
  const jwt = auth?.jwt || localStorage.getItem("jwt");

  const timeframes = [
    { label: "1 Day", value: "1" },
    { label: "1 Week", value: "7" },
    { label: "1 Month", value: "30" },
    { label: "3 Months", value: "90" },
    { label: "6 Months", value: "180" },
    { label: "1 Year", value: "365" },
  ];

  useEffect(() => {
    const fetchChartData = async () => {
      if (!coinId || !jwt) {
        console.warn("⚠️ Missing coinId or JWT:", { coinId, jwt });
        return;
      }

      setLoading(true);
      try {
        console.log(`📈 Fetching chart for ${coinId} (${selectedTimeframe} days)`);

        const response = await api.get(`/api/coins/${coinId}/chart`, {
          params: { days: selectedTimeframe },
          headers: { Authorization: `Bearer ${jwt}` },
        });

        if (response.data && Array.isArray(response.data)) {
          console.log("✅ Raw chart data received:", response.data.length, "points");
          
          // ✅ FIXED: Transform API response [[timestamp, price]] to [{time, price}]
          const transformedData = response.data.map((point) => {
            if (Array.isArray(point) && point.length === 2) {
              return {
                time: new Date(point[0]).toLocaleTimeString(),
                timestamp: point[0],
                priceUsd: parseFloat(point[1]).toFixed(2),
              };
            }
            return point;
          });

          console.log("✅ Transformed chart data:", transformedData.length, "points");
          console.log("📊 First point:", transformedData[0]);
          console.log("📊 Last point:", transformedData[transformedData.length - 1]);

          setChartData(transformedData);
        } else {
          console.warn("⚠️ Response data is not an array:", response.data);
          setChartData([]);
        }
      } catch (error) {
        console.error("❌ Error fetching chart:", {
          message: error.message,
          status: error.response?.status,
          data: error.response?.data,
        });
        setChartData([]);
      } finally {
        setLoading(false);
      }
    };

    fetchChartData();
  }, [coinId, selectedTimeframe, jwt]);

  if (!coinId) {
    return (
      <div className="p-5 h-96 flex items-center justify-center text-slate-400">
        <p>No coin selected</p>
      </div>
    );
  }

  return (
    <div className="p-5">
      <div className="flex gap-3 mb-5 flex-wrap">
        {timeframes.map((tf) => (
          <button
            key={tf.value}
            onClick={() => setSelectedTimeframe(tf.value)}
            className={`px-4 py-2 rounded text-sm transition-colors ${
              selectedTimeframe === tf.value
                ? "bg-amber-500 text-white"
                : "bg-slate-700 text-slate-200 hover:bg-slate-600"
            }`}
          >
            {tf.label}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="h-96 flex items-center justify-center">
          <p className="text-slate-400">📊 Loading chart data...</p>
        </div>
      ) : chartData && chartData.length > 0 ? (
        <div className="w-full h-96 bg-slate-900/50 rounded-lg p-2">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart
              data={chartData}
              margin={{ top: 5, right: 30, left: 0, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" stroke="#444" vertical={false} />
              <XAxis
                dataKey="time"
                stroke="#888"
                tick={{ fontSize: 12 }}
                interval={Math.floor(chartData.length / 6)}
              />
              <YAxis
                stroke="#888"
                tick={{ fontSize: 12 }}
                domain={["dataMin - 100", "dataMax + 100"]}
              />
              <Tooltip
                contentStyle={{
                  backgroundColor: "#1a1a1a",
                  border: "1px solid #666",
                  borderRadius: "4px",
                  padding: "8px",
                }}
                labelStyle={{ color: "#fff" }}
                formatter={(value) => [`$${value}`, "Price"]}
              />
              <Line
                type="monotone"
                dataKey="priceUsd"
                stroke="#fbbf24"
                dot={false}
                strokeWidth={2}
                isAnimationActive={false}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      ) : (
        <div className="h-96 flex flex-col items-center justify-center bg-slate-900/50 rounded-lg">
          <p className="text-slate-400 mb-2">📉 No chart data available</p>
          <p className="text-slate-500 text-sm">
            {coinId ? `For ${coinId} (${selectedTimeframe} days)` : "Select a coin"}
          </p>
        </div>
      )}
    </div>
  );
};

export default StockChart;
