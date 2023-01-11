package com.tracku.chris.tracku.Utils.CustomResponses;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    private String fullName;
    private String email;
    private LocalDate createdOn;
}
