package com.tracku.chris.tracku.Utils.CustomResponses;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    private String fullName;
    private String email;
    private LocalDateTime created_at;
}
