package com.tracku.chris.tracku.Utils.CustomResponses;

import lombok.*;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNameResponse {
    private long id;
    private String fullName;
    private String email;
}
