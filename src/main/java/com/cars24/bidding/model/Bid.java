package com.cars24.bidding.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.util.Date;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity(name="bid")
public class Bid {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long bidId;

    @Column
    private double bidAmount;
    @Column
    private int previousBidId;

    @Column
    private Date timestamp;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name ="auctionId")
    private Auction auction;

    @OneToOne
    @JoinColumn(name ="dealerId")
    private Dealer dealer;

}
