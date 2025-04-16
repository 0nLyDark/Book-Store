import {
  BooleanInput,
  Edit,
  NumberInput,
  SelectInput,
  SimpleForm,
  TextInput,
  useGetList,
} from "react-admin";
import { useWatch } from "react-hook-form";

const ParentMenuInput = () => {
  const currentId = useWatch({ name: "menuId" });

  const { data: menus, isLoading } = useGetList("menus");

  const filteredChoices = menus
    ? menus.filter((menu) => menu.menuId !== currentId)
    : [];

  return (
    <SelectInput
      source="parent.menuId"
      label="Menu cha"
      choices={filteredChoices}
      optionText="name"
      optionValue="menuId"
      isLoading={isLoading}
    />
  );
};
const MenuEdit = () => (
  <Edit>
    <SimpleForm>
      <TextInput source="menuId" label="Menu ID" disabled />
      <TextInput source="name" label="Tên menu" />
      <TextInput source="link" label="Đường dẫn liên kết" />
      <ParentMenuInput />
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
export default MenuEdit;
