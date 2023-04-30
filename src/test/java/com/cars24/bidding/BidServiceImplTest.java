package com.cars24.bidding;

import com.cars24.bidding.Utils.AuctionUtils;
import org.junit.Assert;
import com.cars24.bidding.dao.AuctionDao;
import com.cars24.bidding.model.*;
import com.cars24.bidding.model.Enum.AuctionStatus;
import com.cars24.bidding.repository.AuctionRepository;
import com.cars24.bidding.repository.BidRepository;
import com.cars24.bidding.repository.CarRepository;
import com.cars24.bidding.repository.DealerRepository;
import com.cars24.bidding.service.BidService;
import com.cars24.bidding.service.Impl.BidServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BidServiceImplTest {

    @Mock
    private AuctionDao auctionDao;
    @Mock
    private DealerRepository dealerRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private BidRepository bidRepository;

    @InjectMocks
    private BidServiceImpl bidService;

    private AuctionWrapper auctionWrapper;

    @Mock
   private Auction auction;

    @BeforeEach
    public void setUp() {

        auctionWrapper = new AuctionWrapper();
        auctionWrapper.setStartingPrice(1000.0);

        Dealer dealer1 = new Dealer();
        dealer1.setDealerId(1L);
        dealer1.setDealerName("Dealer 1");

        Dealer dealer2 = new Dealer();
        dealer2.setDealerId(2L);
        dealer2.setDealerName("Dealer 2");

        List<Dealer> dealers = new ArrayList<>();
        dealers.add(dealer1);
        dealers.add(dealer2);

        auctionWrapper.setDealer(dealers);

//        auction = new Auction();
//        auction.setAuctionStatus(AuctionStatus.CREATED);
//        auction.setStartingPrice(1000.0);
//        auction.setStartTime(LocalDateTime.now());
//        auction.setEndTime(auction.getStartTime().plusDays(1));
//        auction.setCar(new Car());

        auctionWrapper.setCar(new Car());
    }
    
    @Test
    public void createAuction_withValidInput_shouldReturnAuction() {
        when(auction.getStartTime()).thenReturn(LocalDateTime.now());

        Car car = new Car(12L, "Toyota", "Camry", "2018");
        when(carRepository.save(eq(car))).thenReturn(car);

        Dealer dealer1 = Dealer.builder().dealerId(1L).dealerName("Basant").email("b@gmail.com").build();
        Dealer dealer2 = Dealer.builder().dealerId(2L).dealerName("Manas").email("m@gmail.com").build();
        List<Dealer> dealers = Arrays.asList(dealer1, dealer2);

        when(auctionDao.save(eq(auction))).thenReturn(auction);
        AuctionWrapper auctionWrapper = new AuctionWrapper(1000.0, car, dealers);
        ResponseEntity<Auction> result = bidService.createAuction(auctionWrapper);
        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(auction, result.getBody());
    }

    // for testing that auction status is updated to "RUNNING" when auction status is "CREATED"
    @Test
    public void testPatchStatus_UpdatesAuctionStatus() {
        // Arrange
        Auction auction = new Auction();
        auction.setAuctionId(1L);
        auction.setAuctionStatus(AuctionStatus.CREATED);

        Auction auction1 = new Auction();
        auction1.setAuctionId(1L);
        auction1.setAuctionStatus(AuctionStatus.RUNNING);

        Mockito.when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        Mockito.when(auctionDao.patchStatus(auction)).thenReturn(auction1);

        // Act
        ResponseEntity<Auction> response = bidService.patchStatus(1L);

        // Assert
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(AuctionStatus.RUNNING, response.getBody().getAuctionStatus());
    }


    // for testing that the method returns a "NOT FOUND" status when the auction does not exist
    @Test
    public void testPatchStatus_ReturnsNotFound_WhenAuctionDoesNotExist() {
        // Arrange
        Mockito.when(auctionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Auction> response = bidService.patchStatus(1L);

        // Assert
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertNull(response.getBody());
    }


    // for testing that the method returns a "BAD REQUEST" status when the auction status is not "CREATED
    @Test
    public void testPatchStatus_ReturnsBadRequest_WhenAuctionStatusIsNotCreated() {
        // Arrange
        Auction auction = new Auction();
        auction.setAuctionId(1L);
        auction.setAuctionStatus(AuctionStatus.RUNNING);
        Mockito.when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        // Act
        ResponseEntity<Auction> response = bidService.patchStatus(1L);

        // Assert
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNull(response.getBody());
    }


    // for testing that the method returns a "Not Found" status when auction ID is null:
    @Test
    public void testPatchStatus_ReturnsBadRequest_WhenAuctionIdIsNull() {
        // Act
        ResponseEntity<Auction> response = bidService.patchStatus(null);

        // Assert
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertNull(response.getBody());
    }

    //When auction and dealer IDs are valid,
    // bid amount is higher than current highest bid, and auction status is running, the bid should be successfully placed
    @Test
    public void testPlaceBid_Successful() {
        Long auctionId = 1L;
        Long dealerId = 1L;
        Double bidAmount = 20000.0;

        Auction auction = new Auction();
        auction.setAuctionId(auctionId);
        auction.setStartingPrice(10000.0);
        auction.setAuctionStatus(AuctionStatus.RUNNING);
        List<Bid> bids = new ArrayList<>();
        auction.setBids(bids);

        Dealer dealer = new Dealer();
        dealer.setDealerId(dealerId);

        Mockito.when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        Mockito.when(dealerRepository.findById(dealerId)).thenReturn(Optional.of(dealer));
        Mockito.when(bidRepository.save(Mockito.any(Bid.class))).thenReturn(new Bid());

        ResponseEntity<Object> response = bidService.placeBid(auctionId, dealerId, bidAmount);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertTrue(response.getBody() instanceof BidResponse);
        BidResponse bidResponse = (BidResponse) response.getBody();
        Assert.assertEquals("Your bid has been placed.", bidResponse.getMessage());
        Assert.assertNotNull(bidResponse.getBid());
    }

    // a successful scenario where an  no auction has bids and returns the winner bid
    @Test
    public void testGetWinnerBidNoAuctionFound() {
        Long auctionId = 1L;

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = bidService.getWinnerBid(auctionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // the scenario where no bid have been placed on an auction
    // and verifies that the  bid is need to be place first with the corresponding dealer ID.
    @Test
    public void testGetWinnerBidNoBidsPlaced() {
        Long auctionId = 1L;
        Long dealerId = 1L;
        Double bidAmount = 1000.0;

        Dealer dealer = Dealer.builder().dealerId(dealerId)
                .dealerName("")
                .email("john@gmail.com")
                .build();
        

        Auction auction = Auction.builder().auctionId(auctionId)
                        .auctionStatus(AuctionStatus.RUNNING)
                                .startingPrice(1000.0)
                .bids(new ArrayList<>()).build();

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

        ResponseEntity<?> response = bidService.getWinnerBid(auctionId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        assertEquals("Please place bid first", response.getHeaders().get("Message").get(0));
    }

    // the scenario where single bid have been placed on an auction
    // and verifies that the highest bid is returned as the winner bid with the corresponding dealer ID.
    @Test
    public void testGetWinnerBidSingleBidPlaced() {
        Long auctionId = 1L;
        Long dealerId = 1L;
        Double bidAmount = 1000.0;
        Auction auction = Auction.builder().auctionId(auctionId)
                .auctionStatus(AuctionStatus.RUNNING)
                .startingPrice(500.0)
                .bids(null).build();

        Dealer dealer = Dealer.builder().dealerId(dealerId)
                .dealerName("")
                .email("john@gmail.com")
                .build();


        Bid bid = Bid.builder().bidId(1L)
                .auction(auction)
                .dealer(dealer)
                .bidAmount(bidAmount).build();

        List<Bid> bids = new ArrayList<>();
        bids.add(bid);

        auction.setBids(bids);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

        ResponseEntity<?> response = bidService.getWinnerBid(auctionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(bidAmount, ((Map<String, Object>) response.getBody()).get("bidAmount"));
        assertEquals(dealerId, ((Map<String, Object>) response.getBody()).get("dealerId"));
    }


    // the scenario where multiple bids have been placed on an auction
    // and verifies that the highest bid is returned as the winner bid with the corresponding dealer ID.
    @Test
    public void testGetWinnerBidMultipleBidsPlaced() {
      
        Long auctionId = 1L;
        Auction auction = Auction.builder()
                .auctionId(auctionId)
                .startingPrice(50000.0)
                .auctionStatus(AuctionStatus.RUNNING)
                .build();

        Dealer dealer1 = Dealer.builder()
                .dealerId(1L)
                .dealerName("John")
                .email("john.doe@example.com")
                .build();

        Dealer dealer2 = Dealer.builder()
                .dealerId(2L)
                .dealerName("Jane")
                .email("jane.doe@example.com")
                .build();

        Bid bid1 = Bid.builder()
                .auction(auction)
                .dealer(dealer1)
                .bidAmount(55000.0)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        Bid bid2 = Bid.builder()
                .auction(auction)
                .dealer(dealer2)
                .bidAmount(60000.0)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        List<Bid> bidList= new ArrayList<>();
        bidList.add(bid1);
        bidList.add(bid2);
        auction.setBids(bidList);
        Mockito.when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

        ResponseEntity<?> response = bidService.getWinnerBid(auctionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(2, responseBody.size());
        assertEquals(60000.0, responseBody.get("bidAmount"));
        assertEquals(2L, responseBody.get("dealerId"));
    }

    //When auction ID is invalid, the bid should not be placed
    @Test
    public void testPlaceBid_InvalidAuctionId() {
        Long auctionId = 1L;
        Long dealerId = 1L;
        Double bidAmount = 20000.0;

        Mockito.when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bidService.placeBid(auctionId, dealerId, bidAmount);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals("Invalid auction or dealer ID.", response.getBody());
    }

    //When dealer ID is invalid, the bid should not be placed
    @Test
    public void testPlaceBid_InvalidDealerId() {
        Long auctionId = 1L;
        Long dealerId = 1L;
        Double bidAmount = 20000.0;

        Auction auction = new Auction();
        auction.setAuctionId(auctionId);

        Mockito.when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        Mockito.when(dealerRepository.findById(dealerId)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bidService.placeBid(auctionId, dealerId, bidAmount);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals("Invalid auction or dealer ID.", response.getBody());
    }

    //When auction status is not running, the bid should not be placed
    @Test
    public void testPlaceBid_AuctionNotRunning() {
        Long auctionId = 1L;
        Long dealerId = 1L;
        Double bidAmount = 20000.0;

        Auction auction = new Auction();
        auction.setAuctionId(auctionId);
        auction.setAuctionStatus(AuctionStatus.CREATED);

        Dealer dealer = new Dealer();
        dealer.setDealerId(dealerId);

        Mockito.when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        Mockito.when(dealerRepository.findById(dealerId)).thenReturn(Optional.of(dealer));

        ResponseEntity<Object> response = bidService.placeBid(auctionId, dealerId, bidAmount);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals("Auction is not running.", response.getBody());
    }

// When bid amount is not higher than current highest bid, the bid should not be placed:
//java

    @Test
    public void testPlaceBidInvalidBidAmount() {
        Long auctionId = 1L;
        Long dealerId = 1L;
        Double bidAmount = 50000.0;

        Auction auction = Auction.builder()
                .auctionId(auctionId)
                .startingPrice(10000.0)
                .auctionStatus(AuctionStatus.RUNNING)
                .build();

        Dealer dealer = Dealer.builder()
                .dealerId(dealerId)
                .dealerName("Test Dealer")
                .build();

        List<Bid> bids = new ArrayList<>();
        bids.add(Bid.builder()
                .bidId(1L)
                .auction(auction)
                .dealer(dealer)
                .bidAmount(4000000.0)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build());
        auction.setBids(bids);

        Mockito.when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        Mockito.when(dealerRepository.findById(dealerId)).thenReturn(Optional.of(dealer));

        ResponseEntity<Object> responseEntity = bidService.placeBid(auctionId, dealerId, bidAmount);

       // Mockito.verify(bidRepository, Mockito.never()).save(Mockito.any(Bid.class));
     //   Mockito.verify(auctionDao, Mockito.never()).save(Mockito.any(Auction.class));

        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("Bid amount must be higher than the current highest bid.", responseEntity.getBody());
    }



}
