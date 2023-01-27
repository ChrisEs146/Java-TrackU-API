package com.tracku.chris.tracku.Utils.CustomRequests.Users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNameRequest {
    @NotBlank(message = "Full name can't be empty")
    @Size(min = 4, message = "Full name should be at least 4 chars long")
    private String newFullName;
}
