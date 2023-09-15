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

### 페이지 권한 설정
- 상품 등록에 관한 페이지는 관리자(ADMIN) 계정에서만 접속가능
- 일반 (USER) 사용자는 그 외에만 접속 가능

`Authentication EntryPoint` 인터페이스를 구현하여, 인증되지 않은 사용자가 리소스를 요청할 경우, "Unauthorized" 에러를 발생시키도록 함.
SecurityConfig에 `http.exceptionHandling()` 로 예외처리를 수행하도록 핸들러를 구성.

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

### 기본적인 상품 등록, 수정, 관리 가능
쇼핑몰이라면 갖추어야할 상품을 등록하고 수정하며 관리할 수 있는 기능을 모두 구현하였습니다.

### 회원가입 시 이메일 인증 기능 구현
- spring-boot-starter-mail 라이브러리 사용
- application.properties 에 구글 smtp 설정 추가
- mail controller와 mail service 설계
- 마주한 에러와 해결과정
    - `401 error`  

      : 다음과 같이 http.authorizeRequests()에 /mail/과 관련된 경로를 모두 permitAll() 해줌으로써 권한문제 해결
    ```java
    http.authorizeRequests()                        
                    .mvcMatchers("/css/**", "/js/**", "/img/**").permitAll()   
                    .mvcMatchers("/", "/members/**", "/item/**", "/images/**", "/mail/**").permitAll()
    ```
    
    - `403 error` 에러 발생  
    
     : csrf를 disable 함으로써 문제 해결
    ```java
    http.authorizeRequests()                       
                    .mvcMatchers("/css/**", "/js/**", "/img/**").permitAll()  
                    .mvcMatchers("/", "/members/**", "/item/**", "/images/**", "/mail/**").permitAll()
                    .mvcMatchers("/admin/**").hasRole("ADMIN")    
                    .anyRequest().authenticated()
                    .and()
                    .csrf().ignoringAntMatchers("/mail/**") // csrf disable 설정 
            ;
    ```
   
  
(작동 예시)
![1](https://github.com/kkong-ji/my_online_pet_shop_project/assets/87354210/2e1c8dcb-e242-4b80-b6cf-7d00d3c9d82c)
![2](https://github.com/kkong-ji/my_online_pet_shop_project/assets/87354210/a19d841f-cf79-47ff-9c0b-6525392c85d1)
![3](https://github.com/kkong-ji/my_online_pet_shop_project/assets/87354210/b411ab9c-6239-44ff-a17b-af8b7c374988)
![4](https://github.com/kkong-ji/my_online_pet_shop_project/assets/87354210/b3ced86e-b41e-4256-95a3-ddb7466f21c3)
![5](https://github.com/kkong-ji/my_online_pet_shop_project/assets/87354210/3704ab17-e502-4746-99c4-fbe82235e539)

### 로그인 시 회원 정보를 잃어버렸을 경우, 임시 비밀번호 전송하기 기능
https://velog.io/@kjh950330/%ED%9A%8C%EC%9B%90-%EC%9E%84%EC%8B%9C-%EB%B9%84%EB%B0%80%EB%B2%88%ED%98%B8-%EC%A0%84%EC%86%A1-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84

- 마주한 에러와 해결과정
    - SMTP 구문 오류
     : 윈도우 계정을 영어로 바꾸어서 해결
    - 401 error
     : csrf 예외처리 설정을 통해 해결    


## 프로젝트를 통해 느낀점

사실 혼자서 공부를 하거나 강의를 들으며 간단하게 프로젝트를 만들었던 적은 있지만 스스로 모든 것을 기획하며 <br>
구글링과 도서를 참고하며 프로젝트를  만든 것은 처음이다보니 시간이 오래걸리고, 상당히 많은 부분을 오류와 싸우게 되었습니다.
<br>
그러나, 오류를 해결하고 생각했던 대로 정상적으로 기능이 작동하는 것을 볼 때, 스스로도 뿌듯함을 느끼기도 했습니다.
아직 부족한 점이 많고 더욱 배워야할 것들이 많지만 지금의 프로젝트 경험이 자양분이 되어 앞으로 어떤 것을 공부하고자 할 때,
방향성을 주었다고 생각합니다.
<br>
이 프로젝트는 향후 지속적으로 더 많은 기능들을 업데이트하며 추가할 계획이고, 앞으로 어떤 기능을 추가할지는 고민중에 있습니다.

