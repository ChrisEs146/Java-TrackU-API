package com.tracku.chris.tracku.Utils.CustomResponses;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private long id;
    private String fullName;
    private String email;
}
