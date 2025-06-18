import { Check, X } from "@mui/icons-material";
import {
  ArrayField,
  Button,
  Datagrid,
  DateField,
  FunctionField,
  ImageField,
  NumberField,
  Show,
  SimpleShowLayout,
  TextField,
  TopToolbar,
  useNotify,
  useRecordContext,
  useRefresh,
  useUpdate,
} from "react-admin";
import ExportPDFButton from "../../components/ExportPDFButton";
import { useEffect } from "react";
const ChangeStatusButton = () => {
  const record = useRecordContext();
  const notify = useNotify();
  const refresh = useRefresh();
  const [update, { isLoading }] = useUpdate();
  if (!record) return null;
  const handleClick = () => {
    const newStatus = !record.status;
    update(
      "import-receipts", // Tên resource
      {
        id: record.id,
        data: { id: record.id, status: newStatus },
        previousData: record,
      },
      {
        onSuccess: () => {
          notify("Trạng thái đã được cập nhật", { type: "success" });
          refresh(); // làm mới lại show page
        },
        onError: (error: any) => {
          if (error.status == 403) {
            notify(`Bạn không có quyền thay đổi trạng thái đơn hàng`, {
              type: "error",
            });
          } else {
            notify(`Lỗi: ${error.message}`, { type: "error" });
          }
        },
      },
    );
  };

  return (
    <>
      <Button
        label="Thay đổi trạng thái"
        onClick={handleClick}
        disabled={isLoading}
      >
        {record.status === "pending" ? <Check /> : <X />}
      </Button>
    </>
  );
};

const ShowActions = () => (
  <TopToolbar>
    <CustomPDFButton />
    <ChangeStatusButton />
  </TopToolbar>
);

const CustomPDFButton = () => {
  const record = useRecordContext();
  if (!record) {
    return <span>Loading ...</span>;
  }

  if (!record.id) {
    return <span>No ImportReceipt ID</span>;
  }
  console.log("record", record);

  return <ExportPDFButton type="importReceipt" data={record} />;
};
const ImportReceiptShow = () => {
  useEffect(() => {
    document.title = "Chi tiết phiếu nhập hàng";
  }, []);
  return (
    <Show actions={<ShowActions />}>
      <SimpleShowLayout sx={{ mb: 4 }}>
        <TextField source="createdBy" label="ID tài khoản tạo phiếu nhập" />
        <TextField source="supplier.supplierName" label="Nhà cung cấp" />
        <DateField source="importDate" label="Ngày nhập" showTime />
        <NumberField
          source="totalAmount"
          label="Tổng tiền"
          options={{ style: "currency", currency: "VND" }}
          locales="vi-VN"
        />
        <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Tốt" : "Lỗi")}
          sortBy="status"
        />
        <ArrayField source="importReceiptItems" label="Sản phẩm nhập hàng">
          <Datagrid bulkActionButtons={false} rowClick={false}>
            {/* <TextField source="product.productId" label="Product ID" /> */}
            <TextField source="product.isbn" label="Mã sản phẩm" />
            <TextField source="product.productName" label="Tên sản phẩm" />
            <ImageField
              source="product.images[0]"
              label="Hình ảnh"
              title="product.images[0]"
            />
            <NumberField
              source="price"
              label="Giá"
              options={{ style: "currency", currency: "VND" }}
              locales="vi-VN"
            />
            <NumberField source="quantity" label="Số lượng nhập" />
            <NumberField
              source="totalPrice"
              label="Giá nhập hàng"
              options={{ style: "currency", currency: "VND" }}
              locales="vi-VN"
            />
          </Datagrid>
        </ArrayField>
      </SimpleShowLayout>
    </Show>
  );
};
export default ImportReceiptShow;
