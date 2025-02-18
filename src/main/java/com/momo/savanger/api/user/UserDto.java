package com.momo.savanger.api.user;

import com.momo.savanger.converter.DateTimeConverter;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String username;

    @DateTimeConverter
    private LocalDateTime dateRegistered;
}
