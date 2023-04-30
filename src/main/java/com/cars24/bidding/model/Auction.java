package com.cars24.bidding.model;

import com.cars24.bidding.model.Enum.AuctionStatus;
import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


/* Auction entity */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Component
@Entity
public class Auction {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long auctionId;

    @Enumerated(EnumType.STRING)
    private AuctionStatus auctionStatus;

   // @Temporal(TemporalType.DATE)
   @Column
    private LocalDateTime startTime;

   @Column
   private Double startingPrice;

   // @Temporal(TemporalType.DATE)
   @Column
    private LocalDateTime endTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name ="carId")
    private Car car;

    @OneToMany(mappedBy="auction",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dealer> dealer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winnerBidId")
    private Bid winnerBid;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids;



}
