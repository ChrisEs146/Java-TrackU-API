package com.tracku.chris.tracku.Utils.CustomResponses;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
public class UpdatePasswordResponse {
    private final String Message = "Password updated successfully";
}
