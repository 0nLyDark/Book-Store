import {
  Page,
  Text,
  View,
  Document,
  StyleSheet,
  Font,
} from "@react-pdf/renderer";
import Roboto from "../font/Roboto-Regular.ttf";

Font.register({ family: "Roboto", src: Roboto });
function formatNumber(number: { toLocaleString: (arg0: string) => any }) {
  const formattedNumber = number.toLocaleString("vi-VN"); // Định dạng theo tiếng Việt
  return formattedNumber.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}
function formatDate(date: { split: (arg0: string) => [any, any, any] }) {
  const [year, month, day] = date.split("-");
  return `${day}-${month}-${year}`;
}
// Styles
// Định nghĩa styles
const styles = StyleSheet.create({
  page: {
    padding: 40,
    fontSize: 12,
    fontFamily: "Roboto",
  },
  title: {
    fontSize: 20,
    marginBottom: 20,
    textAlign: "center",
    fontWeight: "bold",
  },
  infoSection: {
    marginBottom: 20,
  },
  infoText: {
    marginBottom: 5,
  },
  table: {
    width: "auto",
    borderStyle: "solid",
    borderWidth: 1,
    borderRightWidth: 0,
    borderBottomWidth: 0,
    marginBottom: 20,
  },
  tableRow: {
    flexDirection: "row",
  },
  tableColHeader: {
    width: "25%",
    borderStyle: "solid",
    borderBottomWidth: 1,
    borderRightWidth: 1,
    backgroundColor: "#f0f0f0",
    padding: 5,
    fontWeight: "bold",
  },
  tableCol: {
    width: "25%",
    borderStyle: "solid",
    borderBottomWidth: 1,
    borderRightWidth: 1,
    padding: 5,
  },
  tableColCenter: {
    textAlign: "center",
  },
  tableColRight: {
    textAlign: "right",
  },
  totalSection: {
    textAlign: "right",
    marginTop: 10,
  },
  totalText: {
    fontSize: 14,
    fontWeight: "bold",
  },
});

// Component ReceiptPDF
const ReceiptPDF = ({ data }: { data: any }) => {
  return (
    <Document>
      <Page size="A4" style={styles.page}>
        {/* Header */}
        <Text style={styles.title}>Biên lai nhập sách</Text>

        {/* Info */}
        <View style={styles.infoSection}>
          <Text style={styles.infoText}>Mã đơn: {data.importReceiptId}</Text>
          <Text style={styles.infoText}>
            Nhà cung cấp: {data.supplier.supplierName}
          </Text>
          <Text style={styles.infoText}>
            Ngày nhập: {new Date(data.importDate).toLocaleDateString("vi-VN")}
          </Text>
        </View>

        {/* Table */}
        <View style={styles.table}>
          {/* Header */}
          <View style={styles.tableRow}>
            <Text style={styles.tableColHeader}>Mã sách</Text>
            <Text style={styles.tableColHeader}>Tên sách</Text>
            <Text style={[styles.tableColHeader, styles.tableColCenter]}>
              Số lượng
            </Text>
            <Text style={[styles.tableColHeader, styles.tableColCenter]}>
              Đơn giá (VNĐ)
            </Text>
            <Text style={[styles.tableColHeader, styles.tableColCenter]}>
              Thành tiền (VNĐ)
            </Text>
          </View>
          {/* Rows */}
          {data.importReceiptItems.map((ir: any, index: number) => (
            <View style={styles.tableRow} key={index}>
              <Text style={styles.tableCol}>{ir.product.isbn}</Text>
              <Text style={styles.tableCol}>{ir.product.productName}</Text>
              <Text style={[styles.tableCol, styles.tableColCenter]}>
                {ir.quantity}
              </Text>
              <Text style={[styles.tableCol, styles.tableColRight]}>
                {ir.price.toLocaleString("vi-VN")} đ
              </Text>
              <Text style={[styles.tableCol, styles.tableColRight]}>
                {ir.totalPrice.toLocaleString("vi-VN")} đ
              </Text>
            </View>
          ))}
        </View>
        {/* Footer */}
        <View style={styles.totalSection}>
          <Text style={styles.totalText}>
            Tổng tiền: {data.totalAmount.toLocaleString("vi-VN")} VNĐ
          </Text>
        </View>
      </Page>
    </Document>
  );
};

export default ReceiptPDF;
