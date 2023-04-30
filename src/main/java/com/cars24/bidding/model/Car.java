package com.cars24.bidding.model;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

/* car entity holding all information of car*/

@Entity
public class Car {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long carId;

    @Column
    private String model;

    @Column
    private String make;

    @Column
    private String year;
}
