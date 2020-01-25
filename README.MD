#### Example project for https://stackoverflow.com/questions/59877965/set-content-range-in-spring
Example project for http `Range` and `Content-Range` header handling through filters in Spring.
#### usage:

- get all without any range:
    ```
    curl http://localhost:8080/lc/all
    
    [{"lc":"a"},{"lc":"b"},{"lc":"c"}]
    ```
  note: `Content-Range` header is not present in response
- get middle part:
    ```
    curl http://localhost:8080/lc/all -i -H "Range: bytes=12-21"
    
    HTTP/1.1 200
    Content-Range: bytes 12-21/34
    Content-Length: 10
    Date: Sat, 25 Jan 2020 10:57:18 GMT
    
    {"lc":"b"}
    ```
- get everything from 12th bytes:
    ```
    curl http://localhost:8080/lc/all -i -H "Range: bytes=12-"
    
    HTTP/1.1 200
    Content-Range: bytes 12-33/34
    Content-Length: 22
    Date: Sat, 25 Jan 2020 10:58:30 GMT
    
    {"lc":"b"},{"lc":"c"}]
    ```
