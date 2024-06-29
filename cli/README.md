
### Command
```shell
docker compose exec {컨테이너명} {명령어}
```

#### 예시
```shell
docker compose exec kafka1 \
  kafka-topic.sh \
  --bootstrap-server localhost:19092 \
  --create --topic test-topic \
  --partitions 1
  --replication-factor 2
```

### kafka-topic.sh
> 토픽 관리 명령어

- `--create` : 토픽 생성
- `--alter` : 토픽 수정
- `--list` : 토픽 목록 조회
- `--describe` : 토픽 상세 조회
- `--delete` : 토픽 삭제

### kafka-console-producer.sh
> 프로듀서 관련 명령어

### kafka-console-consumer.sh
> 컨슈머 관련 명령어

### kafka-consumer-groups.sh
> 컨슈머 그룹 관련 명령어

- `--list`: 컨슈머 그룹 목록 조회

