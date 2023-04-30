package com.cars24.bidding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* response of creation of Bid API*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidResponse  {

    private String message;
    private Bid bid;


}
