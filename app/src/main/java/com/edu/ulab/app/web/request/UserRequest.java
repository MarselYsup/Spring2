package com.edu.ulab.app.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserRequest {
    @Schema(description = "Full name of User", example = "Ivanov Ivan Ivanovich")
    @Size(min = 5, max = 255, message = "Full name should be between {min} and {max}")
    @NotBlank(message = "Full name should not be null")
    private String fullName;

    @Schema(description = "Title of User", example = "Reader")
    @Size(min = 3, max = 255, message = "Title should be between {min} and {max}")
    @NotBlank(message = "Title should not be null")
    private String title;

    @Schema(description = "Age of User", example = "22")
    @Min(value = 18, message = "Age should be greater or equals than {value}")
    @Max(value = 100, message = "Age should be less or equals than {value}")
    private int age;
}
