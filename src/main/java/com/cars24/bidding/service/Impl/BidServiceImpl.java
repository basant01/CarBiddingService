package com.cars24.bidding.service.Impl;

import com.cars24.bidding.Utils.AuctionUtils;
import com.cars24.bidding.dao.AuctionDao;
import com.cars24.bidding.model.*;
import com.cars24.bidding.model.Enum.AuctionStatus;
import com.cars24.bidding.repository.AuctionRepository;
import com.cars24.bidding.repository.BidRepository;
import com.cars24.bidding.repository.CarRepository;
import com.cars24.bidding.repository.DealerRepository;
import com.cars24.bidding.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/* BidServiceImpl class provides the implementation for handling the core functionalities of the bidding service.*/
@Service
public class BidServiceImpl implements BidService {

    @Autowired
    Auction auction;

    @Autowired
    AuctionDao auctionDao;

    @Autowired
    DealerRepository dealerRepository;

    @Autowired
    CarRepository carRepository;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    BidRepository bidRepository;

    /* In the createAuction method, it checks if the necessary information for creating an auction is present or not.
     If everything is correct, it creates a new Auction entity and saves it in the database.
     It also saves the associated Car entity and Dealer entities if present.
     Finally, it returns the saved Auction entity as the response body. */
    @Override
    public ResponseEntity<Auction> createAuction(AuctionWrapper auctionWrapper) {

        try {

            if (auctionWrapper.getCar() == null || auctionWrapper.getStartingPrice() < 0 ||
                    auctionWrapper.getDealer() == null || auctionWrapper.getDealer().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Car car = auctionWrapper.getCar();
            Car savedCar = carRepository.save(car);

            auction.setAuctionStatus(AuctionStatus.CREATED);
            auction.setStartingPrice(auctionWrapper.getStartingPrice());
            auction.setStartTime(LocalDateTime.now());
            auction.setEndTime(auction.getStartTime().plusDays(1));
            auction.setCar(savedCar);


            Auction savedAuction = auctionDao.save(auction);

            if (auctionWrapper.getDealer() != null) {
                List<Dealer> dealers = auctionWrapper.getDealer();
                for (Dealer dealer : dealers) {
                    dealer.setAuction(savedAuction);
                    dealerRepository.save(dealer);
                }
                savedAuction.setDealer(dealers);
                auctionDao.save(savedAuction);
            }

            // return saved Auction entity as response body
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAuction);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    /*  In the patchStatus method, it first checks if the auction exists or not.
    If it does not exist, it returns a 404 Not Found response.
    If the auction exists, it checks if the auction status is CREATED or not.
    If the auction status is not CREATED, it returns a 400 Bad Request response.
    Otherwise, it updates the auction status to RUNNING and returns the updated Auction entity as the response body.
     */
    @Override
    public ResponseEntity<Auction> patchStatus(Long auctionId) {

        Auction auction = auctionRepository.findById(auctionId).orElse(null);

        if (auction == null) {
            return ResponseEntity.notFound().build();
        }

        if (auction.getAuctionStatus() == AuctionStatus.CREATED) {
            return ResponseEntity.ok(auctionDao.patchStatus(auction));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /*
    In the getWinnerBid method, it first checks if the auction exists or not.
    If it does not exist, it returns a 404 Not Found response.
    If the auction exists, it retrieves the winner bid from the list of bids associated with the auction using the AuctionUtils.getWinnerBid method.
    If there is no winner bid, it returns a 204 No Content response with a custom message.
    Otherwise, it returns a 200 OK response with a map containing the bid amount and dealer ID of the winner bid.
     */
    @Override
    public ResponseEntity<?> getWinnerBid(Long auctionId) {
        Optional<Auction> optionalAuction = auctionRepository.findById(auctionId);

        if (!optionalAuction.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Auction auction = optionalAuction.get();
        Bid winnerBid = AuctionUtils.getWinnerBid(auction.getBids());

        if (winnerBid == null) {
            return ResponseEntity.noContent()
                    .header("Message", "Please place bid first")
                    .build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("bidAmount", winnerBid.getBidAmount());
        response.put("dealerId", winnerBid.getDealer().getDealerId());

        return ResponseEntity.ok(response);

    }

    /* In the placeBid method, it first checks if the auction and dealer IDs are valid or not.
    If they are not valid, it returns a 400 Bad Request response with an error message.
    If the auction and dealer IDs are valid, it retrieves the auction and dealer entities from the database.
    It then checks if the auction status is RUNNING or not.
    If it is not RUNNING, it returns a 400 Bad Request response with an error message.
    It then checks if the bid amount is higher than the current highest bid or not.
    If it is not higher than the current highest bid, it returns a 400 Bad Request response with an error message.
    It then checks if the bid amount is duplicate or invalid.
    If it is a duplicate or invalid bid amount, it returns a 400 Bad Request response with an error message.
    If everything is correct, it creates a new Bid entity and saves it in the database.
    It also adds the new Bid entity to the list of bids associated with the auction and saves the updated auction entity in the database. Fin
    ally, it returns a 200 OK response with a custom message and the saved Bid entity.  */
    @Override
    public ResponseEntity<Object> placeBid(Long auctionId, Long dealerId, Double bidAmount) {
        Optional<Auction> optionalAuction = auctionRepository.findById(auctionId);
        Optional<Dealer> optionalDealer = dealerRepository.findById(dealerId);

        if (!optionalAuction.isPresent() || !optionalDealer.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid auction or dealer ID.");
        }

        Auction auction = optionalAuction.get();
        Dealer dealer = optionalDealer.get();

        if (auction.getAuctionStatus() != AuctionStatus.RUNNING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Auction is not running.");
        }

        List<Bid> bids = auction.getBids();
        Double currentHighestBid = bids.isEmpty() ? auction.getStartingPrice() : bids.get(0).getBidAmount();

        if (bidAmount <= currentHighestBid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bid amount must be higher than the current highest bid.");
        }

        for (Bid existingBid : bids) {
            if (existingBid.getBidAmount() == (bidAmount)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicate or invalid bid.");
            }
        }

        Bid bid = Bid.builder()
                .auction(auction)
                .dealer(dealer)
                .bidAmount(bidAmount)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        Bid savedBid = bidRepository.save(bid);

        auction.getBids().add(savedBid);
        auctionDao.save(auction);

        BidResponse response = new BidResponse("Your bid has been placed.", savedBid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
