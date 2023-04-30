package com.cars24.bidding.repository;

import com.cars24.bidding.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository  extends JpaRepository<Bid, Long> {
}
