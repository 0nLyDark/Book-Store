import { useEffect } from "react";
import {
  AutocompleteInput,
  BooleanInput,
  Edit,
  NumberInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
  useGetList,
} from "react-admin";
import { useWatch } from "react-hook-form";

const MenuEdit = () => {
  useEffect(() => {
    document.title = "Chỉnh sửa menu";
  }, []);
  return (
    <Edit mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="menuId" label="Menu ID" disabled />
        <TextInput source="name" label="Tên menu" />
        <TextInput source="link" label="Đường dẫn liên kết" />
        <ReferenceInput
          source="parent.menuId"
          reference="menus"
          label="Menu cha"
        >
          <AutocompleteInput
            label="Menu cha"
            optionText="name"
            variant="outlined"
            filterToQuery={(searchText: string) => ({ keyword: searchText })}
          />
        </ReferenceInput>
        <NumberInput
          source="sortOrder"
          label="Thứ tự hiển thị"
          variant="outlined"
          min={0}
        />
        <SelectInput
          source="position"
          label="Vị trí hiển thị"
          choices={[
            { id: "MAINMENU", name: "MAINMENU" },
            { id: "FOOTERMENU", name: "FOOTERMENU" },
          ]}
          variant="outlined"
        />
        <BooleanInput source="status" label="Trạng thái" />
      </SimpleForm>
    </Edit>
  );
};
export default MenuEdit;
