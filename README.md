```
최범균님 영상을 보면서
하나의 기능을 설계 및 구현할때
시니어가 어떤식으로 생각해나가는지, 구현하는지 
직접 배워보기
```
- https://www.youtube.com/watch?v=7P1dJ-VoQb4&list=PLwouWTPuIjUg5gQBL9ajinVkcX4D8BAkI&index=3
- TDD를 직접 체험하고 익히자
-----
내용
- 전체 학생 회원 승급
- DB의 학년 칼럼값을 1증가

고려사항
- 중간에 실패하면 재시도 가능해야함
- 중간에 실패하면 원래 학년으로 원복할 수 있어야함
-------
flow
- <img width="784" alt="스크린샷 2023-01-24 오후 2 30 59" src="https://user-images.githubusercontent.com/62214428/214219080-ad15ab5f-89cc-4c44-8256-e718288b522b.png">
```
1. 승급 대상 추출
2. 승급 대상자 저장
- 이유 : 승급 실패시 이전 상태 원복을 위해서
3. 에러 x -> 승급 성공
4. 에러 o -> 이전 상태 원복
```

----
- <img width="967" alt="스크린샷 2023-01-24 오후 2 35 26" src="https://user-images.githubusercontent.com/62214428/214219562-2d3fa1e4-3eb7-41a8-abe0-63e15d57864c.png">
```
여기서부터 느껴지는게. 책임에 따라 매우 상세하게 분리한다.... !
1. gradeAdvanceService : 전체적인 승급 시스템 시작 서비스
2. targetGen : 대상자 추출
3. targets : 대상자 저장
4. targetsExpoter : 혹시 모르니 타겟 추출 후 저장용(파일이나 디비에 저장할용도) 


그래서 gradeAdvanceService로 승급 서비스 시작
-> States를 통해 회원들 상태를 전부 확인(승급필요한지)
-> 타겟을 추출하고 저장
-> 타겟 리스트인 targets의 요소들을 돌면서
-> advanceApplier를 통해 승급 시도
-> 결과 생성
-> 승급 실패 혹은 되돌려야하는 대상을 대상으로 revoke
```
