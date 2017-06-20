curl --data '{"username":"test", "password": "non" }' -v uuser-X POST -H 'Content-Type:application/json' http://localhost:8080/verifyCredentials
curl --data '{"username":"fred", "password": "dilban" }' -v uuser-X POST -H 'Content-Type:application/json' http://localhost:8080/verifyCredentials
