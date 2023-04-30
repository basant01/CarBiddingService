# CarBiddingService

For Running the CarBiddingService Appliation Follow Below Steps:

1. First Use Java 11 Version and MysSql as database
2. In Application.properties file change the spring.datasource.url,spring.datasource.username,
   spring.datasource.password
3. create schema in mysql and use same schema in spring.datasource.url
4. Now run BiddingApplication.java which is main Class.
5. As we are using Spring Security so to access API you need to first hit http://localhost:8081/api/v1/auction/token
   with request body as in json sample mentioned belwo :
{
   "userName":"Admin",
   "password":"Admin"
}
6. Once you got token now copy that token and to run any of api in Header tab of postman
   create a new key-value pair key as Authorization and value as Bearer token

![TokenPass](https://user-images.githubusercontent.com/24639055/235339586-6e57daf1-d39b-4e3c-b3f6-497c153c9810.jpg)

7. Now Run API

Please find below all Screenshot of 5 API :

1. api/v1/auction/token

![TokenAPI](https://user-images.githubusercontent.com/24639055/235339629-2bf1e2b4-c8c0-4295-86b3-cf6dada4eb45.jpg)


2. api/v1/auction/createAuction

![createAuction](https://user-images.githubusercontent.com/24639055/235339676-9973bef8-ec15-4bf8-be8f-8005dbc8034e.jpg)


3. api/v1/auction/status/98

![AuctionStatus](https://user-images.githubusercontent.com/24639055/235339706-0284b450-4ec8-4fd9-9778-14ed922f6a4a.jpg)

4. pi/v1/auction/placeBid?auctionId=98&dealerId=99&bidAmount=12000000000

![placeBid](https://user-images.githubusercontent.com/24639055/235339751-9b7f362b-241d-417e-b50e-e30eac720294.jpg)

5.api/v1/auction/details/98/winner-bid

![WinnerBid](https://user-images.githubusercontent.com/24639055/235339777-8e8957a0-01a3-4de2-87b8-d10a21ed8116.jpg)

