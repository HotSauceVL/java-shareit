package ru.practicum.shareit.user.dto;

import lombok.*;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDto {
    @PositiveOrZero
    private long id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
