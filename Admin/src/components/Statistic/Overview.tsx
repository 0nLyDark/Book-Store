import React, { useEffect, useState } from "react";
import axiosInstance from "../../api";
import { Box, Card, CardContent, Typography } from "@mui/material";

type StatisticsData = {
  totalRevenue: number;
  totalProfit: number;
  totalOrders: number;
  totalCustomers: number;
  totalProducts: number;
};

const Overview = () => {
  const [stats, setStats] = useState({
    totalRevenue: 0,
    totalProfit: 0,
    totalOrders: 0,
    totalCustomers: 0,
    totalProducts: 0,
  });

  useEffect(() => {
    axiosInstance
      .get("/staff/statistics/overview")
      .then((res) => setStats(res.data))
      .catch((err) => console.error("Error loading stats:", err));
  }, []);

  const statItems = [
    {
      label: "Doanh thu",
      value: Math.round(stats.totalRevenue).toLocaleString("vi-VN") + " ₫",
    },
    {
      label: "Lợi nhuận",
      value: Math.round(stats.totalProfit).toLocaleString("vi-VN") + " ₫",
    },
    // { label: "Đơn hàng", value: stats.totalOrders },
    { label: "Khách hàng", value: stats.totalCustomers },
    { label: "Sản phẩm", value: stats.totalProducts },
  ];

  return (
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
  );
};
export default Overview;

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
