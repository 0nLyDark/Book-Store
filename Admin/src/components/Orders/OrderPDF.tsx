import {
  Page,
  Text,
  View,
  Document,
  StyleSheet,
  Font,
} from "@react-pdf/renderer";
import Roboto from "../font/Roboto-Regular.ttf";

// Đăng ký font
Font.register({ family: "Roboto", src: Roboto });

// Format số
function formatNumber(number: number) {
  return number.toLocaleString("vi-VN") + " đ";
}

// Format ngày
function formatDate(date: string) {
  const d = new Date(date);
  return d.toLocaleDateString("vi-VN");
}

// Styles
const styles = StyleSheet.create({
  page: {
    padding: 40,
    fontSize: 12,
    fontFamily: "Roboto",
  },
  titleh: {
    fontSize: 24,
    marginBottom: 10,
    textAlign: "center",
    fontWeight: "bold",
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
    width: "20%",
    borderStyle: "solid",
    borderBottomWidth: 1,
    borderRightWidth: 1,
    backgroundColor: "#f0f0f0",
    padding: 5,
    fontWeight: "bold",
  },
  tableCol: {
    width: "20%",
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
    alignSelf: "flex-end",
    width: "45%",
    padding: 10,
    marginTop: 5,
    borderTopWidth: 1,
    borderTopColor: "#E0E0E0",
  },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 5,
  },
  totalText: {
    fontSize: 16,
    color: "#333",
  },
  totalLabel: {
    fontWeight: "bold",
    fontSize: 16,
    flex: 1,
  },
});

// Component hóa đơn mua hàng
const OrderPDF = ({ data }: { data: any }) => {
  // const calculateTotal = () =>
  //   data.orderItems.reduce(
  //     (sum: number, item: any) => sum + item.price * item.quantity,
  //     0,
  //   );

  // const totalAmount = calculateTotal();

  return (
    <Document>
      <Page size="A4" style={styles.page}>
        {/* Header */}
        <Text style={styles.titleh}>BOOK STORE</Text>
        <Text style={styles.title}>Hóa đơn mua hàng</Text>

        {/* Thông tin đơn hàng */}
        <View style={styles.infoSection}>
          <Text style={styles.infoText}>Mã đơn hàng: {data.orderId}</Text>
          {data.email && (
            <Text style={styles.infoText}>Email: {data.email ?? ""}</Text>
          )}
          {data.deliveryName && (
            <Text style={styles.infoText}>
              Người nhận: {data.deliveryName ?? ""}
            </Text>
          )}
          {data.deliveryPhone && (
            <Text style={styles.infoText}>
              Số điện thoại: {data.deliveryPhone}
            </Text>
          )}
          <Text style={styles.infoText}>
            Phương thức thanh toán: {data.payment.paymentMethod}
          </Text>
          <Text style={styles.infoText}>
            Ngày {data.OrderType == "COD" ? "Mua hàng" : "Đặt hàng"}:
            {formatDate(data.orderDateTime)}
          </Text>
          {data.adddress && (
            <Text style={styles.infoText}>
              Địa chỉ giao hàng:
              {`${data.address.buildingName}, ${data.address.ward}, ${data.address.district}, ${data.address.city}`}
              {/* , ${data.address.country}`} */}
            </Text>
          )}
        </View>

        {/* Bảng chi tiết sản phẩm */}
        <View style={styles.table}>
          <View style={styles.tableRow}>
            <Text style={styles.tableColHeader}>Mã sách</Text>
            <Text style={styles.tableColHeader}>Tên sách</Text>
            <Text style={[styles.tableColHeader, styles.tableColCenter]}>
              Số lượng
            </Text>
            <Text style={[styles.tableColHeader, styles.tableColRight]}>
              Đơn giá
            </Text>
            <Text style={[styles.tableColHeader, styles.tableColRight]}>
              Thành tiền
            </Text>
          </View>

          {data.orderItems.map((item: any, index: number) => {
            const total =
              (item.quantity * item.price * (100 - item.discount)) / 100;
            return (
              <View style={styles.tableRow} key={index}>
                <Text style={styles.tableCol}>{item.product.isbn}</Text>
                <Text style={styles.tableCol}>{item.product.productName}</Text>
                <Text style={[styles.tableCol, styles.tableColCenter]}>
                  {item.quantity}
                </Text>
                <Text style={[styles.tableCol, styles.tableColRight]}>
                  {formatNumber((item.price * (100 - item.discount)) / 100)}
                </Text>
                <Text style={[styles.tableCol, styles.tableColRight]}>
                  {formatNumber(total)}
                </Text>
              </View>
            );
          })}
        </View>

        {/* Tổng tiền */}
        <View style={styles.totalSection}>
          <View style={styles.row}>
            <Text style={[styles.totalText, styles.totalLabel]}>Tổng giá:</Text>
            <Text style={styles.totalText}>{formatNumber(data.subTotal)}</Text>
          </View>

          {data.coupon && (
            <View style={styles.row}>
              <Text style={[styles.totalText, styles.totalLabel]}>
                Giảm giá:
              </Text>
              <Text style={styles.totalText}>
                {formatNumber(
                  data.coupon.valueType
                    ? (data.coupon.value * data.subTotal) / 100
                    : data.coupon.value,
                )}
              </Text>
            </View>
          )}
          <View style={styles.row}>
            <Text style={[styles.totalText, styles.totalLabel]}>
              Phí vận chuyển:
            </Text>
            <Text style={styles.totalText}>
              {formatNumber(
                data.priceShip -
                  (data.freeship.valueType
                    ? (data.freeship.value * data.priceShip) / 100
                    : data.freeship.value),
              )}
            </Text>
          </View>
          <View style={styles.row}>
            <Text style={[styles.totalText, styles.totalLabel]}>
              Tổng tiền:
            </Text>
            <Text style={styles.totalText}>
              {formatNumber(data.totalAmount)}
            </Text>
          </View>
        </View>
      </Page>
    </Document>
  );
};

export default OrderPDF;
