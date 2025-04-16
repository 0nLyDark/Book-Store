import {
  ArrayField,
  Datagrid,
  FunctionField,
  ImageField,
  NumberField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const CartShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="cartId" label="Cart ID" />
      <TextField source="user.userId" label="User ID" />
      <TextField source="user.fullName" label="Tên người dùng" />
      <TextField source="user.email" label="Email" />
      <TextField source="user.mobileNumber" label="Số điện thoại" />
      <NumberField
        source="totalPrice"
        label="Tổng tiền"
        options={{ style: "currency", currency: "VND" }}
        locales="vi-VN"
      />
      <ArrayField source="cartItems" label="Sản phẩm trong giỏ hàng">
        <Datagrid bulkActionButtons={false} rowClick={false}>
          <TextField source="product.productId" label="Product ID" />
          <TextField source="product.productName" label="Tên sản phẩm" />
          <TextField source="product.isbn" label="Mã sản phẩm" />
          <ImageField
            source="product.images[0]"
            label="Hình ảnh"
            title="Tên sản phẩm"
          />
          <FunctionField
            label="Danh mục"
            render={(record) => {
              return (
                <span>
                  {record.product.categories.map(
                    (category: any, index: number) => (
                      <span key={index}>
                        - {category.categoryName}
                        <br />
                      </span>
                    ),
                  )}
                </span>
              );
            }}
          />
          <NumberField
            source="product.price"
            label="Giá"
            options={{ style: "currency", currency: "VND" }}
            locales="vi-VN"
          />
          <NumberField source="quantity" label="Số lượng" />
          <FunctionField
            label="% Giảm giá"
            render={(record) => `${record.product.discount ?? 0} %`}
            sortBy="product.discount"
          />
          <FunctionField
            label="Tổng tiền"
            render={(record) =>
              (
                (record.product.price ?? 0) *
                (record.product.discount != 0
                  ? (100 - record.product.discount) / 100
                  : 1) *
                (record.quantity ?? 0)
              ).toLocaleString("vi-VN", {
                style: "currency",
                currency: "VND",
              })
            }
          />
        </Datagrid>
      </ArrayField>
    </SimpleShowLayout>
  </Show>
);
export default CartShow;
