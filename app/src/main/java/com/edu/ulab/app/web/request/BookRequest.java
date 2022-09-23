package com.edu.ulab.app.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class BookRequest {

    @Schema(description = "Title of book", example = "Dead souls")
    @Size(min = 3, max = 255, message = "Title should be between {min} and {max}")
    @NotBlank(message = "Title should not be null")
    private String title;

    @Schema(description = "Name of author", example = "Nikolai Gogol")
    @Size(min = 3, max = 255, message = "Name of author should be between {min} and {max}")
    @NotBlank(message = "Author name should not be null")
    private String author;

    @Min(value = 1, message = "The number of pages should be greater or equals than {value}")
    private long pageCount;
}
