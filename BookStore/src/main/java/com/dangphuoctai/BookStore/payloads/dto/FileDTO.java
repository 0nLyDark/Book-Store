package com.dangphuoctai.BookStore.payloads.dto;

import com.dangphuoctai.BookStore.enums.FileType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {
    private Long fileId;
    private String fileName;
    private FileType type;

}
