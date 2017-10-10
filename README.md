This is a simple TicTacToe server using Spring (Java), Websockets, and MongoDB.

# Stack Overview
### Client / Webpage
Client (webpage) uses JQuery for basic page changes, and SockJS and STOMP libraries to handle the websocket during gameplay.

### Server
Server runs on Java using the Spring Framework.

Spring REST is used to handle basic queries before gameplay, such as getting a list of available games, creating a new game, joining a game, or disconnecting.

Spring STOMP Websocket is used to handle messages sent during gameplay. "Move" messages are sent to the server, which then updates the clients with the new game state.

MongoDB and Spring MongoDB are used to store the game state models.
