# point

1. DB 설계  
   point_info(계정과 연결된 포인트 master)  
   point_item(포인트들)  
   order(주문)  
   point_usage(order에 대한 포인트 사용)  
   point_usage_link(실제 포인트 사용 내역)  
   위와 같이 진행하려던 중 pointKey가 event마다 생성되는걸 확인하여 point_event 테이블을 추가했습니다.  
<br>
2. 
