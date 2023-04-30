package com.cars24.bidding.Utils;

import com.cars24.bidding.model.Bid;

import java.util.List;

public class AuctionUtils {

    /*
    static method of this utility class
    that takes the list of bids as
    an input and returns the winner bid
    */

    public static Bid getWinnerBid(List<Bid> bids) {
        if (bids.isEmpty()) {
            return null;
        }
        Bid winnerBid = bids.get(0);
        for (Bid bid : bids) {
            if (bid.getBidAmount() > winnerBid.getBidAmount()) {
                winnerBid = bid;
            }
        }
        return winnerBid;
    }
}
