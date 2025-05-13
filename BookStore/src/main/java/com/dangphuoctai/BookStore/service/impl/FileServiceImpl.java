package com.dangphuoctai.BookStore.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.service.FileService;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        String originalFileName = file.getOriginalFilename();
        String randomId = UUID.randomUUID().toString();
        @SuppressWarnings("null")
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;

        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }

        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;

    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        String filePath = path + File.separator + fileName;

        InputStream inputStream = new FileInputStream(filePath);

        return inputStream;

    }

    @Override
    public String downloadImageFromUrl(String imageUrl, String folderPath) {
        try {
            // Mở kết nối và lấy Content-Type
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            String contentType = connection.getContentType(); // ví dụ: "image/jpeg"

            // Lấy phần mở rộng từ Content-Type, mặc định ".jpg"
            String extension = ".jpg"; // Fallback nếu không có phần mở rộng
            if (contentType != null && contentType.startsWith("image/")) {
                extension = "." + contentType.substring("image/".length()); // ví dụ: ".jpeg"
            }

            // Tạo tên file ngẫu nhiên với phần mở rộng
            String fileName = UUID.randomUUID() + extension;
            Path targetPath = Paths.get(folderPath).resolve(fileName);

            // Tạo thư mục nếu chưa tồn tại
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs(); // Đảm bảo thư mục tồn tại
            }

            // Tải ảnh và lưu vào thư mục
            try (InputStream in = connection.getInputStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return fileName;
        } catch (IOException e) {
            throw new APIException("Lỗi khi tải ảnh từ URL: " + imageUrl + " detail: " + e.getMessage());
        } catch (Exception e) {
            throw new APIException("Đã xảy ra lỗi không mong muốn: " + e.getMessage());
        }
    }

}