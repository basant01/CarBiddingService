package com.cars24.bidding.model;

import lombok.*;

// used for providing userName and passsword for generating token
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JwtRequest {

    String userName;
    String password;

}
