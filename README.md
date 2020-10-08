# M2RS_AndroidStreamVid
## Creation of a client<->server system by bluetooth
>> M2 IISC RS
>
> ```DUCROUX Guillaume```
```SAINT-AMAND Matthieu```

Enclosed you will find the source files of the code. Import it on an IDE, here, Android Studio.

 ---
 
>The purpose of this program is to transfer a file by bluetooth bridge. 
>>On one side, we have the server whose role is to download a .mp4 video through a URL given by the user. Then, this server is placed on listening waiting for a client that can connect. 
>>On the other side at the client level, the client connects to one of the devices available in the paired bluetooth devices list. Once the bluetooth bridge is initialized, the server sends the downloaded file to the client which will save it in its /Download's directory.
