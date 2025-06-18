import { useEffect, useState } from "react";
import { Box } from "@mui/material";

import RevenueChart from "./components/Statistic/RevenueChart";
import OrderChart from "./components/Statistic/OrderChart";
import Overview from "./components/Statistic/Overview";
import OrderOverview from "./components/Statistic/OrderOverview";
import BestSellingProductList from "./components/Statistic/BestSellingProduct";
// import { TopProductsChart } from "./TopProductsChart";

export const Dashboard = () => (
  <Box
    sx={{
      display: "flex",
      flexDirection: "column",
      gap: 3,
      mt: 4,
    }}
  >
    <Box>
      <Overview />
    </Box>
    <Box>
      <OrderOverview />
    </Box>
    <Box sx={{ flex: 1 }}>
      <RevenueChart />
    </Box>
    <Box sx={{ flex: 1 }}>
      <OrderChart />
    </Box>
    <Box
      sx={{
        display: "flex",
        flexDirection: { xs: "column", md: "row" },
        gap: 1,
      }}
    >
      <Box sx={{ flex: 1 }}></Box>
      <Box sx={{ flex: 1 }}></Box>
    </Box>
    <Box>
      <BestSellingProductList />
    </Box>
  </Box>
);
