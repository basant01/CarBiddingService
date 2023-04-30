package com.cars24.bidding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.cars24.bidding.model","com.cars24.bidding.rest","com.cars24.bidding.service",
		"com.cars24.bidding.service.Impl","com.cars24.bidding.service.BidService",
		"com.cars24.bidding.dao","com.cars24.bidding.Security.jwt.helper","com.cars24.bidding.Security"})
public class BiddingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BiddingApplication.class, args);
	}

}
