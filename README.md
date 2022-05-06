# GCAuth
Grasscutter Authentication System
### Usage : 
- Place jar inside plugins folder of Grasscutter.
- To change hash algorithm change `Hash` in config.json inside plugins/GCAuth (Only Bcrypt and Scrypt is supported)
- All payload must be send with `application/json` and Compact JSON format ( without unnecessary spaces )
- Auth endpoint is:
  - Authentication Checking : `/authentication/type` (GET) , it'll return `GCNGAuthAuthenticationHandler` if GCAuth is loaded and enabled.
  - Register: `/authentication/register` (POST)
  ```
  {"username":"username","password":"password","password_confirmation":"password_confirmation"}
  ```
  - Login: `/authentication/login` (POST) 
  ```
  {"username":"username","password":"password"}
  ```
  - Change password: `/authentication/change_password` (POST)  
  ```
  {"username":"username","new_password":"new_password","new_password_confirmation":"new_password_confirmation","old_password":"old_password"}
  ```
- Response is `JSON` with following keys:
  - `status` : `success` or `error`
  - `message` : 
    - AUTH_ENABLED : Plugin is enabled
    - AUTH_DISABLED : Plugin is disabled
    - EMPTY_BODY : No data was sent with the request
    - USERNAME_TAKEN : Username is already taken
    - PASSWORD_MISMATCH : Password does not match
    - UNKNOWN : Unknown error
    - INVALID_ACCOUNT : Username or password is invalid
    - NO_PASSWORD : Password is not set, please set password first by resetting it (change password)
  - `jwt` : JWT token if success with body :
    - `token` : Token used for authentication, paste it in username field of client.
    - `username` : Username of the user.
    - `uid` : UID of the user.