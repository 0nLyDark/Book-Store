import React, { useEffect, useState } from "react";
import {
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
} from "@mui/material";
import axiosInstance from "../../api";
import { API_IMAGE } from "../../dataProvider";

interface BestSellingProduct {
  id: number;
  productId: number;
  isbn: string;
  productName: string;
  image: string;
  totalQuantitySold: number;
}

const BestSellingProductCustomList: React.FC = () => {
  const [products, setProducts] = useState<BestSellingProduct[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    axiosInstance
      .get<BestSellingProduct[]>("/staff/statistics/best-selling-products")
      .then((res) => {
        const dataWithId = res.data.map((item) => ({
          ...item,
          id: item.productId,
        }));
        setProducts(dataWithId);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error fetching best selling products:", err);
        setError("Error fetching data");
        setLoading(false);
      });
  }, []);

  if (loading) return <Typography>Loading...</Typography>;
  if (error) return <Typography color="error">{error}</Typography>;

  return (
    <Card sx={{ m: 2, p: 2, borderRadius: 3, boxShadow: 3 }}>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Danh sách 15 sản phẩm bán chạy nhất
        </Typography>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell sx={HeaderTable}>Top</TableCell>
              <TableCell sx={HeaderTable}>ISBN</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>Tên sản phẩm</TableCell>
              <TableCell sx={HeaderTable}>Hình ảnh</TableCell>
              <TableCell sx={HeaderTable}>Số lượng đã bán</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {products.map((product, index) => (
              <TableRow key={product.id}>
                <TableCell sx={{ textAlign: "center", fontWeight: "bold" }}>
                  {index + 1}
                </TableCell>
                <TableCell sx={{ textAlign: "center" }}>
                  {product.isbn}
                </TableCell>
                <TableCell>{product.productName}</TableCell>
                <TableCell sx={{ textAlign: "center" }}>
                  <img
                    src={`${API_IMAGE}${product.image}`}
                    alt={product.productName}
                    style={{ width: 75, height: 125, objectFit: "cover" }}
                  />
                </TableCell>
                <TableCell sx={{ textAlign: "right", p: 4 }}>
                  {product.totalQuantitySold}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
};

export default BestSellingProductCustomList;

const HeaderTable: React.CSSProperties = {
  textAlign: "center",
  fontWeight: "bold",
};
const CellTable: React.CSSProperties = {
  textAlign: "center",
  fontWeight: "bold",
};
