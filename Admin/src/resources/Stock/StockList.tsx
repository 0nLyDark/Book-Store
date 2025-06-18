import {
  Box,
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Paper,
  Typography,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { useEffect, useState } from "react";
import {
  BooleanInput,
  Button,
  Datagrid,
  DateField,
  ImageField,
  List,
  ListContextProvider,
  ListControllerSuccessResult,
  NumberField,
  SelectInput,
  SimpleShowLayout,
  SortPayload,
  TextField,
  TextInput,
  useNotify,
  useRecordContext,
  useRefresh,
} from "react-admin";
import TextFieldMUI from "@mui/material/TextField";
import axiosInstance from "../../api";
interface OverviewStock {
  totalProductWarning: number;
  totalProduct: number;
  totalStock: number;
  totalCost: number;
}
const BtnShow = () => {
  const [open, setOpen] = useState(false);
  const record = useRecordContext();

  return (
    <Box>
      <Button
        onClick={() => {
          setOpen(true);
        }}
        label="Xem chi tiết"
        variant="outlined"
      />
      <Dialog
        open={open}
        onClose={() => setOpen(false)}
        fullWidth
        maxWidth="lg"
        sx={{ borderRadius: 25 }}
      >
        <DialogTitle
          sx={{
            textAlign: "center",
            fontWeight: "bold",
            fontSize: "1.5rem",
          }}
        >
          Chi tiết tồn kho
          <IconButton
            aria-label="close"
            onClick={() => setOpen(false)}
            sx={{
              position: "absolute",
              right: 8,
              top: 8,
              color: (theme) => theme.palette.grey[500],
            }}
          >
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <Box
            sx={{
              display: "grid",
              gridTemplateColumns: "repeat(2, 1fr)",
              gap: 2,
            }}
          >
            <SimpleShowLayout record={record}>
              <TextField source="isbn" label="Mã sản phẩm" />
              <TextField source="productName" label="Tên sản phẩm" />
            </SimpleShowLayout>
            <SimpleShowLayout record={record}>
              <NumberField source="quantity" label="Số lượng tồn kho" />
              <NumberField
                source="totalCost"
                label="Tổng tiền"
                locales="vi-VN"
                options={{
                  style: "currency",
                  currency: "VND",
                  minimumFractionDigits: 0,
                }}
              />
            </SimpleShowLayout>
          </Box>
          <ShowDialogStock data={record?.stockItems || []} />
        </DialogContent>
      </Dialog>
    </Box>
  );
};
const ShowDialogStock = ({ data }: any) => {
  // const record = useRecordContext();
  // const data = record?.stockItems || [];

  if (!data.length) return null;

  const [sort, setSort] = useState<SortPayload>({
    field: "importDate",
    order: "ASC",
  });
  const listContext: ListControllerSuccessResult<any> = {
    data: [...data].sort((a, b) => {
      const aValue = a[sort.field];
      const bValue = b[sort.field];
      if (aValue < bValue) return sort.order === "ASC" ? -1 : 1;
      if (aValue > bValue) return sort.order === "ASC" ? 1 : -1;
      return 0;
    }),
    total: data.length,
    sort,
    setSort,
    resource: "stockItems",
    error: null,
    isFetching: false,
    isLoading: false,
    isPending: false,
    onSelect: () => {},
    onToggleItem: () => {},
    selectedIds: [],
    filterValues: {},
    displayedFilters: {},
    showFilter: () => {},
    hideFilter: () => {},
    hasNextPage: false,
    hasPreviousPage: false,
    page: 1,
    perPage: data.length,
    setPage: () => {},
    setPerPage: () => {},
    defaultTitle: "",
    refetch: () => {},
    onSelectAll: () => {},
    onUnselectItems: () => {},
    setFilters: () => {},
  };

  return (
    <ListContextProvider value={listContext}>
      <Datagrid
        bulkActionButtons={false}
        sx={{
          "& .RaDatagrid-row": {
            height: "60px",
          },
        }}
      >
        <TextField source="supplierName" label="Nhà cung cấp" />
        <NumberField
          source="cost"
          label="Giá nhập"
          locales="vi-VN"
          options={{
            style: "currency",
            currency: "VND",
            minimumFractionDigits: 0,
          }}
        />
        <NumberField source="quantity" label="Số lượng" />
        <NumberField
          source="totalCost"
          label="Tổng tiền"
          locales="vi-VN"
          options={{
            style: "currency",
            currency: "VND",
            minimumFractionDigits: 0,
          }}
        />
        <DateField source="importDate" label="Ngày nhập" showTime />
      </Datagrid>
    </ListContextProvider>
  );
};
const OverviewStock = () => {
  const notify = useNotify();
  const refresh = useRefresh();
  const [qtyWarning, setQtyWarning] = useState<number>(100);
  const [data, setData] = useState<OverviewStock>({
    totalProductWarning: 0,
    totalProduct: 0,
    totalStock: 0,
    totalCost: 0,
  });
  const updateQtyWarning = () => {
    axiosInstance
      .put(`/staff/stocks/qtyWarning/${qtyWarning}`)
      .then((res) => {
        getStockOverview();
        notify("Đã cập nhật số lượng cảnh báo thành công", {
          type: "success",
        });
      })
      .catch((error) => {
        console.error("Erorr update quantity warning: ", error);
        if (error.status === 403) {
          notify("Bạn không có quyền thay đổi dữ liệu này", {
            type: "warning",
          });
        }
        notify("Đã cập nhật số lượng cảnh báo thành công", {
          type: "success",
        });
      });
  };
  const getStockOverview = () => {
    axiosInstance
      .get("/staff/stocks/overview")
      .then((res) => {
        console.log("stockOverview: ", res.data);
        setData(res.data);
      })
      .catch((error) => {
        console.error("Error get stock overview:", error);
        notify("Đã xảy ra lỗi khi lấy dữ liệu thống kê tồn kho", {
          type: "error",
        });
      });
  };
  useEffect(() => {
    axiosInstance
      .get("/staff/stocks/qtyWarning")
      .then((res) => {
        setQtyWarning(res.data);
      })
      .catch((error) => {
        console.error("Error get qtyWarning:", error);
      });
  }, []);
  useEffect(() => {
    getStockOverview();
  }, []);
  const stats = [
    {
      label: "Số sản phẩm sắp hết hàng",
      value: data.totalProductWarning,
      color: "#fff3cd",
    },
    {
      label: "Tổng loại sản phẩm",
      value: data.totalProduct,
    },
    {
      label: "Tổng số lượng",
      value: data.totalStock,
    },
    {
      label: "Tổng giá trị",
      value: data.totalCost.toLocaleString("vi-VN", {
        style: "currency",
        currency: "VND",
        minimumFractionDigits: 0,
      }),
    },
  ];

  return (
    <Box display="flex" gap={2} my={2} alignItems="flex-start" flexWrap="wrap">
      {/* Phần thống kê bên trái */}
      <Box display="flex" gap={2} flex="3" flexWrap="wrap">
        {stats.map((stat, index) => (
          <Paper
            key={index}
            elevation={3}
            sx={{
              p: 2,
              minWidth: 200,
              backgroundColor: stat.color || "#f9f9f9",
              flex: "1 1 250px",
            }}
          >
            <Typography variant="subtitle1" gutterBottom>
              {stat.label}
            </Typography>
            <Typography variant="h6" color="primary">
              {stat.value}
            </Typography>
          </Paper>
        ))}
      </Box>

      {/* Ô nhập cảnh báo bên phải */}
      <Box flex="1" minWidth={250}>
        <Paper elevation={3} sx={{ p: 2 }}>
          <Typography variant="subtitle1" gutterBottom>
            Thiết lập cảnh báo tồn kho
          </Typography>
          <TextFieldMUI
            type="number"
            fullWidth
            label="Số lượng cảnh báo"
            value={qtyWarning}
            onChange={(e) => setQtyWarning(Number(e.target.value))}
            sx={{ mb: 2 }}
          />
          <Button variant="contained" fullWidth onClick={updateQtyWarning}>
            Thiết lập
          </Button>
        </Paper>
      </Box>
    </Box>
  );
};
const StocktFilter = [
  <TextInput label="Tìm theo mã sách" source="isbn" alwaysOn />,
  <TextInput label="Tìm theo tên" source="keyword" alwaysOn />,
  <SelectInput
    alwaysOn
    source="status"
    label="Trạng thái"
    choices={[
      { id: true, name: "Hiện" },
      { id: false, name: "Ẩn" },
    ]}
  />,
  <BooleanInput
    label="Hiển thị sản phẩm sắp hết hàng"
    source="isWarning"
    alwaysOn
  />,
];
const StockList = () => {
  useEffect(() => {
    document.title = "Quản lý tồn kho";
  }, []);
  return (
    <>
      <OverviewStock />
      <List
        filters={StocktFilter}
        sx={{
          "& .column-image": {
            textAlign: "center",
          },
          "& .RaDatagrid-headerCell": {
            textAlign: "center",
          },
        }}
      >
        <Datagrid bulkActionButtons={false}>
          <TextField source="isbn" label="Mã sản phấm" />
          <TextField source="productName" label="Tên sản phấm" />
          <ImageField
            source="images[0]"
            label="Hình ảnh"
            textAlign="center"
            sortable={false}
          />
          <NumberField
            source="quantity"
            label="Số lượng tồn kho"
            textAlign="center"
          />
          <NumberField
            source="totalCost"
            label="Tổng giá trị"
            sortable={false}
            options={{
              style: "currency",
              currency: "VND",
              minimumFractionDigits: 0,
            }}
            locales="vi-VN"
            sx={{
              "& .RaField-label": { textAlign: "center" },
              textAlign: "center",
            }}
          />
          <BtnShow />
        </Datagrid>
      </List>
    </>
  );
};

export default StockList;
