--Education Connect main file


--Global variables
local connected = false
local timeremaining = 0
 
--Initiate the connection
function initiateConnection()
     connected = true
     print("Connection successful")
end
 
--Update the connection
function updateConnection()
     if connected == true then
          timeremaining = timeremaining - 1
          if timeremaining <= 0 then
               connected = false
               timeremaining = 0
               print("Connection lost")
          end
     end
end
 
--Send a request
function requestSend(msg)
     if connected == true then
          print("Sending request: " .. msg)
     else
          print("No connection")
     end
end
 
--Respond to a request
function requestResponse(msg)
     if connected == true then
          print("Received request: " .. msg)
     else
          print("No connection")
     end
end
 
--Disconnect the connection
function disconnectConnection()
     connected = false
     timeremaining = 0
     print("Connection terminated")
end
 
--Main
while true do
     --Check for the connection
     if connected == false then
          initiateConnection()
     end
     --Update the connection
     updateConnection()
     --Perform actions
     requestSend("Test request")
     requestResponse("Test response")
     --Disconnect if needed
     if timeremaining <= 0 then
          disconnectConnection()
     end
     sleep(1)
end