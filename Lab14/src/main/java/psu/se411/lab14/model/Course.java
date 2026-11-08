package psu.se411.lab14.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("COURSES")
public record Course(
        @Id @NotNull @PositiveOrZero Long id,
        @NotBlank String code,
        @NotBlank @Size(min = 2) String name,
        String description,
        @NotNull @Min(1) @Max(10) Integer credits
) {
}
