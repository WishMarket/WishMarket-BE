# WishMarket
![image](https://user-images.githubusercontent.com/113013495/224330528-670aded4-b5d6-4836-aa7d-9e053b43db52.png)



### 프로젝트 개요 
- 고가의 상품을 선물할 때  펀딩 방식을 통해 개인이 원하는 만큼 모금하여 부담 없이 선물할 수 있는 플랫폼

### 기획배경
  
- 내가 아끼고 좋아하는 사람들을 위해 선물을 주는 것은 너무 기쁜 일이지만, 받는 사람은 선물 가격이 비싸다면 선뜻 의사를 밝히기 어렵고, 선물을 주는 사람 또한 흔한 선물보다는 의미 있는 값진 선물을 주고 싶은 경우가 많습니다.
- 따라서 고가의 상품을 선물할 때 **펀딩 방식**을 통해 참여자 별 부담 가능한 금액을 모금하여 전달하는 플랫폼을 기획하였습니다.

### 해결방법
- 특정 대상(받는 사람)을 지정하여 참여자들의 펀딩 목적을 뚜렷하게 하고, 참여 금액은 자유롭게 설정할 수 있도록 하여 원하는 예산 내에서 선물에 동참할 수 있는 기회를 제공할 것입니다.
- 친구 목록에 등록된 사용자라면 해당 사용자가 받고 싶어하는 선물 목록을 확인한 후, 펀딩을 통해 진행하고자 하는 선물의 선택의 폭을 줄일 수 있는 기회를 제공할 수 있습니다.
- 해당 펀딩이 진행되는 링크를 통해 친구가 아니어도, 사용자들은 펀딩에 참여할 수 있기 때문에 선물을 할 수 있는 기회를 확대할 수 있습니다.

**Demo link**
[프로젝트 데모영상 링크](https://youtu.be/IbfWnYwOlSs)

**팀 노션**
[Gift4U Team Notion](https://www.notion.so/Gift4U-Gift-for-you-dc8c3ba1c8724c41b2a798699a92ac36)


</br>

## 🕰️ 개발 기간
* 23.01.26일 - 23.03.09일 (약 6주)

</br>

## 🛠️ Skills
### Front-End
<img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white"> <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=TypeScript&logoColor=white"> 
<img src="https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=Vite&logoColor=white"> <img src="https://img.shields.io/badge/Recoil-0075EB?style=for-the-badge"> <img src="https://img.shields.io/badge/React Router-CA4245?style=for-the-badge&logo=sasReact Routers&logoColor=white"> <img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=Axios&logoColor=white"> <img src="https://img.shields.io/badge/Scss-cc6699?style=for-the-badge&logo=sass&logoColor=white"> 


### Back-End

<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white">  <img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/spring batch-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=spring security&logoColor=white"> <img src="https://img.shields.io/badge/gradle-2D4999?style=for-the-badge&logo=gradle&logoColor=white">

<img src="https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge"> <img src="https://img.shields.io/badge/QueryDql-7957D5?style=for-the-badge&logo=QueryDql&logoColor=white"> <img src="https://img.shields.io/badge/mariaDB-003545?style=for-the-badge&logo=mariaDB&logoColor=white"> <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"> 

<img src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens"> <img src="https://img.shields.io/badge/Server Sent Event-B5314C?style=for-the-badge&logo=net%20web%20tokens">

### Infrastructure
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white"> <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=Amazon EC2&logoColor=white"> <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=for-the-badge&logo=Amazon RDS&logoColor=white"> <img src="https://img.shields.io/badge/Amazon S3-569A31.svg?style=for-the-badge&logo=Amazon S3&logoColor=white"> <img src="https://img.shields.io/badge/Git Action-181717.svg?style=for-the-badge&logo=Git&logoColor=white">


### Collaboration tool
<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white"> <img src="https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"> <img src="https://img.shields.io/badge/GitHub-4A154B?style=for-the-badge&logo=GitHub&logoColor=white">

</br>

## ⚒️ 프로젝트 주요 기능
<details>
<summary> 회원가입</summary>
<div markdown="1">
<ul>
<li>이메일 중복 확인을 통해 유효한 이메일 주소가 있으면 회원 가입이 가능 </li>
<li>인증 코드 메일을 발송</li>
<li>인증 코드 만료 시간은 3분으로 redis를 통해 관리</li>
<li>회원가입 버튼 클릭 시 인증 코드 검사를 진행하며 정상 통과 시 회원가입이 진행</li>
</ul>
</div>
</details>
<details>
<summary> 로그인</summary>
<div markdown="1">
<ul>
<li>일반 로그인과 SNS 로그인은 JWT 토큰을 기반으로 관리</li>
<li>일반 로그인 : 로그인 성공 시 Access, Refresh Token을 발급하여 로그인 성공을 응답 (Refresh Token의 경우 Redis 를 이용하여 토큰 관리)</li>
<li>OAuth 로그인 : 네이버, 깃허브 계정을 통해 로그인
<ul>
<li>Front 에서 Authorization code 발급받아 Server 로 전달</li>
<li>Authorization code 를 받아 Access Token 및 유저 정보를 요청</li>
<li>응답받은 유저 정보로 Database 를 검색하여 회원 정보 생성 및 수정</li>
<li>로그인 성공 시 Access, Refresh Token을 발급하여 응답</li>
</ul>
</li>
</ul>
</div>
</details>
<details>
<summary> 회원정보 변경 및 회원탈퇴</summary>
<div markdown="1">
<ul>
<li>회원정보 수정
<ul>
<li>닉네임, 주소, 연락처, 프로필 이미지 변경 가능</li>
<li>일반 로그인 사용자와 OAuth 로그인 사용자는 동일한 항목에 대해 정보 변경 가능</li>
<li>OAuth 로그인 사용자는 해당 사이트 계정에서 이름, 프로필이 변경된 경우 자동 적용</li>
</ul>
</li>
<li>회원탈퇴
<ul>
<li>사용자는 회원탈퇴 버튼을 통해 안내사항을 확인 후 탈퇴가능</li>
<li>탈퇴 시 Database 에 등록된 사용자 관련 정보는 모두 삭제</li>
<li>탈퇴 후 사용자는 해당 계정으로 다시 로그인 불가능</li>
</ul>
</li>
</li>
<li>비밀번호 분실 시 비밀번호 변경
<ul>
<li>일반 로그인으로 회원가입한 유저의 경우 비밀번호 분실 시 비밀번호를 변경 가능</li>
<li>가입했던 이메일로 발송된 인증코드를 통해 가입한 계정에 대한 인증을 받고, 변경하고자 하는 비밀번호를 입력하여 비밀번호를 변경</li>
<li>Oauth 로그인 사용자는 이용 불가능</li>
</ul>
</li>
</ul>
</div>
</details>
<details>
<summary> 친구찾기 & 추가</summary>
<div markdown="1">
<ul>
<li>email, name, nickname 타입을 기반으로 회원가입된 전체 유저 목록을 검색</li>
<li>검색 전 인기 유저 추천</li>
<li>검색 후 기준에 맞는 검색 결과가 있으면 검색 결과
<ul>
<li>이미 친구인 경우 친구 삭제 버튼</li>
<li>친구가 아닌 경우 친구 추가 버튼</li>
</ul>
</li>
</li>
<li>검색 결과가 없으면 “검색 결과가 없습니다.” 텍스트 띄움</li>
<li>친구 기능은 단방향으로 추가 가능, 별도의 수락 없음
<ul>
<li>일반 로그인으로 회원가입한 유저의 경우 비밀번호 분실 시 비밀번호를 변경 가능</li>
</ul>
</li>
</ul>
</div>
</details>
<details>
<summary> 친구목록</summary>
<div markdown="1">
<ul>
<li>Access Token을 기반으로 로그인한 유저를 판단하여 해당 유저의 친구 목록을 조회</li>
<li>친구 리스트 hover 시에 삭제 버튼 노출</li>
<li>삭제 버튼 이외 영역 클릭 시 친구의 펀딩 내역, 찜 목록 확인 가능</li>
<li>비로그인 상태에서 클릭하면 로그인 페이지로 연결</li>
</ul>
</div>
</details>
<details>
<summary> 상품</summary>
<div markdown="1">
<ul>
<li>상품 조회 기능
<ul>
<li>카테고리별(의류, 가전제품, 완구, 전자기기, 장신구, 가구, 기타 ) 상품 조회가 가능</li>
<li>매일 바뀌는 오늘의 베스트 상품을 메인 페이지에서 확인 가능</li>
</ul>
</li>
<li>상품 추가 기능
<ul>
<li>(관리자 페이지) 에서 상품 데이터를 추가</li>
</ul>
</li>
<li>기타
<ul>
<li>베스트 상품 데이터는 매일 새벽 2시에 상품의 추천수를 기준으로 업데이트</li>
</ul>
</li>
<li>상품검색
<ul>
<li>Parameter로 전달된 Keyword로 상품을 검색</li>
<li>페이징처리로 한번에 전달되는 상품개수를 제한</li>
</ul>
</li>
</li>
<li>상품 상세정보 조회
<ul>
<li>URL 경로로 전달된 ProductId로 해당 상품에 대한 상세정보 조회</li>
</ul>
</li>
<li>상품 리뷰 조회
<ul>
<li>Parameter로 전달된 ProductId로 해당 상품 리뷰 조회</li>
<li>페이징처리로 한번에 전달되는 상품개수를 제한</li>
</ul>
</li>
</ul>
</div>
</details>
<details>
<summary> 찜목록</summary>
<div markdown="1">
<ul>
<li>찜목록 추가
<ul>
<li>사용자는 원하는 상품을 본인의 찜목록에 자유롭게 추가</li>
</ul>
</li>
<li>찜목록 조회
<ul>
<li>찜목록에 추가한 상품을 조회</li>
<li>친구의 찜목록을 조회하여 친구가 무슨 상품을 좋아하는지 확인 가능</li>
</ul>
</li>
<li>찜목록 삭제
<ul>
<li>찜목록에 추가한 상품은 삭제 가능</li>
</ul>
</li>
</ul>
</div>
</details>
<details>
<summary> 펀딩</summary>
<div markdown="1">
<ul>
<li>펀딩 시작
<ul>
<li>사용자는 원하는 상품, 펀딩 기간, 금액, 펀딩을 받을 대상자를 선택해 펀딩을 시작</li>
<li>사용자는 다른 유저들이 진행중인 펀딩에 추가로 참여</li>
</ul>
</li>
<li>펀딩 조회
<ul>
<li>사용자는 본인이 참여한(시작한) 펀딩목록을 조회</li>
<li>사용자는 본인이 대상자인 펀딩목록을 조회</li>
<li>친구가 참여한 펀딩목록을 조회</li>
<li>메인 페이지에서 인기유저(인플루언서) 유저를 대상으로 진행중인 펀딩들을 조회</li>
</ul>
</li>
<li>펀딩 상세정보 조회
<ul>
<li>펀딩에 참여한 사람들의 수, 유저, 내가 펀딩한 금액, 남은 금액 등 상세정보를 조회</li>
</ul>
</li>
<li>펀딩이 완료된 대상자는 주소와 후기(코멘트)를 남겨 펀딩상품 수령</li>
</ul>
</div>
</details>
<details>
<summary> 포인트</summary>
<div markdown="1">
<ul>
<li>포인트 충전
<ul>
<li>Access Token으로 전달된 UserId로 본인의 포인트를 10000증가</li>
</ul>
</ul>
</div>
</details>
<details>
<summary> 알림</summary>
<div markdown="1">
<ul>
<li>알림 조회
<ul>
<li>Access Token에 포함된 UserId로 본인의 알림목록 조회</li>
</ul>
</li>
<li>알림 읽음
<ul>
<li>URL경로로 전달된 AlarmId로 해당 알람을 읽음 처리</li>
</ul>
</li>
<li>알림 삭제
<ul>
<li>URL경로로 전달된 AlarmId로 해당 알람을 삭제</li>
</ul>
</li>
<li>SSE 기능
<ul>
<li>Access Token으로 전달된 UserId로 본인에 대한 알림이 발생할때 실시간으로 서버에서 알림을 보냄</li>
<li>본인에 대한 읽지 않은 알림개수에 변화가 있을때 실시간으로 서버에서 알림을 보냄</li>
</ul>
</li>
<li>Badge알림
<ul>
<li>Access Token에 포함된 UserId로 본인의 읽지 않은 알림의 개수를 조회</li>
</ul>
</li>
</ul>
</div>
</details>

</br>

## ⚙️ Architecture
### System Architecture
![시스템 아키텍처](https://user-images.githubusercontent.com/79897135/224329187-51fa48a5-2d51-44b7-9d0b-144f67ca6026.png)




### Batch
![배치 구조](https://user-images.githubusercontent.com/79897135/224329205-44fe09be-1f77-4807-b2c9-4d2be27f0e67.png)

<br><br>

## 📄ERD
[ERD-Cloud](https://www.erdcloud.com/d/vLR6JdRk3a6ZdrRhL)

![erd](https://user-images.githubusercontent.com/79897135/224329255-8a880e52-f79c-459a-b033-a7da57703759.png)

</br>

## 🚀Demo
###
|Sign Up|Log In|
|---|---|
|<img src ="https://user-images.githubusercontent.com/111720709/224335202-39ea9f53-863d-427c-9174-a4e83dc4508f.gif" width="100%" height="200"/>|<img src ="https://user-images.githubusercontent.com/111720709/224335433-2ba00579-dff6-4d9e-b11f-62a9cd6a4061.gif" width="100%" height="200"/>|
|Products|Wish List|
|<img src ="https://user-images.githubusercontent.com/111720709/224335558-ece03f08-f32c-4080-ba8c-bc51d77085e2.gif" width="100%" height="200"/>|<img src ="https://user-images.githubusercontent.com/111720709/224335682-6260aa67-9ad1-4a8f-9d91-15122d5a717a.gif" width="100%" height="200"/>|
|Funding Start|Funding Attend|
|<img src ="https://user-images.githubusercontent.com/111720709/224335787-d9ff230d-dd66-4d09-838d-5feaef37d5fd.gif" width="100%" height="200"/>|<img src ="https://user-images.githubusercontent.com/111720709/224335804-e45be96a-e575-41c2-bc22-aeeab5fd4620.gif" width="100%" height="200"/>|
|Find Friends|Friends List|
|<img src ="https://user-images.githubusercontent.com/111720709/224335947-f2aece9e-b356-4534-8cd4-4dd280f913ce.gif" width="100%" height="200"/>|<img src ="https://user-images.githubusercontent.com/111720709/224335958-7ed7a245-f8d2-4b08-b86b-eb8ee26c1afe.gif" width="100%" height="200"/>|
|Profile|Gift|
|<img src ="https://user-images.githubusercontent.com/111720709/224336125-fcaa770a-b23d-462f-b0e8-37bfeeed67d4.gif" width="100%" height="200"/>|<img src ="https://user-images.githubusercontent.com/111720709/224336116-7ba29de9-b1d7-43c6-8ad6-fde5df684599.gif" width="100%" height="200"/>|

</br>


## 🛠️기술 특장점
<details>
<summary> SSE(Sever-Sent-Event)</summary>
<div markdown="1">
<ul>
<li>실시간 알림같은 경우는 서버에서 클라이언트 방향으로만 데이터를 보내면 되기 때문에 단방향 통신인 SSE가 적합하다. </li>
<li>클라이언트에서 처음 HTTP 연결을 맺고 나면 서버는 클라이언트로 계속하여 데이터를 전송할 수 있다.</li>
<li>websocket과 달리 별도의 프로토콜을 사용하지 않고 HTTP 프로토콜만으로 사용이 가능하며 훨씬 가볍다.</li>
<li>접속에 문제가 있으면 자동으로 재연결을 시도한다.</li>
<li>최대 동시 접속 수는 HTTP/1.1의 경우 브라우저 당 6개이며, HTTP/2는 100개까지 가능하다.</li>
<li>IE를 제외한 브라우저에서 지원된다.</li>
</ul>
<img src="https://user-images.githubusercontent.com/112573111/224340927-0dded0ee-db09-4b1a-b373-7a0248f0618a.png" width="400" height="500">
</div>
</details>

<details>
<summary> Spring Batch / Scheduler</summary>
<div markdown="1">
<ul>
<li>스케줄러를 통해 일정 주기마다 데이터를 처리해야 한다.!

<ul>
<li>위 경우 단순 메소드로 구현하기보단 데이터 처리, 자동화(사용자의 개입X), 신뢰성의 이유로 배치로 처리하기에 적합하다.</li>
<li>펀딩의 성공 여부와 같은 민감한 데이터를 처리할 시 특정 문제가 발생 시 아예 동작하지 못하도록 해야 한다. (Step별로 작업 처리)</li>
</ul>
</li>
<li>매 정각마다 펀딩 만료 여부를 확인해야 한다.
<ul>
<li>현재 진행 중인 펀딩 데이터들에 한해서 매 정각(ex 1:00)시간 마다 펀딩 목표 기간이 지난 데이터들에 대해서 펀딩의 상태 값을 FAIL처리를 해준다.</li>
</ul>
</li>
</ul>
</div>
</details>

<details>
<summary> Redis</summary>
<div markdown="1">
<ul>
<li>베스트 상품은 하루를 주기로 업데이트 되기 때문에 하루 동안은 변하지 않는다. 이때 다수의 사용자가 매번 조회하기 보단, Redis를 통해 캐시화 하여 저장하면 많은 유저가 같은 날 조회를 해도 DB가 아닌 바로 Redis에서 값을 가져오므로 처리량이 줄고 효율이 증가한다. </li>
<li>토큰 기반 인증 방식에 Redis를 사용한 Refresh Token를 관리한다.</li>
</ul>
</div>
</details>

<details>
<summary> OAuth2 LOGIN</summary>
<div markdown="1">
<ul>
  <br>
<img src="https://user-images.githubusercontent.com/112573111/224341745-4acbaeec-fb0a-499e-8e06-436b0d6ef35a.png">
<li>[구현 방식]
<ul>
<li>Front 에서 Authorization code 발급받아 Server 로 전달</li>
<li>Authorization code 를 받아 Access Token 및 유저 정보를 요청</li>
<li>응답받은 유저 정보로 Database 를 검색하여 회원 정보 생성 및 수정</li>
<li>로그인 성공 시 WishMarket Server 에서 Access, Refresh Token을 발급하여 응답</li>
</ul>
</li>
<li>네이버, 깃허브 이메일을 통해 로그인</li>
<li>계정의 생성, 인증 등 절차는 해당 OAuth Server 에서 관리</li>
<li>WishMarket Server 에서는 이미 인증된 계정으로 간편하게 로그인 처리만 진행</li>
</ul>
</div>
</details>

<details>
<summary> Recoil</summary>
<div markdown="1">
<ul>
<li>Recoil을 활용하여 전역 상태 관리</li>
<li>비밀번호 찾기 → 비밀번호 변경 페이지</li>
<li>카테고리 하트 버튼 클릭 → 위시리스트 추가</li>
</ul>
</div>
</details>

<details>
<summary> React-datepicker / Daum-Postcode</summary>
<div markdown="1">
<ul>
<li>React-datepicker , Daum-Postcode 등 라이브러리를 이용해 사용자 친화적인 UI / UX</li>
<li>펀딩시작 : React-datepicker</li>
<li>마이페이지 - 정보변경, 받은선물 : Daum-Postcode </li>
</ul>
</div>
</details>

<br>


## 👥 팀원소개
- 원세영(Back-end) : 회원가입, 팔로우, 펀딩 기능 및 배포
- 김선범(Back-end) : 상품검색, 포인트, 알림 기능
- 백동주(Back-end) : 소셜로그인, 유저정보관리 기능
- 고동우(Back-end) : 상품추가, 베스트상품 조회, 위시리스트, 펀딩 기능 및 배포
- 박주경(Front-end) : 상품 조회 및 검색, 헤더, 메인 컴포넌트, 마이 페이지 UI 구현 및 연동, 프론트 배포
- 조혜준(Front-end) : 로그인, 회원가입, 비밀번호변경, 친구검색, 펀딩 시작 & 참여, 메인 인기펀딩 페이지 UI 구현 및 연동
