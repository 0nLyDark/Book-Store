import {
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Box,
} from "@mui/material";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from "recharts";
import { useEffect, useState } from "react";
import axiosInstance from "../../api";

const RevenueChart = () => {
  const [data, setData] = useState([]);
  const [startDate, setStartDate] = useState("2024-01-01");
  const [endDate, setEndDate] = useState(() => {
    const today = new Date();
    return today.toISOString().slice(0, 10);
  });
  const [loading, setLoading] = useState(false);

  const fetchData = () => {
    if (!startDate || !endDate) return;

    setLoading(true);
    const params = { startDate, endDate };

    axiosInstance
      .get("/staff/statistics/revenue/date", { params })
      .then((res) => {
        console.log(res);
        setData(res.data);
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <Card sx={{ m: 2, p: 2, borderRadius: 3, boxShadow: 3 }}>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Biểu đồ doanh thu
        </Typography>

        {/* Bộ lọc ngày */}
        <Box
          sx={{
            display: "flex",
            flexDirection: { xs: "column", sm: "row" },
            maxWidth: 600,
            gap: 2,
            mb: 2,
          }}
        >
          <TextField
            fullWidth
            label="Từ ngày"
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            inputProps={{ max: endDate }}
            InputLabelProps={{ shrink: true }}
          />
          <TextField
            fullWidth
            label="Đến ngày"
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            inputProps={{ min: startDate }}
            InputLabelProps={{ shrink: true }}
          />

          <Button
            // variant="contained"
            color="primary"
            onClick={fetchData}
            disabled={loading}
            sx={{ minWidth: "100px" }}
          >
            {loading ? "Đang tải..." : "Lọc"}
          </Button>
        </Box>

        {/* Biểu đồ */}
        {data.length === 0 ? (
          <Typography color="textSecondary">Không có dữ liệu.</Typography>
        ) : (
          <ResponsiveContainer
            // style={{ overflowY: "auto" }}
            width="100%"
            height={300}
          >
            <LineChart data={data}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis
                tickFormatter={(value) =>
                  new Intl.NumberFormat("vi-VN", {
                    notation: "compact",
                    compactDisplay: "short",
                  }).format(value)
                }
              />
              <Tooltip
                formatter={(value: number) => `${value.toLocaleString()} đ`}
              />
              <Line
                name="Doanh thu"
                type="monotone"
                dataKey="revenue"
                stroke="#1976d2"
                strokeWidth={2}
              />
            </LineChart>
          </ResponsiveContainer>
        )}
      </CardContent>
    </Card>
  );
};

export default RevenueChart;
