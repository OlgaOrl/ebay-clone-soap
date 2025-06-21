#!/bin/bash

# Example SOAP client using curl
BASE_URL="http://localhost:8080/ws/ebay"

# Register User Example
register_user() {
  echo "Registering new user..."
  curl -X POST -H "Content-Type: text/xml" -d @- $BASE_URL << EOF
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ser="http://soap.ebay.com/service">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:registerUserRequest>
         <ser:username>testuser</ser:username>
         <ser:email>test@example.com</ser:email>
         <ser:password>password123</ser:password>
         <ser:firstName>Test</ser:firstName>
         <ser:lastName>User</ser:lastName>
      </ser:registerUserRequest>
   </soapenv:Body>
</soapenv:Envelope>
EOF
}

# Login Example
login() {
  echo "Logging in..."
  curl -X POST -H "Content-Type: text/xml" -d @- $BASE_URL << EOF
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ser="http://soap.ebay.com/service">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:loginRequest>
         <ser:email>test@example.com</ser:email>
         <ser:password>password123</ser:password>
      </ser:loginRequest>
   </soapenv:Body>
</soapenv:Envelope>
EOF
}

# Create Listing Example
create_listing() {
  echo "Creating new listing..."
  curl -X POST -H "Content-Type: text/xml" -d @- $BASE_URL << EOF
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ser="http://soap.ebay.com/service">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:createListingRequest>
         <ser:title>Test Listing</ser:title>
         <ser:description>This is a test listing</ser:description>
         <ser:startingPrice>10.00</ser:startingPrice>
         <ser:buyNowPrice>50.00</ser:buyNowPrice>
         <ser:categoryId>1</ser:categoryId>
         <ser:duration>7</ser:duration>
         <ser:condition>NEW</ser:condition>
         <ser:images>https://example.com/image1.jpg</ser:images>
         <ser:authToken>YOUR_AUTH_TOKEN</ser:authToken>
      </ser:createListingRequest>
   </soapenv:Body>
</soapenv:Envelope>
EOF
}

# Search Listings Example
search_listings() {
  echo "Searching listings..."
  curl -X POST -H "Content-Type: text/xml" -d @- $BASE_URL << EOF
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ser="http://soap.ebay.com/service">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:searchListingsRequest>
         <ser:query>test</ser:query>
         <ser:minPrice>5.00</ser:minPrice>
         <ser:maxPrice>100.00</ser:maxPrice>
         <ser:page>0</ser:page>
         <ser:size>10</ser:size>
      </ser:searchListingsRequest>
   </soapenv:Body>
</soapenv:Envelope>
EOF
}

# Place Bid Example
place_bid() {
  echo "Placing bid..."
  curl -X POST -H "Content-Type: text/xml" -d @- $BASE_URL << EOF
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ser="http://soap.ebay.com/service">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:placeBidRequest>
         <ser:listingId>1</ser:listingId>
         <ser:amount>15.00</ser:amount>
         <ser:authToken>YOUR_AUTH_TOKEN</ser:authToken>
      </ser:placeBidRequest>
   </soapenv:Body>
</soapenv:Envelope>
EOF
}

# Buy Now Example
buy_now() {
  echo "Buying now..."
  curl -X POST -H "Content-Type: text/xml" -d @- $BASE_URL << EOF
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ser="http://soap.ebay.com/service">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:buyNowRequest>
         <ser:listingId>1</ser:listingId>
         <ser:authToken>YOUR_AUTH_TOKEN</ser:authToken>
      </ser:buyNowRequest>
   </soapenv:Body>
</soapenv:Envelope>
EOF
}

# Run examples
echo "SOAP Client Examples"
echo "===================="
echo "1. Register User"
echo "2. Login"
echo "3. Create Listing"
echo "4. Search Listings"
echo "5. Place Bid"
echo "6. Buy Now"
echo "7. Run All Examples"
echo "0. Exit"
echo

read -p "Enter your choice: " choice

case $choice in
  1) register_user ;;
  2) login ;;
  3) create_listing ;;
  4) search_listings ;;
  5) place_bid ;;
  6) buy_now ;;
  7) 
    register_user
    login
    create_listing
    search_listings
    place_bid
    buy_now
    ;;
  0) exit 0 ;;
  *) echo "Invalid choice" ;;
esac
