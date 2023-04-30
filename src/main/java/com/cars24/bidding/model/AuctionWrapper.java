package com.cars24.bidding.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.util.List;

/* AuctionWrapper act as  request body for creation of auction*/

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuctionWrapper {

    private double startingPrice;
    private Car car;
    private List<Dealer> dealer;


}
