import { useEffect } from "react";
import {
  AutocompleteInput,
  Create,
  NumberInput,
  ReferenceInput,
  required,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";
import { useFormContext, useWatch } from "react-hook-form";

const MenuForm = () => {
  const { resetField } = useFormContext();
  const type = useWatch({ name: "type" });
  useEffect(() => {
    resetField("refId");
  }, [type, resetField]);
  return (
    <>
      <SelectInput
        source="type"
        label="Kiểu menu"
        choices={[
          { id: "CUSTOMER", name: "Tùy chỉnh" },
          { id: "CATEGORY", name: "Danh mục" },
          { id: "AUTHOR", name: "Tác giả" },
          { id: "TOPIC", name: "Chủ đề bài viết" },
          { id: "PAGE", name: "Trang đơn" },
        ]}
        variant="outlined"
        validate={required()}
      />
      {type === "CUSTOMER" && (
        <>
          <TextInput
            source="name"
            label="Tên menu"
            validate={required()}
            variant="outlined"
          />
          <TextInput
            source="link"
            label="Đường dẫn liên kết"
            variant="outlined"
          />
        </>
      )}
      {type === "CATEGORY" && (
        <ReferenceInput
          key={type}
          source="refId"
          reference="categories"
          label="Chọn danh mục"
          filter={{ status: true }}
        >
          <SelectInput
            label="Chọn danh mục"
            optionText="categoryName"
            optionValue="categoryId"
            variant="outlined"
            validate={required()}
          />
        </ReferenceInput>
      )}
      {type === "AUTHOR" && (
        <ReferenceInput
          key={type}
          source="refId"
          reference="authors"
          label="Chọn tác giả"
          filter={{ status: true }}
        >
          <SelectInput
            label="Chọn tác giả"
            optionText="authorName"
            optionValue="authorId"
            variant="outlined"
            validate={required()}
          />
        </ReferenceInput>
      )}
      {type === "TOPIC" && (
        <ReferenceInput
          key={type}
          source="refId"
          reference="topics"
          label="Chọn chủ đề bài viết"
          filter={{ status: true }}
        >
          <SelectInput
            label="Chọn chủ đề bài viết"
            optionText="topicName"
            optionValue="topicId"
            variant="outlined"
            validate={required()}
          />
        </ReferenceInput>
      )}
      {type === "PAGE" && (
        <ReferenceInput
          key={type}
          source="refId"
          reference="posts"
          label="Chọn trang đơn"
          filter={{ status: true, type: "PAGE" }}
        >
          <SelectInput
            label="Chọn trang đơn"
            optionText="title"
            optionValue="postId"
            variant="outlined"
            validate={required()}
          />
        </ReferenceInput>
      )}
      <ReferenceInput source="parent.menuId" reference="menus" label="Menu cha">
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
        validate={required()}
        min={0}
      />
      <SelectInput
        source="position"
        label="Vị trí hiển thị"
        validate={required()}
        choices={[
          { id: "MAINMENU", name: "MAINMENU" },
          { id: "FOOTERMENU", name: "FOOTERMENU" },
        ]}
        variant="outlined"
      />
    </>
  );
};

const MenuCreate = () => {
  useEffect(() => {
    document.title = "Tạo mới menu";
  }, []);

  return (
    <Create mutationMode="pessimistic">
      <SimpleForm>
        <MenuForm />
      </SimpleForm>
    </Create>
  );
};

export default MenuCreate;
