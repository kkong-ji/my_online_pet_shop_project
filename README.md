# my online pet shop project [댕댕월드] 

스프링부트와 JPA, Security로 구현한 강아지 용품 온라인 쇼핑몰 프로젝트

## 목차
- [개발환경](###개발환경)
- [사용기술](###사용기술)
    - [백엔드](###백엔드)
    - [프론트엔드](###프론트엔드)
 

  
## 개발 환경
- intellij
- Github
- Mysql Workbench

## 사용 기술
### 백엔드
#### 주요 프레임워크 / 라이브러리
- Java 11
- SpringBoot 2.7.1
- SpringBoot Security
- Spring Data JPA
- QueryDsl

#### Build tool
- Maven

#### Database
- MySql

#### Infra
- AWS EC2

#### 프론트엔드
- Html/Css
- Thymeleaf
- Bootstrap5

#### 기타 라이브러리
- Lombok

## 핵심 키워드
- 스프링부트, 스프링 시큐리티, JPA를 사용하여 웹 애플리케이션 전반적인 기획과 배포, 유지 보수 과정을 통해
  백엔드 개발의 전과정 운영 경험
- AWS 기반 무중단 배포 인프라 구축  
- MVC 프레임워크 기반으로 백엔드 서버를 구축


## ERD
![ERD](https://github.com/kkong-ji/my_online_pet_shop_project/assets/87354210/76514909-4f6b-41d6-8b91-87a7a1beb6d9)

## 프로젝트 목적
개인 블로그에도 작성한 내용이지만 계속해서 강의를 듣고 스프링을 학습하면서 단순히 공부만 하기보다는 직접 프로젝트를 기획하고 진행하며 배운 내용을 빠르게 체득하고 싶었습니다.   
이론으로만 접하는 것과 직접 몸으로 부딪히며 문제를 해결해나가며 공부하는 것은 많은 차이가 있다고 생각하기 때문입니다. 그러나, 간단하고 쉬운 프로젝트보다는 지금 내 수준에서 어렵고 복잡한 로직을 가진 프로젝트더라도 진행하면서 많은 것들을 배울 수 있는 것이 좋겠다는 생각을 했습니다.
그러던 중, MVC패턴을 익힐 수 있고, 각 Entity를 설계, JPA를 통해 연관관계 설정, 스프링 시큐리티를 통한 로그인 기능 등. Spring framework의 기능들을 최대로 활용할 수 있는 온라인 쇼핑몰을 만드는 것이 적합하다는 결론에 이르렀고, 지금의 댕댕월드를 구상하여 제작하게 되었습니다.

## 핵심기능

### 소셜 로그인 (google)
- Spring Security
- OAuth2 인증 방식 사용

`WebSecurityConfigurerAdapter` 가 Deprecated 됨에 따라 `SecurityFilterChain` 을 빈으로 등록하는 방식으로 변경  

다음의 로직들로 구현함
1. DefaultOAuth2UserService 를 상속해서 만든 커스텀 OAuth2UserService
2. UserDetails 메서드에 실제 OAuth2User 구현
3. 위의 내용들을 반영하는 SecurityConfig 설정
4. MemberService 에서 loadUserByUsername 메서드를 구현하여 회원을 찾도록 함.
5. MemberController 에서 UserDetail  
    Form 로그인이면 UserDetails,
    OAuth2 로그인이면 OAuth2User 타입으로 반환

(작동 예시)
![image](https://github.com/kkong-ji/my_online_pet_shop_project/assets/87354210/0cfd92fd-4a5d-4858-8352-e91eecff57cc)

### Querydsl을 통해 동적 쿼리 생성
- Qdsl을 학습 후, JPQL 방식이 아닌 Qdsl 방식 채택
- QueryDslPredicateExecutor 인터페이스 상속


