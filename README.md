# 🏪 편해

편의점 행사상품을 확인하고 상품별 리뷰를 작성할 수 있는 편의점 리뷰 플랫폼입니다.  

자세한 구현 내용은 PR을 통해 확인하실 수 있습니다.

<br>     

## ☁️ 사용 기술 및 환경
![편해_사용기술](https://github.com/cvs-go/cvs-go-server/assets/60397314/dd84aecb-7921-4902-9202-5c64ce4b1295)

Java17, Spring Boot 3.0.0, Gradle, JPA, MariaDB, GitHub Actions, AWS S3, AWS RDS, AWS CodeDeploy

<br>     

## ☁️ 프로젝트 주요 관심사

### 프로젝트 지향점

- RESTful API 원칙 준수
- 지속적인 성능 개선과 리팩토링 진행
- 성능 향상과 DB 부하 감소를 위해 N+1 문제 지양
- 코드 리뷰를 통해 코드 품질 향상과 일관성 유지
- 코드 품질 향상 및 개발 생산성 향상을 위해 테스트 코드 작성
- Spring REST Docs를 통한 API 문서화
- CI/CD를 통한 빌드, 테스트, 배포 자동화
- Scale-out 방식으로 서버를 확장할 수 있도록 서버 상태 최소화
    - JWT 토큰 기반 인증 방식
    - DB Version column을 사용한 Lock
    - DB Unique Constraint 사용

### 코드 컨벤션

- Google code Style 준수
- 코드 품질 향상을 위해 SonarLint 사용

<br>     

## ☁️ API 문서

http://15.164.110.225:8080/docs/api-doc.html

<br>     

## ☁️ Git 브랜치 전략

Git-Flow 브랜치 전략을 사용하여 관리합니다.  
모든 feature 브랜치는 develop 브랜치에서 시작되며 Approve 받은 PR만 develop 브랜치로 merge 됩니다.

- main: 배포 시 사용됩니다.
- develop: PR을 거친 후 승인된 feature 브랜치가 모입니다.
- feature: 기능 개발을 진행할 때 사용됩니다.
- hot-fix: 배포를 진행한 후 발생한 버그를 수정해야 할 때 사용합니다.

<br>     

## ☁️ 테스트

- Mockito 라이브러리의 Mock, Spy 등의 기능을 이용해 테스트 격리성을 확보한 유닛 테스트를 작성하였습니다.
- 테스트 커버리지 80% 이상을 유지하고 있습니다.
- GitHub Actions를 사용해 테스트를 자동화하였습니다.

<br>     

## ☁️ CI / CD

GitHub Actions을 통해 Pull Request(PR)을 생성할 때마다 자동으로 빌드와 테스트가 수행됩니다.  
빌드가 CI 서버에서 성공적으로 완료되면, Workflow가 실행되어 빌드 결과물이 AWS S3 버킷으로 전송됩니다.  
그 후, AWS CodeDeploy를 활용하여 자동으로 배포가 이루어집니다.

<br>     

## ☁️ DB

![편해_DB](https://github.com/cvs-go/cvs-go-server/assets/60397314/5e7a1cdd-15e4-472e-958b-cb28f466a8df)

<br>     

## ☁️ 프로젝트 화면 구성
카카오 오븐 - https://ovenapp.io/project/vY1TlQAx9fS3BdwiFSaHHZtBSybmEYUb#ulWKH
