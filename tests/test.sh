#!/bin/bash

# Test script to compare REST vs SOAP responses
echo "Running SOAP API tests..."

# Configuration
SOAP_URL="http://localhost:8080/ws/ebay"
REST_URL="http://localhost:8081/api"  # Adjust to your REST API URL
TEST_EMAIL="test_$(date +%s)@example.com"
TEST_USERNAME="testuser_$(date +%s)"
TEST_PASSWORD="password123"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Helper function to extract values from XML
extract_xml_value() {
  local xml=$1
  local xpath=$2
  echo "$xml" | grep -o "<$xpath>[^<]*</$xpath>" | sed -e "s/<$xpath>//" -e "s/<\/$xpath>//"
}

# Helper function to extract values from JSON
extract_json_value() {
  local json=$1
  local key=$2
  echo "$json" | grep -o "\"$key\":\"[^\"]*\"" | sed -e "s/\"$key\":\"//g" -e "s/\"//g"
}

# Test user registration
test_user_registration() {
  echo "Testing user registration..."
  
  # SOAP request
  SOAP_RESPONSE=$(curl -s -X POST -H "Content-Type: text/xml" -d @- $SOAP_URL << EOF
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ser="http://soap.ebay.com/service">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:registerUserRequest>
         <ser:username>$TEST_USERNAME</ser:username>
         <ser:email>$TEST_EMAIL</ser:email>
         <ser:password>$TEST_PASSWORD</ser:password>
         <ser:firstName>Test</ser:firstName>
         <ser:lastName>User</ser:lastName>
      </ser:registerUserRequest>
   </soapenv:Body>
</soapenv:Envelope>
EOF
)

  # REST request
  REST_RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" -d @- $REST_URL/users/register << EOF
{
  "username": "$TEST_USERNAME",
  "email": "$TEST_EMAIL",
  "password": "$TEST_PASSWORD",
  "firstName": "Test",
  "lastName": "User"
}
EOF
)

  # Extract and compare values
  SOAP_USERNAME=$(extract_xml_value "$SOAP_RESPONSE" "username")
  REST_USERNAME=$(extract_json_value "$REST_RESPONSE" "username")
  
  if [ "$SOAP_USERNAME" == "$TEST_USERNAME" ] && [ "$REST_USERNAME" == "$TEST_USERNAME" ]; then
    echo -e "${GREEN}✓ User registration test passed${NC}"
    return 0
  else
    echo -e "${RED}✗ User registration test failed${NC}"
    echo "SOAP Response: $SOAP_RESPONSE"
    echo "REST Response: $REST_RESPONSE"
    return 1
  fi
}

# Test login
test_login() {
  echo "Testing login..."
  
  # SOAP request
  SOAP_RESPONSE=$(curl -s -X POST -H "Content-Type: text/xml" -d @- $SOAP_URL << EOF
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ser="http://soap.ebay.com/service">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:loginRequest>
         <ser:email>$TEST_EMAIL</ser:email>
         <ser:password>$TEST_PASSWORD</ser:password>
      </ser:loginRequest>
   </soapenv:Body>
</soapenv:Envelope>
EOF
)

  # REST request
  REST_RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" -d @- $REST_URL/auth/login << EOF
{
  "email": "$TEST_EMAIL",
  "password": "$TEST_PASSWORD"
}
EOF
)

  # Extract token
  SOAP_TOKEN=$(extract_xml_value "$SOAP_RESPONSE" "token")
  REST_TOKEN=$(extract_json_value "$REST_RESPONSE" "token")
  
  if [ -n "$SOAP_TOKEN" ] && [ -n "$REST_TOKEN" ]; then
    echo -e "${GREEN}✓ Login test passed${NC}"
    # Save token for later tests
    export AUTH_TOKEN=$SOAP_TOKEN
    return 0
  else
    echo -e "${RED}✗ Login test failed${NC}"
    echo "SOAP Response: $SOAP_RESPONSE"
    echo "REST Response: $REST_RESPONSE"
    return 1
  fi
}

# Add more test functions for other operations
test_create_listing() {
  echo "Testing create listing..."
  # Similar implementation as above
}

test_search_listings() {
  echo "Testing search listings..."
  # Similar implementation as above
}

test_place_bid() {
  echo "Testing place bid..."
  # Similar implementation as above
}

test_buy_now() {
  echo "Testing buy now..."
  # Similar implementation as above
}

# Run all tests
echo "Starting tests..."
FAILED=0

test_user_registration
if [ $? -ne 0 ]; then
  FAILED=$((FAILED+1))
fi

test_login
if [ $? -ne 0 ]; then
  FAILED=$((FAILED+1))
fi

# Uncomment to run additional tests
# test_create_listing
# test_search_listings
# test_place_bid
# test_buy_now

if [ $FAILED -eq 0 ]; then
  echo -e "${GREEN}All tests passed!${NC}"
  exit 0
else
  echo -e "${RED}$FAILED tests failed!${NC}"
  exit 1
fi
