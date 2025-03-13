package com.example.webcvtemplate.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpsertBlogRequest {
    String title;
    String description;
    String content;
    Boolean status;
    String thumbnail;
    private List<String> blogRoleCodes;
}
