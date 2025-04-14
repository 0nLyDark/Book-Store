import axios from "axios";
import {
  DataProvider,
  DeleteManyParams,
  DeleteParams,
  RaRecord,
} from "react-admin";

const API_URL = "http://localhost:8080/api";
export const API_IMAGE = `${API_URL}/public/file/`;

const httpClient = {
  get: (url: string) => {
    const token = localStorage.getItem("token");

    return axios
      .get(url, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        // withCredentials: true,
      })
      .then((response) => ({ json: response.data }))
      .catch((error) => {
        console.error("API request failed:", error);
        // throw error;
      });
  },

  post: (url: string, data: any) => {
    const token = localStorage.getItem("token");
    return axios
      .post(url, data, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        withCredentials: true,
      })
      .then((response) => ({ json: response.data }))
      .catch((error) => {
        console.error("API request failed:", error);
        throw error;
      });
  },

  put: (url: string, data: any) => {
    const token = localStorage.getItem("token");
    return axios
      .put(url, data, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        withCredentials: true,
      })
      .then((response) => ({ json: response.data }))
      .catch((error) => {
        console.error("API request failed:", error);
        throw error;
      });
  },

  delete: (url: string) => {
    const token = localStorage.getItem("token");

    return axios
      .delete(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
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
    if (resource === "contacts") {
      baseUrl = "staff";
    }
    if (resource === "carts") {
      baseUrl = "admin";
    }
    const idField = idFieldMapping[resource] || "id";

    const query = {
      pageNumber: page.toString(),
      pageSize: perPage.toString(),
      sortBy: field,
      sortOrder: order,
      type: "children",
      ...filter,
    };
    const queryString = new URLSearchParams(query).toString();
    const url = `${API_URL}/${baseUrl}/${resource}?${queryString}`;
    const response = await httpClient.get(url);
    if (!response) {
      throw new Error("Failed to fetch data from API");
    }
    console.log("response: ", url);
    console.log("response: ", queryString);
    console.log("response: ", response.json);
    const data = response.json.content.map(
      (item: { [x: string]: any; image: any }) => ({
        id: item[idField],
        ...item,
        image: item.image ? `${API_IMAGE}${item.image}` : null,
      }),
    );
    if (resource === "carts") {
      data.forEach((item: any) => {
        item.products = item.cartItems.map((item: any) => ({
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
      });
    }
    return {
      data: data,
      total: response.json.totalElements,
    };
  },

  getOne: async (resource, params) => {
    let baseUrl = "public";
    if (resource === "contacts") {
      baseUrl = "staff";
    }
    const url = `${API_URL}/${baseUrl}/${resource}/${params.id}`;
    const idField = idFieldMapping[resource] || "id";
    const response = await httpClient.get(url);
    if (!response) {
      throw new Error("Failed to fetch data from API");
    }
    const data = {
      id: response.json[idField],
      ...response.json,
      image: response.json.image ? API_IMAGE + response.json.image : null,
      images: response.json.images
        ? response.json.images.map((img: any) => API_IMAGE + img.fileName)
        : null,
    };
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
    const url = `${API_URL}/staff/${resource}`;
    const idField = idFieldMapping[resource] || "id";
    const { data } = params;
    console.log("params:", data);
    let payload: FormData | typeof data;
    payload = data;
    const formData = new FormData();
    if (
      resource === "authors" ||
      resource === "banners" ||
      resource === "products"
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
          formData.append(key, data[key]);
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
    const url = `${API_URL}/staff/${resource}`;
    const idField = idFieldMapping[resource] || "id";
    console.log("paramssssssssss:", params.data);

    const data = {
      ...params.data,
      [idField]: params?.data.id,
    };
    let payload: FormData | typeof data;
    payload = data;
    console.log("params:", data);
    const formData = new FormData();

    if (
      resource === "authors" ||
      resource === "banners" ||
      resource === "products"
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
      delete data.categories;
      delete data.authors;
      delete data.languages;
      delete data.image;
      delete data.images;

      for (const key in data) {
        if (data[key] !== undefined && data[key] !== null) {
          formData.append(key, data[key]);
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
