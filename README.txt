To compile you need to get your own API key because we have our own SHA keys.
For facebook sign in I'd need your Key hash to add it to the fb perms cause
it's an "in development app". You can get that using:

keytool -exportcert -alias YOUR_RELEASE_KEY_ALIAS -keystore YOUR_RELEASE_KEY_PATH | openssl sha1 -binary | openssl base64

L1O+e7UCSfsbnXK9yMrm04SBfAk= looks like this


KNOWN BUGS:
-Game only disappears if someone wins
-Free for all doesnt work
-Some of the ui is not optimized for smaller phones