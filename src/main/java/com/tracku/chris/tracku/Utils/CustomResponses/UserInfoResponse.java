package com.tracku.chris.tracku.Utils.CustomResponses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private long id;
    private String fullName;
    private String email;
}
