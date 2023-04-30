package com.cars24.bidding.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// used for producing response as token
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    String token;
}
