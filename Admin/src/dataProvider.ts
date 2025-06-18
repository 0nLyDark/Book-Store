import {
  DataProvider,
  DeleteManyParams,
  DeleteParams,
  RaRecord,
} from "react-admin";
import axiosInstance from "./api";

export const API_URL = import.meta.env.VITE_API_URL;
export const API_IMAGE = `${API_URL}/public/file/`;

export const httpClient = {
  get: (url: string) => {
    return axiosInstance
      .get(url, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      })
      .then((response) => ({ json: response.data }))
      .catch((error) => {
        console.error("API request failed:", error);
        throw error;
      });
  },

  post: (url: string, data: any) => {
    return axiosInstance
      .post(url, data, {
        withCredentials: true,
      })
      .then((response) => ({ json: response.data }))
      .catch((error) => {
        console.error("API request failed:", error);

        throw error;
      });
  },

  put: (url: string, data: any) => {
    return axiosInstance
      .put(url, data, {
        withCredentials: true,
      })
      .then((response) => ({ json: response.data }))
      .catch((error) => {
        console.error("API request failed:", error);
        throw error;
      });
  },

  delete: (url: string) => {
    return axiosInstance
      .delete(url, {
        withCredentials: true,
      })
      .then((response) => ({ data: response.data }))
      .catch((error) => {
        console.error("API request failed:", error);
        throw error;
      });
  },
};
const idFieldMapping: { [key: string]: string } = {
  products: "productId",
  categories: "categoryId",
  carts: "cartId",
  orders: "orderId",
  users: "userId",
  topics: "topicId",
  posts: "postId",
  languages: "languageId",
  banners: "bannerId",
  publishers: "publisherId",
  suppliers: "supplierId",
  authors: "authorId",
  contacts: "contactId",
  menus: "menuId",
  "import-receipts": "importReceiptId",
  promotions: "promotionId",
};
const dataProvider: DataProvider = {
  getList: async (
    resource,
    { pagination = {}, sort = {}, filter = {}, meta = {} },
  ) => {
    const { page = 0, perPage = 10 } = pagination;
    const { field = "id", order = "ASC" } = sort;
    console.log("resource: ", resource);
    let baseUrl = "public";
    if (
      resource === "contacts" ||
      resource === "import-receipts" ||
      resource === "orders" ||
      resource === "stocks"
    ) {
      baseUrl = "staff";
    } else if (resource === "users") {
      baseUrl = "admin";
    }

    const idField = idFieldMapping[resource] || "id";

    let query = {
      pageNumber: page.toString(),
      pageSize: perPage.toString(),
      sortBy: field,
      sortOrder: order,
      ...filter,
    };
    if (resource === "categories") {
      query.type = "children";
    }
    if (resource === "menus") {
      query = {
        ...query,
        type: "children",
      };
    }

    const queryString = new URLSearchParams(query).toString();
    const url = `${API_URL}/${baseUrl}/${resource}?${queryString}`;
    const response = await httpClient.get(url);
    if (!response) {
      throw new Error("Failed to fetch data from API");
    }
    console.log("data: ", response.json.content);

    // console.log("response: ", url);
    // console.log("response: ", queryString);
    // console.log("response: ", response.json);
    const data = response.json.content.map(
      (item: {
        [x: string]: any;
        image?: string;
        images?: { fileName: string }[];
        avatar?: string;
      }) => ({
        id: item[idField],
        ...item,
        avatar: item.avatar ? API_IMAGE + item.avatar : null,
        image: item.image ? API_IMAGE + item.image : null,
        images: item.images
          ? item.images.map(
              (img: { fileName: string }) => API_IMAGE + img.fileName,
            )
          : [],
      }),
    );

    console.log("data: ", data);
    return {
      data: data,
      total: response.json.totalElements,
      lastPage: response.json.lastPage,
    };
  },

  getOne: async (resource, params) => {
    let baseUrl = "public";
    const supUrl = resource === "carts" ? "/user" : "";
    if (resource === "contacts" || resource === "import-receipts") {
      baseUrl = "staff";
    }

    const url = `${API_URL}/${baseUrl}/${resource}${supUrl}/${params.id}`;
    const idField = idFieldMapping[resource] || "id";
    const response = await httpClient.get(url);
    if (!response) {
      throw new Error("Failed to fetch data from API");
    }
    const data = {
      id: response.json[idField],
      ...response.json,
      avatar: response.json.avatar ? API_IMAGE + response.json.avatar : null,
      image: response.json.image ? API_IMAGE + response.json.image : null,
      images: response.json.images
        ? response.json.images.map((img: any) => {
            img.fileName = API_IMAGE + img.fileName;
            return img;
          })
        : null,
    };
    if (resource === "orders") {
      data.orderItems = response.json.orderItems.map((item: any) => ({
        ...item,
        product: {
          ...item.product,
          images: item.product.images
            ? item.product.images.map(
                (img: any) => `${API_IMAGE}${img.fileName}`,
              )
            : null,
        },
      }));
    }
    if (resource === "carts") {
      data.cartItems = response.json.cartItems.map((item: any) => ({
        ...item,
        product: {
          ...item.product,
          images: item.product.images
            ? item.product.images.map(
                (img: any) => `${API_IMAGE}${img.fileName}`,
              )
            : null,
        },
      }));
    }
    if (resource === "import-receipts") {
      data.importReceiptItems = response.json.importReceiptItems.map(
        (item: any) => ({
          ...item,
          product: {
            ...item.product,
            images: item.product.images
              ? item.product.images.map(
                  (img: any) => `${API_IMAGE}${img.fileName}`,
                )
              : null,
          },
        }),
      );
    }
    console.log("response data: ", data);
    return { data };
  },

  getMany: async (resource, params) => {
    let baseUrl = "public";
    if (resource === "contacts") {
      baseUrl = "staff";
    }
    const query = params.ids.map((id) => `id=${id}`).join("&");
    const url = `${API_URL}/${baseUrl}/${resource}/ids?${query}`;
    const response = await httpClient.get(url);

    if (!response) {
      throw new Error("Failed to fetch data from API");
    }
    const idField = idFieldMapping[resource] || "id";

    const data = response.json.map((item: any) => ({
      id: response.json[idField],
      image: API_IMAGE + response.json.image,
      ...item,
    }));

    return { data };
  },

  getManyReference: async (resource, params) => {
    const { page, perPage } = params.pagination;
    const { field, order } = params.sort;

    const query = `?${params.target}=${params.id}&page=${page - 1}&size=${perPage}&sort=${field},${order}`;
    const response = await httpClient.get(`/${resource}${query}`);

    return {
      data: response.json.content,
      total: response.json.totalElements,
    };
  },

  create: async (resource, params) => {
    let baseUrl = "staff";
    if (resource === "menus" || resource === "promotions") {
      baseUrl = "admin";
    }
    const url = `${API_URL}/${baseUrl}/${resource}`;
    const idField = idFieldMapping[resource] || "id";
    const { data } = params;
    console.log("params:", data);
    let payload: FormData | typeof data;
    payload = data;
    const formData = new FormData();
    if (
      resource === "authors" ||
      resource === "banners" ||
      resource === "products" ||
      resource === "categories" ||
      resource === "publishers" ||
      resource === "posts"
    ) {
      if (data.images && Array.isArray(data.images)) {
        data.images.forEach((img: any) => {
          if (img.rawFile) {
            formData.append("files", img.rawFile);
          }
        });
      } else if (data.image?.rawFile) {
        formData.append("file", data.image.rawFile);
      }
      if (resource === "products") {
        formData.append("publisherId", data.publisher.publisherId);
        formData.append("supplierId", data.supplier.supplierId);
      }

      delete data.publisher;
      delete data.supplier;
      delete data.image;
      delete data.images;

      for (const key in data) {
        if (data[key] !== undefined && data[key] !== null) {
          formData.append(
            key,
            typeof data[key] === "object" && !Array.isArray(data[key])
              ? JSON.stringify(data[key])
              : data[key],
          );
          console.log("key:", key);
          console.log("value:", data[key]);
        }
      }
      payload = formData;
    }
    console.log("payload:", payload);

    const response = await httpClient.post(url, payload);
    return {
      data: {
        id: response.json[idField],
        image: response.json.image ? API_IMAGE + response.json.image : null,
        images: response.json.images
          ? response.json.images.map((img: any) => API_IMAGE + img.fileName)
          : null,
        ...response.json,
      },
    };
  },

  update: async (resource, params) => {
    let baseUrl = "staff";
    if (resource === "menus" || resource === "promotions") {
      baseUrl = "admin";
    }
    const url = `${API_URL}/${baseUrl}/${resource}`;
    const idField = idFieldMapping[resource] || "id";

    const data = {
      ...params.data,
      [idField]: params.data.id,
    };
    let payload: FormData | typeof data;
    payload = data;
    console.log("params:", data);
    const formData = new FormData();

    if (
      resource === "authors" ||
      resource === "banners" ||
      resource === "products" ||
      resource === "categories" ||
      resource === "publishers" ||
      resource === "posts"
    ) {
      if (data.images && Array.isArray(data.images)) {
        data.images.forEach((img: any) => {
          if (img.rawFile) {
            formData.append("files", img.rawFile);
          } else if (img.fileId) {
            formData.append("oldImages", img.fileId);
          }
          console.log("img:", img);
        });
      } else if (data.image?.rawFile) {
        formData.append("file", data.image.rawFile);
      }
      if (resource === "products") {
        formData.append("publisherId", data.publisher.publisherId);
        formData.append("supplierId", data.supplier.supplierId);
      }

      delete data.publisher;
      delete data.supplier;
      delete data.categories;
      delete data.authors;
      delete data.languages;
      delete data.image;
      delete data.images;

      for (const key in data) {
        if (data[key] !== undefined && data[key] !== null) {
          formData.append(
            key,
            typeof data[key] === "object" && !Array.isArray(data[key])
              ? JSON.stringify(data[key])
              : data[key],
          );
          console.log("key:", key);
          console.log("value:", data[key]);
        }
      }
      payload = formData;
    }
    console.log("payload:", payload);
    const response = await httpClient.put(url, payload);

    return {
      data: {
        id: response.json[idField],
        ...response.json,
        image: response.json.image ? API_IMAGE + response.json.image : null,
        images: response.json.images
          ? response.json.images.map((img: any) => API_IMAGE + img.fileName)
          : null,
      },
    };
  },

  delete: async <RecordType extends RaRecord = any>(
    resource: string,
    params: DeleteParams,
  ) => {
    const url = `${API_URL}/admin/${resource}/${params.id}`;

    const response = await httpClient.delete(url);
    if (!response) {
      throw new Error("Failed to delete data from API");
    }
    return { data: { id: params.id } as RecordType };
  },

  deleteMany: async (resource: string, params: DeleteManyParams) => {
    const ids = params.ids;
    const results = await Promise.all(
      ids.map((id) => {
        const url = `${API_URL}/admin/${resource}/${id}`;
        return httpClient.delete(url);
      }),
    );
    return { data: ids };
  },

  updateMany: async (resource, params) => {
    const results = await Promise.all(
      params.ids.map((id) => httpClient.put(`/${resource}/${id}`, params.data)),
    );
    return { data: results.map((res) => res.json.id || null) };
  },
};

export default dataProvider;
