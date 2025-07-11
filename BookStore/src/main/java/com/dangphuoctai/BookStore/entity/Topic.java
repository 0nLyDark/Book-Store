package com.dangphuoctai.BookStore.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "topics")
@AllArgsConstructor
@NoArgsConstructor
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicId;

    @NotNull
    @Column(nullable = false)
    @Size(min = 3, message = "Tên chủ đề phải có ít nhất 3 ký tự")
    private String topicName;

    @Column(nullable = false, unique = true)
    private String slug;

    private String description;

    @OneToMany(mappedBy = "topic", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private List<Post> posts = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean status;

    @Column(nullable = false)
    private Long createdBy;
    @Column(nullable = false)
    private Long updatedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
