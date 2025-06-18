// utils/parseExcel.ts
import * as XLSX from "xlsx";

export const parseExcelFile = async (file: File) => {
  const data = await file.arrayBuffer();
  const workbook = XLSX.read(data);
  const sheetName = workbook.SheetNames[0];
  const worksheet = workbook.Sheets[sheetName];
  const jsonData = XLSX.utils.sheet_to_json<any>(worksheet);

  const products = jsonData.map((item, index) => ({
    productName: item.productName || "",
    isbn: item.isbn || "",
    size: item.size || "",
    format: item.format || "",
    weight: Number(item.weight || 0),
    year: Number(item.year || 0),
    quantity: Number(item.quantity || 0),
    price: Number(item.price || 0),
    discount: Number(item.discount || 0),
    pageNumber: Number(item.pageNumber || 0),
    description: item.description || "",
    status: item.status?.toString().toLowerCase() === "true", // chuyển từ chuỗi sang boolean
    categoryIds: parseIdList(item.categoryIds),
    authorIds: parseIdList(item.authorIds),
    languageIds: parseIdList(item.languageIds),
    supplierId: Number(item.supplierId || 0),
    publisherId: Number(item.publisherId || 0),
    files: [], // sẽ upload sau trong giao diện
  }));

  return products;
};

const parseIdList = (str: string) => {
  if (!str) return [];
  return str
    .split(",")
    .map((s) => s.trim())
    .filter(Boolean)
    .map(Number);
};

export default parseExcelFile;
