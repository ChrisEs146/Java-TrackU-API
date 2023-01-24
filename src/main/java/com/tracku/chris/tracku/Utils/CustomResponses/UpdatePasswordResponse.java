package com.tracku.chris.tracku.Utils.CustomResponses;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
public class UpdatePasswordResponse {
    private final String Message = "Password updated successfully";
}
