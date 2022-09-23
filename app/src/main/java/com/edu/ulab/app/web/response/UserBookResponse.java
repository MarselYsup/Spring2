package com.edu.ulab.app.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserBookResponse {
    private Long userId;

    @Schema(description = "Full name of User", example = "Ivanov Ivan Ivanovich")
    private String fullName;

    @Schema(description = "Title of User", example = "Reader")
    private String title;

    @Schema(description = "Age of User", example = "22")
    private Integer age;

    private List<Long> booksIdList;
}
