// import { Box } from "@mui/material";
// import { useEffect, useState } from "react";
// import {
//   ArrayInput,
//   AutocompleteInput,
//   Create,
//   NumberInput,
//   RadioButtonGroupInput,
//   ReferenceInput,
//   required,
//   SelectInput,
//   SimpleForm,
//   SimpleFormIterator,
//   TextInput,
//   useGetList,
// } from "react-admin";
// const paymentMethods = [
//   { id: "COD", name: "Tiền mặt" },
//   { id: "VNPAY", name: "Thanh toán VNPAY" },
// ];
// const OrderCreate = () => {
//   const [code, setCode] = useState(null);
//   const { data, isLoading } = useGetList("promotions", {
//     filter: { type: "VOUCHER", status: true },
//     pagination: { page: 1, perPage: 10000 },
//     sort: { field: "promotionName", order: "ASC" },
//   });
//   return (
//     <Create
//       transform={(data: any) => ({
//         order: {
//           deliveryName: data.deliveryName,
//           deliveryPhone: data.deliveryPhone,
//           email: data.email,
//           payment: { paymentMethod: data.paymentMethod },
//           coupon: { promotionCode: code },
//         },
//         productQuantities: data.productQuantities ?? [],
//       })}
//     >
//       <SimpleForm>
//         <TextInput source="deliveryName" label="Tên người nhận" />
//         <TextInput source="deliveryPhone" label="Số điện thoại" />
//         <TextInput source="email" label="Email" />
//         <SelectInput
//           source="promotionCode"
//           label="Mã khuyến mãi"
//           optionText="promotionName"
//           optionValue="promotionCode"
//           onChange={(event) => {
//             setCode(event.target.value);
//           }}
//           value={code}
//           choices={data ?? []}
//           disabled={isLoading}
//         />
//         <ArrayInput source="productQuantities" label="Sản phẩm">
//           <SimpleFormIterator>
//             <Box display={"flex"} flexDirection={"row"} gap={2}>
//               <ReferenceInput
//                 source="productId"
//                 reference="products"
//                 label="Sản phẩm"
//                 filter={{ status: true }}
//                 perPage={100}
//               >
//                 <AutocompleteInput
//                   optionText={(record) =>
//                     `ISBN: ${record.isbn} - Name: ${record.productName}`
//                   }
//                   filterToQuery={(searchText) => ({ isbn: searchText })}
//                 />
//               </ReferenceInput>
//               <NumberInput source="quantity" label="Số lượng" min={1} />
//             </Box>
//           </SimpleFormIterator>
//         </ArrayInput>
//         <RadioButtonGroupInput
//           source="paymentMethod"
//           label="Phương thức thanh toán"
//           choices={paymentMethods}
//           optionText="name"
//           optionValue="id"
//           validate={[required()]}
//         />
//       </SimpleForm>
//     </Create>
//   );
// };

// export default OrderCreate;
import {
  ArrayInput,
  AutocompleteInput,
  NumberInput,
  RadioButtonGroupInput,
  ReferenceInput,
  required,
  SelectInput,
  SimpleForm,
  SimpleFormIterator,
  TextInput,
  useNotify,
  useRedirect,
  useRefresh,
  useGetList,
  regex,
} from "react-admin";
import { Box } from "@mui/material";
import { useEffect, useState } from "react";
import axiosInstance from "../../api";

const paymentMethods = [
  { id: "COD", name: "Tiền mặt" },
  { id: "VNPAY", name: "Thanh toán VNPAY" },
];

const OrderCreate = () => {
  useEffect(() => {
    document.title = "Tạo mới đơn hàng";
  }, []);
  const [code, setCode] = useState(null);
  const notify = useNotify();
  const redirect = useRedirect();
  const refresh = useRefresh();

  const { data: promotions, isLoading } = useGetList("promotions", {
    filter: { type: "VOUCHER", status: true },
    pagination: { page: 1, perPage: 10000 },
    sort: { field: "promotionName", order: "ASC" },
  });

  const handleSubmit = async (formData: any) => {
    const requestBody = {
      order: {
        deliveryName: formData.deliveryName,
        deliveryPhone: formData.deliveryPhone,
        email: formData.email,
        payment: {
          paymentMethod: formData.paymentMethod,
        },
        coupon: {
          promotionCode: code != null ? code : null,
        },
      },
      productQuantities: formData.productQuantities ?? [],
    };

    try {
      const response = await axiosInstance.post("/staff/orders", requestBody);
      const paymentUrl = response.data?.url;

      if (paymentUrl) {
        window.open(paymentUrl, "_blank");
      } else {
        notify("ignore_key", {
          type: "success",
          messageArgs: {
            _: "Tạo đơn hàng thành công!",
          },
        });
        redirect(`/orders/${response.data.orderId}/show`);
        refresh();
      }
    } catch (error) {
      console.error(error);
      notify("Tạo đơn hàng thất bại!", { type: "error" });
    }
  };

  return (
    <SimpleForm onSubmit={handleSubmit}>
      <TextInput source="deliveryName" label="Tên người nhận" />
      <TextInput
        source="deliveryPhone"
        label="Số điện thoại"
        inputProps={{
          maxLength: 10,
          inputMode: "numeric",
          pattern: "[0-9]*",
        }}
        validate={[
          regex(
            /^0\d{9}$/,
            "Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số",
          ),
        ]}
      />
      <TextInput
        source="email"
        label="Email"
        validate={[
          regex(/^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/, "Email không hợp lệ"),
        ]}
      />
      <SelectInput
        source="promotionCode"
        label="Mã khuyến mãi"
        optionText="promotionName"
        optionValue="promotionCode"
        onChange={(event) => setCode(event.target.value)}
        value={code}
        choices={promotions ?? []}
        disabled={isLoading}
      />
      <ArrayInput source="productQuantities" label="Sản phẩm">
        <SimpleFormIterator>
          <Box display="flex" flexDirection="row" gap={2}>
            <ReferenceInput
              source="productId"
              reference="products"
              label="Sản phẩm"
              filter={{ status: true }}
              perPage={100}
            >
              <AutocompleteInput
                optionText={(record) =>
                  `ISBN: ${record.isbn} - Name: ${record.productName}`
                }
                filterToQuery={(searchText) => ({ isbn: searchText })}
              />
            </ReferenceInput>
            <NumberInput source="quantity" label="Số lượng" min={1} />
          </Box>
        </SimpleFormIterator>
      </ArrayInput>
      <RadioButtonGroupInput
        source="paymentMethod"
        label="Phương thức thanh toán"
        choices={paymentMethods}
        optionText="name"
        optionValue="id"
        validate={[required()]}
      />
    </SimpleForm>
  );
};

export default OrderCreate;
