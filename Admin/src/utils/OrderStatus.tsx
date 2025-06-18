const statusMap: Record<string, string> = {
  COMPLETED: "Đã hoàn thành",
  SHIPPED: "Đang giao",
  PENDING: "Đang chờ xử lý",
  CANCELLED: "Đã hủy",
  PAID: "Đã xác nhận",
  FAILED: "Thất bại",
};

export const formatOrderStatus = (status: string): string => {
  return statusMap[status] || "Không xác định";
};
