package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingInputDto {
    private long id;
    private Long bookerId;
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
    private BookingStatus status;

}
