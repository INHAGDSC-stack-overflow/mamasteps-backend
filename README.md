# Mamasteps-backend

<p align="center"><img src="https://res.cloudinary.com/startup-grind/image/upload/c_fill,dpr_2,f_auto,g_center,q_auto:good/v1/gcs/platform-data-dsc/contentbuilder/GDG-Bevy-ChapterThumbnail.png" height="200px" width="200px"></p>

Project to participate in 2024 google solution challenge

# Backend-member
|Jaegyeong Han|Sungkyu Shin|Gyuhyeok Hwang|
|:-:|:-:|:-:|
|<img alt="h" src="https://github.com/INHAGDSC-stack-overflow/.github/assets/126947828/9ad665a6-dc05-481f-9f70-04d8a6d8b33c" width="100px">|<img width="100" alt="s" src="https://github.com/INHAGDSC-stack-overflow/.github/assets/126947828/8b886570-a9df-4323-a203-b29bdd55673f">|<img src="https://github.com/umc-hackathon-Y/Y-Server/assets/113760409/22148297-a7db-4abd-86cf-952e35e1be61" width="100px" />|
|[@hanjaegyeong](https://github.com/hanjaegyeong)|[@kyu4583](https://github.com/kyu4583)|[@Gyuhyeok99](https://github.com/Gyuhyeok99)|

# About Implementation
## Backend
### 1. Tech Stack
- Java 17
- Spring, Spring boot
- Spring Web MVC, Spring Security
- Spring Data JPA
- MySQL
- AWS(RDS, Elastic beanstalk, s3, route53, ec2)

### 2. Architecture
<img width="841" alt="아키" src="https://github.com/INHAGDSC-stack-overflow/.github/assets/126947828/04685343-84c4-4391-aa56-51799d46ba69">

### 3. ERD

![erd](https://github.com/INHAGDSC-stack-overflow/.github/assets/126947828/79029d03-d54d-42c2-a016-e31ea44006b7)

- We chose MVC pattern as an architecture.
  - Every feature is divided into modules, and each module has its own controller, view, and binding.
- The data layer is divided into models and providers. 
  - The models are used to store data, and the providers are used to communicate with the backend.
