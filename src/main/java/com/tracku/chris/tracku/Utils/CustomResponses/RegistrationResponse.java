package com.tracku.chris.tracku.Utils.CustomResponses;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    private long id;
    private String fullName;
    private String email;
    private LocalDateTime created_at;
}
