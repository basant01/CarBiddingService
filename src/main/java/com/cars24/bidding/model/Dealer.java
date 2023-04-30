package com.cars24.bidding.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;

/* Dealer entity holding all information of Dealer*/
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Dealer {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long dealerId;

    @Column
    private String dealerName;

    @Column
    private String email;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="auctionId")
    private Auction auction;



}
