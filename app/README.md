- Install json server
```
npm install -g json-server
```
- Run the json server from where db.json is.

```
json-server --watch db.json --host <IP:Adress> --port 3000
```
- Navigate to the directory `app/src/main/java/com/example/paymentapp/network` in your Android project.

- Create a Kotlin object named `Config` in this directory.

- Add the following content to the Config.kt file
```
package com.example.paymentapp.network

object Config {
    const val BASE_URL = "http://172.17.212.100:3000/"
}
```

- I recommend running Android Studio with a physical device.
- For what to do simply choose one of the functional requirements and work on it.
