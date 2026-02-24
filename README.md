# Point Service

대량 트랜잭션 환경에서 포인트 적립/사용/취소 요청이 동시에 발생하더라도  
정합성을 유지할 수 있도록 설계한 포인트 처리 시스템입니다.

동시 요청으로 인한 중복 처리, 경쟁 상태, 부분 실패 상황을 고려하여  
포인트 마스터/이벤트/사용 단위를 분리하고 추적 가능 구조로 설계했습니다.

---

## 핵심 문제
포인트 시스템은 다음 문제가 자주 발생합니다.

- 동시에 요청이 들어올 때 잔액 불일치
- 동일 요청 중복 처리
- 사용/취소 이력 추적 불가
- 정책 변경 시 로직 수정 필요

---

## 해결 전략

### 1. 포인트 상태 구조 분리
- `user_point_info` → 사용자 포인트 마스터
- `point_item` → 실제 포인트 단위
- `point_usage` → 주문 단위 사용
- `point_usage_link` → 실제 차감된 포인트 연결

→ 잔액 / 사용내역 / 이벤트를 독립적으로 관리

---

### 2. 동시성 제어
`user_point_info` 엔티티에 `@Version` 적용하여  
낙관적 락 기반 경쟁 상태를 방지했습니다.

---

### 3. 멱등 처리 확장 가능 구조
중복 거래 식별 키(transactionId 등)를 추가하면  
재요청에도 동일 결과가 보장되도록 확장 가능한 구조로 설계했습니다.

---

### 4. 정책 분리 + 캐싱
포인트 정책을 테이블로 분리하고 캐시 적용 가능 구조로 설계했습니다.  
→ Redis 적용 시 조회 비용 감소

---

### 5. 일관된 응답 구조
모든 API 응답은 `CommonResponseDto`로 통일하고  
전역 ExceptionHandler로 오류 상황도 동일 포맷으로 반환합니다.

---

## DB 구조

- user (외부 도메인)
- order (외부 도메인)
- user_point_info
- point_event
- point_item
- point_usage
- point_usage_link

---

##  테스트데이터   
/resources/postman/Point.postman_test_collection.json 에 postman으로 사용할 컬렉션있습니다.   

| 기능          | Method | Endpoint            | Request                                                                 |
|---------------|--------|--------------------|-------------------------------------------------------------------------|
| 사용자 생성    | POST   | /users             | { "userId": "koo" }                                                    |
| 포인트 적립    | POST   | /point/earn        | { "userId": "koo", "pointAmount": 2000 }                               |
| 포인트 취소    | PUT    | /point/cancel      | { "pointItemKey": 1}                                                   |
| 포인트 사용    | POST   | /point/use         | { "orderKey": "ORD001", "pointUseAmount": 1500, "userId": "koo" }      |
| 포인트 사용 취소| POST  | /point/use/cancel   | { "orderKey": "ORD001", "pointCancelAmount": 1000, "userId": "koo" }   |
| 정책 변경      | PUT    | /point/policy      | { "userId": "koo", "pointEarnLimit": 3000, "pointTotalLimit": 50000 }  |

---

## 실행 방법
```bash
git clone https://github.com/modekoo/point
cd point
chmod +x ./gradlew
git checkout dev
git pull origin dev
./gradlew clean build
./gradlew bootrun
```