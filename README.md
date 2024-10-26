# How to launch application:
1. Launch desktop docker
2. Launch docker compose script by running the following commands from the project root:
```
cd .\src\main\resources\scripts\docker
docker compose up
```
3. Launch the backend
4. Launch the frontend (available: https://github.com/EdouardYu/FootCardz-UI)
# How to signup application:

# How to retrieve activation code or reset password code:
From the address bar of a web browser: http://localhost:8082

# Test the admin features on the API that are not implemented in the application:
1. Launch postman and import the file available from the project root:
```
.\src\main\resources\static\postman\FootCardz.postman_collection.json
```
2. Log in as admin (available in sign in as admin request in the user folder):
   - username: admin
   - password: admin
3. Retrieve the bearer token and use it to add, modify or remove a player, league, team or nation
Test images are available from the project root:
```
.\src\main\resources\static\postman\images
```
