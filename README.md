## Запуск 
Необходимо собрать Dockerfile с параметрами access_token, api_version, group_id, согласно документации, в противном случае приложение не будет работать корректно.

```
sudo docker build --build-arg TOKEN=значение_токена --build-arg VERSION=5.199 --build-arg ID=значение_group_id .
```
