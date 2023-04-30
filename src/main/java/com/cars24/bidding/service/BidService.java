package com.cars24.bidding.service;

import com.cars24.bidding.model.*;
import com.cars24.bidding.model.Enum.AuctionStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface BidService {

    ResponseEntity<Auction> createAuction(AuctionWrapper auctionWrapper);

    ResponseEntity<Auction> patchStatus(Long auctionId);


    ResponseEntity<?> getWinnerBid(Long auctionId);
    ResponseEntity<Object> placeBid(Long auctionId, Long dealerId,Double bidAmount);

}
