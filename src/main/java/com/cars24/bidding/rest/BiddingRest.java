package com.cars24.bidding.rest;

import com.cars24.bidding.model.*;
import com.cars24.bidding.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/auction")
public class BiddingRest {

    @Autowired
    BidService bidService;


    /**
     * creating new auction for car based on auctionWrapper
     *
     * @requestBody auctionWrapper the request wrapper for createAuction
     * @return new auction for a car
     */
    @PostMapping("/createAuction")
    public ResponseEntity<Auction> createAuction(@RequestBody AuctionWrapper auctionWrapper) {
        return bidService.createAuction(auctionWrapper);
    }

    /**
     * update the status of auction as running for auction
     *
     * @param  id is the auctionId
     * @return make status of auction as RUNNING
     */
    @PatchMapping("/status/{id}")
    public ResponseEntity<Auction> patchStatus(@PathVariable(required = true) Long id) {
        return bidService.patchStatus(id);
    }


    /**
     * Getting WinnerBid and its respective dealer in respective auction.
     *
     * @param auctionId the path param for getting winnerBid
     * @return Retrieved WinnerBid and its respective dealer from auctionId
     */
    @GetMapping("/details/{auctionId}/winner-bid")
    public ResponseEntity<?> getWinnerBid(@PathVariable(required = true) Long auctionId) {

        return bidService.getWinnerBid(auctionId);
    }

    /**
     * creating new bid for dealer in auction for a car
     * @param auctionId
     * @param dealerId
     * @param bidAmount
     * @return new bid for a dealer for car auction
     */
    @PostMapping("/placeBid")
    public ResponseEntity<Object> placeBid(
            @RequestParam(required = true) Long auctionId,
            @RequestParam(required = true) Long dealerId,
            @RequestParam(required = true) Double bidAmount) {
        return  bidService.placeBid(auctionId, dealerId, bidAmount);

    }
}


