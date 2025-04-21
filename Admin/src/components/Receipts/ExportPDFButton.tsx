import { BlobProvider, pdf } from "@react-pdf/renderer";
import ReceiptPDF from "./ReceiptPDF";
import { HiOutlinePrinter } from "react-icons/hi";

const ExportPDFButton = ({ data }: { data: any }) => {
  const styles: { btn: React.CSSProperties; hover: React.CSSProperties } = {
    btn: {
      borderRadius: "3px",
      display: "flex",
      alignItems: "center",
      gap: "4px",
      padding: "6px 10px",
      fontSize: "12px",
      color: "#ffd700",
      fontWeight: 700,
      cursor: "pointer",
      userSelect: "none",
      backgroundColor: "#ffd70000",
      textDecoration: "none",
      transition: "background-color 0.3s, color 0.3s",
    },
    hover: {
      backgroundColor: "#ffd70010",
    },
  };
  const handleMouseEnter = (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.currentTarget.style.backgroundColor = styles.hover.backgroundColor ?? "";
    e.currentTarget.style.color = styles.hover.backgroundColor ?? "";
  };

  const handleMouseLeave = (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.currentTarget.style.backgroundColor = styles.btn.backgroundColor ?? "";
    e.currentTarget.style.color = styles.btn.color ?? "";
  };
  return (
    <BlobProvider document={<ReceiptPDF data={data} />}>
      {({ url, blob }) => (
        <a
          href={url ?? ""}
          target="_blank"
          style={styles.btn}
          onMouseEnter={handleMouseEnter}
          onMouseLeave={handleMouseLeave}
        >
          <HiOutlinePrinter size={17} />
          <span style={{ textDecoration: "none" }}>PRINT</span>
        </a>
      )}
    </BlobProvider>
  );
};

export default ExportPDFButton;
