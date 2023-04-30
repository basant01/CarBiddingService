package com.cars24.bidding.dao;

import com.cars24.bidding.model.Auction;
import com.cars24.bidding.model.Enum.AuctionStatus;
import com.cars24.bidding.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/* AuctionDao for implement some complex query in future and provide loose coupling*/

@Repository
public class AuctionDao {

    @Autowired
    AuctionRepository auctionRepository;

    public Auction save(Auction auction) {
        return auctionRepository.save(auction);
    }

    public Auction patchStatus(Auction auction) {
        auction.setAuctionStatus(AuctionStatus.RUNNING);
       return auctionRepository.save(auction);

    }
}
