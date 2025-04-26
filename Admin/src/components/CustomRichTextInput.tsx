import React, { useEffect } from "react";
import { useInput } from "react-admin";
import { Editor } from "@tinymce/tinymce-react";

// Đảm bảo TinyMCE được tải từ local
export const LoadTinyMCE = () => {
  useEffect(() => {
    // Thêm script TinyMCE từ local
    const script = document.createElement("script");
    script.src = "/tinymce/tinymce.min.js"; // Đường dẫn đến tinymce.min.js trong thư mục public
    script.referrerPolicy = "origin";
    script.async = true;
    document.body.appendChild(script);
  }, []);

  return null; // Không cần render gì
};

interface CustomRichTextInputProps {
  source: string;
  label: string;
}

const CustomRichTextInput = ({ source, label }: CustomRichTextInputProps) => {
  const {
    field: { value, onChange, name },
    fieldState: { error },
    isRequired,
  } = useInput({ source });

  return (
    <div>
      {/* Nạp TinyMCE từ local */}
      <LoadTinyMCE />

      <label htmlFor={name}>
        {label} {isRequired && "*"}
      </label>
      <Editor
        tinymceScriptSrc="/tinymce/tinymce.min.js"
        id={name}
        value={value}
        onEditorChange={(content: any) => onChange(content)}
        init={{
          height: 300,
          width: "100%",
          menubar: false,
          plugins: "link image code lists",
          toolbar:
            "undo redo | formatselect | bold italic | alignleft aligncenter alignright | bullist numlist | link image | code",
          content_css: "/tinymce/skins/content/default/content.min.css",
          skin_url: "/tinymce/skins/ui/oxide",
          base_url: "/tinymce", // Đường dẫn thư mục chứa tinymce.min.js
          suffix: ".min",
          branding: false,
        }}
      />
      {error && <span style={{ color: "red" }}>{error.message}</span>}
    </div>
  );
};

export default CustomRichTextInput;
