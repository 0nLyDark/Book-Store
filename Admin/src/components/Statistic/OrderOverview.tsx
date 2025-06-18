import { useEffect, useState } from "react";
import axiosInstance from "../../api";
import { Card, CardContent, Typography } from "@mui/material";

const OrderOverview = () => {
  const [statsOrder, setStatsOrder] = useState({
    completedCount: 0,
    shippedCount: 0,
    paidCount: 0,
    pendingCount: 0,
    cancelledCount: 0,
    failedCount: 0,
  });

  useEffect(() => {
    axiosInstance
      .get("/staff/statistics/overview/order")
      .then((res) => setStatsOrder(res.data))
      .catch((err) => console.error("Error loading stats:", err));
  }, []);

  const statItems = [
    { label: "Đã hoàn thành", value: statsOrder.completedCount },
    { label: "Đang vận chuyển", value: statsOrder.shippedCount },
    { label: "Chưa giao hàng", value: statsOrder.paidCount },
    { label: "Đã hủy", value: statsOrder.cancelledCount },
    { label: "Thất bại", value: statsOrder.failedCount },
  ];

  return (
    <>
      <Typography variant="h6">Thống kê đơn hàng:</Typography>
      <div style={containerStyle}>
        {statItems.map((item, index) => (
          <Card key={index} style={cardStyle}>
            <CardContent>
              <Typography variant="subtitle2" color="textSecondary">
                {item.label}
              </Typography>
              <Typography variant="h6" color="primary">
                {item.value}
              </Typography>
            </CardContent>
          </Card>
        ))}
      </div>
    </>
  );
};
export default OrderOverview;

const cardStyle: React.CSSProperties = {
  flex: "1 1 180px",
  margin: "10px",
  minHeight: "100px",
  textAlign: "center",
  background: "#f5f5f5",
};

const containerStyle: React.CSSProperties = {
  display: "flex",
  flexWrap: "wrap",
  justifyContent: "space-between",
  gap: "10px",
};
