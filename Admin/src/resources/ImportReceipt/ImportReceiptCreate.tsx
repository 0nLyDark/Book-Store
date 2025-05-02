import { Box } from "@mui/material";
import { useEffect, useState } from "react";
import {
  ArrayInput,
  AutocompleteInput,
  Create,
  NumberInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  SimpleFormIterator,
} from "react-admin";

const ImportReceiptCreate = () => {
  const [supplierId, setSupplierId] = useState(null);

  useEffect(() => {
    console.log("Supplier ID changed:", supplierId);
  }, [supplierId]);
  return (
    <Create>
      <SimpleForm>
        <ReferenceInput source="supplier.supplierId" reference="suppliers">
          <SelectInput
            optionText="supplierName"
            label="Nhà cung cấp"
            variant="outlined"
            onChange={(event) => setSupplierId(event.target.value)}
          />
        </ReferenceInput>

        {/* Chỉ hiển thị phần chọn sản phẩm khi có supplierId */}
        {supplierId && (
          <ArrayInput source="importReceiptItems" label="Sản phẩm">
            <SimpleFormIterator>
              <Box display={"flex"} flexDirection={"row"} gap={2}>
                <ReferenceInput
                  source="product.productId"
                  reference="products"
                  label="Sản phẩm"
                  filter={{ supplierId }}
                >
                  <AutocompleteInput
                    optionText={(record) =>
                      `ISBN: ${record.isbn} - Name: ${record.productName}`
                    }
                    filterToQuery={(searchText) => ({ isbn: searchText })}
                  />
                </ReferenceInput>
                <NumberInput source="quantity" label="Số lượng" min={0} />
                <NumberInput source="price" label="Giá nhập" min={0} />
              </Box>
            </SimpleFormIterator>
          </ArrayInput>
        )}
      </SimpleForm>
    </Create>
  );
};
// const ImportReceiptCreate = () => (
//   <Create>
//     <SimpleForm>
//       <ReferenceInput source="supplier.supplierId" reference="suppliers">
//         <SelectInput
//           optionText="supplierName"
//           label="Nhà cung cấp"
//           variant="outlined"
//         />
//         <ArrayInput source="importReceiptItems" label="Sản phẩm">
//           <SimpleFormIterator>
//             <ReferenceInput
//               source="product.productId"
//               reference="products"
//               label="Sản phẩm"
//             >
//               <AutocompleteInput
//                 optionText={(record) =>
//                   `ISBN: ${record.isbn} - Name: ${record.productName}`
//                 }
//                 filterToQuery={(searchText) => ({ isbn: searchText })}
//               />
//             </ReferenceInput>

//             <NumberInput source="quantity" label="Số lượng" min={0} />
//             <NumberInput source="price" label="Giá nhập" min={0} />
//             <NumberInput
//               source="discount"
//               label="Giảm giá (%)"
//               min={0}
//               max={100}
//             />
//           </SimpleFormIterator>
//         </ArrayInput>
//       </ReferenceInput>
//     </SimpleForm>
//   </Create>
// );

export default ImportReceiptCreate;
