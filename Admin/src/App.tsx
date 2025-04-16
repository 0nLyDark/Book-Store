import { Admin, Menu, Resource } from "react-admin";
import { Layout } from "./Layout";
import authProvider from "./authProvider";
import { Dashboard } from "./Dashboard";
import CategoryList from "./resources/Categories/CategoryList";
import dataProvider from "./dataProvider";
import CategoryEdit from "./resources/Categories/CategoryEdit";
import CategoryCreate from "./resources/Categories/CategoryCreate";
import TopicList from "./resources/Topics/TopicList";
import TopicCreate from "./resources/Topics/TopicCreate";
import TopicEdit from "./resources/Topics/TopicEdit";
import CategoryShow from "./resources/Categories/CategoryShow";
import TopicShow from "./resources/Topics/TopicShow";
import LanguageList from "./resources/Languages/LanguageList";
import LanguageCreate from "./resources/Languages/LanguageCreate";
import LanguageEdit from "./resources/Languages/LanguageEdit";
import LanguageShow from "./resources/Languages/LanguageShow";
import PublisherList from "./resources/Publishers/PublisherList";
import PublisherCreate from "./resources/Publishers/PublisherCreate";
import PublisherEdit from "./resources/Publishers/PublisherEdit";
import PublisherShow from "./resources/Publishers/PublisherShow";
import SupplierList from "./resources/Suppliers/SupplierList";
import SupplierCreate from "./resources/Suppliers/SupplierCreate";
import SupplierEdit from "./resources/Suppliers/SupplierEdit";
import SupplierShow from "./resources/Suppliers/SupplierShow";
import PostList from "./resources/Posts/PostList";
import PostCreate from "./resources/Posts/PostCreate";
import PostEdit from "./resources/Posts/PostEdit";
import PostShow from "./resources/Posts/PostShow";
import AuthorList from "./resources/Authors/AuthorList";
import AuthorCreate from "./resources/Authors/AuthorCreate";
import AuthorEdit from "./resources/Authors/AuthorEdit";
import AuthorShow from "./resources/Authors/AuthorShow";
import BannerList from "./resources/Banners/BannerList";
import BannerCreate from "./resources/Banners/BannerCreate";
import BannerEdit from "./resources/Banners/BannerEdit";
import BannerShow from "./resources/Banners/BannerShow";
import ContactList from "./resources/Contacts/ContactList";
import ContactEdit from "./resources/Contacts/ContactEdit";
import ProductList from "./resources/Product/ProductList";
import ProductCreate from "./resources/Product/ProductCreate";
import ProductEdit from "./resources/Product/ProductEdit";
import ProductShow from "./resources/Product/ProducShow";
import CartList from "./resources/Cart/CartList";
import CartShow from "./resources/Cart/CartShow";
import ImportReceiptList from "./resources/ImportReceipt/ImportReceiptList";
import ImportReceiptShow from "./resources/ImportReceipt/ImportReceiptShow";
import ImportReceiptCreate from "./resources/ImportReceipt/ImportReceiptCreate";
import MenuShow from "./resources/Menu/MenuShow";
import MenuEdit from "./resources/Menu/MenuEdit";
import MenuCreate from "./resources/Menu/MenuCreate";
import MenuList from "./resources/Menu/MenuList";

export const App = () => (
  <Admin
    authProvider={authProvider}
    layout={Layout}
    dataProvider={dataProvider}
    dashboard={Dashboard}
  >
    <Resource
      name="categories"
      list={CategoryList}
      create={CategoryCreate}
      edit={CategoryEdit}
      show={CategoryShow}
      options={{ label: "Danh mục" }}
    />
    <Resource
      name="languages"
      list={LanguageList}
      create={LanguageCreate}
      edit={LanguageEdit}
      show={LanguageShow}
      options={{ label: "Ngôn ngữ" }}
    />
    <Resource
      name="authors"
      list={AuthorList}
      create={AuthorCreate}
      edit={AuthorEdit}
      show={AuthorShow}
      options={{ label: "Tác giả" }}
    />
    <Resource
      name="publishers"
      list={PublisherList}
      create={PublisherCreate}
      edit={PublisherEdit}
      show={PublisherShow}
      options={{ label: "Nhà sản xuất" }}
    />
    <Resource
      name="suppliers"
      list={SupplierList}
      create={SupplierCreate}
      edit={SupplierEdit}
      show={SupplierShow}
      options={{ label: "Nhà cung cấp" }}
    />
    <Resource
      name="products"
      list={ProductList}
      create={ProductCreate}
      edit={ProductEdit}
      show={ProductShow}
      options={{ label: "Sản phẩm" }}
    />
    <Resource
      name="carts"
      list={CartList}
      show={CartShow}
      options={{ label: "Giỏ hàng" }}
    />
    <Resource
      name="import-receipts"
      list={ImportReceiptList}
      create={ImportReceiptCreate}
      show={ImportReceiptShow}
      options={{ label: "Nhập hàng" }}
    />
    <Resource
      name="menus"
      list={MenuList}
      create={MenuCreate}
      edit={MenuEdit}
      show={MenuShow}
      options={{ label: "Menu" }}
    />
    <Resource
      name="topics"
      list={TopicList}
      create={TopicCreate}
      edit={TopicEdit}
      show={TopicShow}
      options={{ label: "Chủ đề" }}
    />
    <Resource
      name="posts"
      list={PostList}
      create={PostCreate}
      edit={PostEdit}
      show={PostShow}
      options={{ label: "Bài viết" }}
    />
    <Resource
      name="banners"
      list={BannerList}
      create={BannerCreate}
      edit={BannerEdit}
      show={BannerShow}
      options={{ label: "Banner" }}
    />
    <Resource
      name="contacts"
      list={ContactList}
      edit={ContactEdit}
      options={{ label: "Liên hệ" }}
    />
  </Admin>
);
