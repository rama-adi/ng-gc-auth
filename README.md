# Nextgen GCAuth
(Adapted from [GCAuth by exzork](https://github.com/exzork/GCAuth)) an full-stack authentication system for Grasscutter

This plugin is still WIP so you will need to compile from source

### Usage :
To run the authentication system, you must already have a NGAuth web app running (repo WIP). If you've done so, follow the steps below

1. Place the `nextgengcauth` jar into your plugins folder
2. Open the `config.json` file and copy the secret key
3. On your NGAuth web app, open the `.env` file and put in the secret key in `GCAUTH_SECRET_TOKEN`
4. 