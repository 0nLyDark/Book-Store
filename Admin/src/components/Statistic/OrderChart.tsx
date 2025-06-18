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

const OrderChart = () => {
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
    const orderStatus = "COMPLETED";
    const params = { orderStatus: orderStatus, startDate, endDate };

    axiosInstance
      .get("/staff/statistics/orderCount/date", { params })
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
          Biểu đồ đơn hàng
        </Typography>

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
            InputLabelProps={{ shrink: true }}
          />
          <TextField
            fullWidth
            label="Đến ngày"
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            InputLabelProps={{ shrink: true }}
          />
          <Button
            // variant="contained"
            color="primary"
            onClick={fetchData}
            disabled={loading}
            sx={{ minWidth: 100 }}
          >
            Lọc
          </Button>
        </Box>

        {data.length === 0 ? (
          <Typography color="textSecondary">Không có dữ liệu.</Typography>
        ) : (
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={data}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis allowDecimals={false} />
              <Tooltip
                formatter={(value: number) => `${value.toLocaleString()} đơn`}
              />
              <Line
                name="Đã hoàn thành"
                type="monotone"
                dataKey="completedCount"
                stroke="#4caf50" // màu xanh lá
                strokeWidth={2}
              />
              {/* <Line
                name="Đang vận chuyển"
                type="monotone"
                dataKey="shippedCount"
                stroke="#ff5722" // màu cam
                strokeWidth={2}
              />
              <Line
                name="Chưa giao hàng"
                type="monotone"
                dataKey="paidCount"
                stroke="#2196f3" // màu xanh dương
                strokeWidth={2}
              /> */}
              <Line
                name="Đã hủy"
                type="monotone"
                dataKey="cancelledCount"
                stroke="#9e9e9e" // màu xám
                strokeWidth={2}
              />
              <Line
                name="Thất bại"
                type="monotone"
                dataKey="failedCount"
                stroke="#f44336" // màu đỏ
                strokeWidth={2}
              />
            </LineChart>
          </ResponsiveContainer>
        )}
      </CardContent>
    </Card>
  );
};

export default OrderChart;
