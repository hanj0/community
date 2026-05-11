# Community Frontend

웹 커뮤니티 서비스의 프론트엔드. React 19 + TypeScript + Vite 기반.

## 기술 스택

- **프레임워크**: React 19 (함수형 컴포넌트 + Hooks)
- **언어**: TypeScript (strict 모드)
- **빌드 도구**: Vite
- **스타일링**: 순수 CSS (CSS Custom Properties, 네이티브 CSS 중첩 문법)
- **라우팅**: 미도입 → 추가 시 React Router 사용 예정
- **상태 관리**: 미도입 → 추가 시 결정 예정

## 명령어

```bash
npm run dev       # 개발 서버 실행
npm run build     # 타입 체크 후 프로덕션 빌드
npm run lint      # ESLint 실행
npm run preview   # 프로덕션 빌드 미리보기
```

## 디렉토리 구조 (목표)

```
src/
├── assets/           # 이미지, 아이콘 등 정적 자원
├── components/       # 재사용 가능한 UI 컴포넌트
│   ├── common/       # 버튼, 인풋 등 범용 컴포넌트
│   └── layout/       # 헤더, 푸터, 사이드바 등 레이아웃
├── pages/            # 라우트별 페이지 컴포넌트
├── hooks/            # 커스텀 React Hooks
├── api/              # API 호출 함수 모음
├── types/            # 공통 TypeScript 타입/인터페이스
├── utils/            # 순수 유틸리티 함수
├── App.tsx
└── main.tsx
```

> 현재는 초기 세팅 상태. 기능 추가 시 위 구조에 맞게 파일을 배치한다.

## 코딩 컨벤션

### 컴포넌트
- 함수형 컴포넌트만 사용 (클래스 컴포넌트 금지)
- 파일명과 컴포넌트명은 PascalCase: `PostCard.tsx`
- 한 파일에 하나의 컴포넌트를 원칙으로 함

### TypeScript
- `any` 타입 사용 금지
- Props 타입은 인터페이스로 정의: `interface PostCardProps { ... }`
- 불필요한 타입 단언(`as`) 지양

### CSS
- CSS Custom Properties 활용 (`--color-primary` 등)
- 클래스명은 kebab-case: `.post-card`, `.btn-primary`
- 인라인 스타일 사용 금지

### 상태 관리
- 컴포넌트 로컬 상태는 `useState`
- 비동기 데이터 패칭은 `useEffect` 또는 커스텀 훅으로 분리

## 백엔드 연동

- 백엔드 경로: `../backend` (Spring Boot + Gradle)
- API 기본 경로: `/api/*` (예정)
- 개발 시 Vite 프록시 설정으로 CORS 우회 예정

## 주의사항

- `console.log` 커밋 금지
- 미사용 변수/임포트 금지 (TypeScript strict 설정으로 빌드 에러 발생)
- 컴포넌트 추가 시 반드시 `src/components/` 또는 `src/pages/` 하위에 배치
