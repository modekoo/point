# point



1. 빌드방법   
  -> git clone https://github.com/modekoo/point   
  -> git checkout dev   
  -> git pull origin dev  
  -> ./gradlew build clean   
  -> ./gradlew bootrun


2. DB 설계  
1)User(유저), order(주문)  //타도메인  
2)user_point_info(계정과 연결된 포인트 관리)  
3)point_evet(포인트 관련 이벤트)  
4)point_item(포인트들)   
5)point_usage(order에 대한 포인트 사용 내역, header)  
6)point_usage_link(실제 포인트 사용 내역)


3. 서비스 설계   
1)정책을 table화 하여 분리하고 cache를 적용했습니다. Redis에 사용해도 좋습니다.  
2)userPointInfo는 포인트를 관리하는 master로서 lock이 필요해보여 @version을 두었습니다.  
중복거래를 판별할수있는 key가 있으면 좋을 것 같습니다.  
3)진입 데이터는 reqDto로 칭하며, spring-validation을 이용한 가벼운 @valid를 걸었습니다.  
4)응답은 CommonResponseDto로 모든 응답은 같은 구조로 응답합니다.(error포함)   
5)서비스에서 금액적으로 validation해야하는 부분이 있어 mode를 'soft', 'hard'로 구분해봤습니다.   
soft는 포인트적립 시 한계이상의 값이 들어와도 한계값까지 채워줍니다. hard는 얄짤없이 튕겨냅니다.  
6)