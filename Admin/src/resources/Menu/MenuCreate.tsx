import {
  Create,
  NumberInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";

const MenuCreate = () => (
  <Create>
    <SimpleForm>
      <TextInput source="name" label="Tên menu" />
      <TextInput source="link" label="Đường dẫn liên kết" />
      <ReferenceInput source="parent.menuId" reference="menus">
        <SelectInput optionText="name" variant="outlined" label="Menu cha" />
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
    </SimpleForm>
  </Create>
);

export default MenuCreate;
