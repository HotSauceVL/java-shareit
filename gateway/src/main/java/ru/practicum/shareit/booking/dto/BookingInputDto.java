package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingInputDto {
    @NotNull
    private Long itemId;
    @NotNull
    @Future
    @JsonProperty("start")
    private LocalDateTime startDate;
    @NotNull
    @Future
    @JsonProperty("end")
    private LocalDateTime endDate;

}
