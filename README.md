# point

```
1. 빌드/실행 방법   
  -> git clone https://github.com/modekoo/point   
  -> cd point   
  -> chmod +x ./gradlew
  -> git checkout dev   
  -> git pull origin dev  
  -> ./gradlew build clean   
  -> ./gradlew bootrun
```
```
2. DB 설계  
1)User(유저), order(주문)  //타도메인  
2)user_point_info(계정과 연결된 포인트 관리)  
3)point_evet(포인트 관련 이벤트)  
4)point_item(포인트들)   
5)point_usage(order에 대한 포인트 사용 내역, header)  
6)point_usage_link(실제 포인트 사용 내역)
```
```
3. 프로젝트 구조   
musinsa.point.config    # cache, mode설정   
musinsa.point.consts    # 프로젝트 고정값   
musinsa.point.domain    # 도메인 서비스(controller, service, entity, repo...)   
musinsa.point.dto       # 공통처리DTO   
musinsa.point.exception # 에러처리 관련
```
```
4. 서비스 설계   
 1) 정책을 table화 하여 분리하고 cache를 적용했습니다. 
  -> Redis에도 적합합니다.
 2) userPointInfo는 포인트를 관리하는 master로서 lock이 필요해보여 @version을 두었습니다.  
    중복거래를 판별할수있는 key가 있으면 좋을 것 같습니다.  
 3) 진입 데이터는 reqDto로 칭하며, spring-validation을 이용한 @valid를 걸었습니다.  
 4) CommonResponseDto로 모든 응답은 같은 구조로 처리됩니다.(error포함)    
 5) 전역 ExceptionHandler를 두어 에러시에도 처리 후 응답값을 조립합니다.
 -> -> 현재는 기본 ResponseEntityExceptionHandler를 상속하여 부분 적용이 되어있습니다.   
 5) 서비스에서 금액적으로 validation해야하는 부분이 있어 mode를 'SOFT', 'HARD'로 구분해봤습니다.   
 -> HARD 일경우 최대충전금액 등 validation으로 튕겨내며 SOFT일경우 최대충전금액까지 충전하거나 
    1회한도 금액이 초과 시 1회한도 금액까지 충전등이 가능합니다.   
 6) User, Order는 타도메인이지만 임시로 넣어두었습니다.
```
```
5.   테스트데이터   
/resouces/postman/Point.postman_test_collection.json 에 postman으로 사용할 컬렉션있습니다.   

| 기능        | Method | Endpoint            | Request                                                                 |
|-----------|--------|---------------------|-------------------------------------------------------------------------|
| 사용자 생성    | POST   | `/users`            | `{ "userId": "koo" }`                                                   |
| 포인트 적립    | POST   | `/point/earn`       | `{ "userId": "koo", "pointAmount": 2000 }`                              |
| 포인트 취소    | PUT    | `/point/cancel`      | `{ "pointItemKey": 1}`                                                   |
| 포인트 사용    | POST   | `/point/use`        | `{ "orderKey": "ORD001", "pointUseAmount": 1500, "userId": "koo" }`     |
| 포인트 사용 취소 | POST   | `/point/use/cancel` | `{ "orderKey": "ORD001", "pointCancelAmount": 1000, "userId": "koo" }`  |
| 정책 변경     | PUT    | `/point/policy`     | `{ "userId": "koo", "pointEarnLimit": 3000, "pointTotalLimit": 50000 }` |
```
```
6.  AWS는 제가 실무에 사용해보지 못하여 더존에서 사용한 뉴타닉스기반 VM 클라우드, 하나은행 오픈쉬프트k8s를 참고했습니다.   
```
```
7.  좋은 과제를 진행할 수 있어 행복했습니다. 문제가 몇 안되지만 많은 고민을 할 수 있었습니다. 읽어 주셔서 감사합니다.
    구지경 드림.
```