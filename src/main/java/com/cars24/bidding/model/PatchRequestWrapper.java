package com.cars24.bidding.model;

import com.cars24.bidding.model.Enum.AuctionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* will used this class when you want patch api to make auction stop and running*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatchRequestWrapper {


    private int auctionId;

    private AuctionStatus auctionStatus;

}
