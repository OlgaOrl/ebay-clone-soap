#!/bin/bash

# Example SOAP client using curl
BASE_URL="http://localhost:8080/ws/ebay"

# Register User Example
register_user() {
  echo "Registering new user..."
  curl -X POST -H "Content-Type: text/xml;charset=UTF-8" -H "SOAPAction: \"\"" -d @- $BASE_URL << EOF
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:typ="http://soap.ebay.com/types">
   <soapenv:Header/>
   <soapenv:Body>
      <typ:registerUserRequest>
         <typ:username>testuser</typ:username>
         <typ:email>test@example.com</typ:email>
         <typ:password>password123</typ:password>
         <typ:firstName>Test</typ:firstName>
         <typ:lastName>User</typ:lastName>
      </typ:registerUserRequest>
   </soapenv:Body>
</soapenv:Envelope>
EOF
}

# Run examples
echo "SOAP Client Examples"
echo "===================="
echo "1. Register User"
echo "0. Exit"
echo

read -p "Enter your choice: " choice

case $choice in
  1) register_user ;;
  0) exit 0 ;;
  *) echo "Invalid choice" ;;
esac

