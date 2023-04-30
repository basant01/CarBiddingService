package com.cars24.bidding.repository;

import com.cars24.bidding.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car,Long> {
}
