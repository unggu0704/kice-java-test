# KICE 시스템&솔루션개발 실기형 문제지
## [2025년 정규 차수]

---

## 분산 로그 분석 시스템

### 개요

**해당 시스템 구현을 통해 요구사항 분석, 멀티스레드 프로그래밍, HTTP Server/Client 구현, 로드밸런싱, 파일 처리 등의 기술역량 및 프로그램 구현 역량을 측정하기 위한 문제입니다.**

본 프로그램은 대용량 로그 데이터를 **분산 처리**하여 실시간으로 분석하고, 다양한 형태(콘솔, 파일, HTTP API)로 결과를 제공하는 **분산 로그 분석 시스템**입니다.

### 기능 요약
- 클라이언트가 로그 분석 요청하면 먼저 로그 데이터에 대한 '로그 파싱'을 수행한다.
- **멀티스레드**를 활용하여 '로그 분석'을 **분산 처리**한다.
- 분석 결과를 다양한 형태(콘솔 출력, 파일 저장, HTTP API 응답)로 제공한다.

### 주의사항

실행 결과로 평가하고 부분점수는 없으므로 아래사항을 필히 주의해야 함

- 구현된 프로그램은 실행 완결성 필수 (명확한 실행&정확한 결과 출력, 통상의 실행 시간)
- 소 문항별 결과 검수 필수 (선행문항 오류 시, 후속문항 전체에 오류가 발생할 수 있음)
- 제시된 조건이 없는 한 선행요구사항 유지 필수
- 프로그램 실행 위치 및 실행결과출력 (위치, 파일명, 데이터포맷)은 요구사항과 정확히 일치 필수
- 제시된 모든 위치는 상대경로 사용 필수 (프로그램 실행 위치 기준)
- 프로그램 종료조건에 맞는 처리 필수
- 제공되는 샘플 파일과 다른 데이터로 채점하므로 제공되는 파일의 내용을 하드코딩하지 말 것
- 모든 문자는 요구사항에 맞는 대소문자 구분 필수

---

## 문제

아래 제시된 문항은 문항번호가 증가할수록 점진적 개선을 요구하는 방식으로 구성되어 있으며, 제시된 문항번호 별로 각각 구현된 소스와 컴파일 된 실행파일을 제출하시오.

### 문제 1. 로그 파일 파싱 및 기본 통계 분석 (25점)

콘솔 입력을 통해서 **로그 파일 경로**를 입력하면 해당 로그 파일을 읽어와 **로그 패턴 파싱**을 수행하고 **기본 통계**를 출력하시오.

#### 상세설명

**※ 로그 패턴 파싱**
- 로그 파일의 각 라인을 다음 패턴으로 파싱: `[timestamp] level method message`
- 예시: `[2024-12-30 14:23:45] ERROR UserService Login failed for user: admin`

**※ 기본 통계**
- 로그 레벨별 개수 (INFO, WARN, ERROR, DEBUG)
- 가장 많이 호출된 메서드 TOP 3
- 시간대별 로그 분포 (시간대: 00~23시)

#### 형식정보

**※ 콘솔 입/출력**
- 입력 포맷: `<로그 파일 경로>`
- 출력 포맷:
```
=== Log Analysis Result ===
Log Level Statistics:
INFO: 150
WARN: 45
ERROR: 23
DEBUG: 82

Top 3 Methods:
1. UserService: 45
2. PaymentService: 32
3. OrderService: 28

Hourly Distribution:
00: 5, 01: 2, 02: 1, ..., 23: 12
```

**※ 로그 파일 형식정보**
- 파일명: LOG_DATA.TXT (각 소문항 홈 아래)
- 파일 형식: `[YYYY-MM-DD HH:mm:ss] <LEVEL> <METHOD> <MESSAGE>`

#### 평가대상
프로그램 정상 실행, 콘솔 입/출력 결과, 프로그램 종료 없음

---

### 문제 2. 멀티스레드 분산 처리 + 파일 출력 (35점)

위 1번 문항까지 구현된 내용을 기준으로, 아래 사항을 추가로 반영하여 구현하시오.

- **WorkerPolicy.json** 파일에 정의된 워커 정책에 따라 **멀티스레드로 분산 처리**
- 각 워커별 처리 결과를 **개별 파일로 출력**
- **전체 통합 결과**를 콘솔에 출력

#### 상세설명

**※ 워커 정책**
```json
{
  "workers": [
    {
      "id": "LogWorker1",
      "thread_count": 2,
      "processing_ratio": 40
    },
    {
      "id": "LogWorker2", 
      "thread_count": 3,
      "processing_ratio": 60
    }
  ]
}
```

**※ 분산 처리 로직**
- 전체 로그를 `processing_ratio` 비율에 따라 각 워커에게 분할 할당
- 각 워커는 `thread_count`만큼의 스레드로 병렬 처리
- 모든 워커의 처리가 완료된 후 결과 통합

**※ 파일 출력**
- 각 워커별 결과 파일: `RESULT_<WorkerID>.json`
- 파일 형식:
```json
{
  "worker_id": "LogWorker1",
  "processed_lines": 120,
  "log_levels": {
    "INFO": 60,
    "ERROR": 12,
    "WARN": 18,
    "DEBUG": 30
  },
  "processing_time_ms": 1250
}
```

#### 평가대상
프로그램 정상 실행, 멀티스레드 처리, 파일 출력, 콘솔 통합 결과, 프로그램 종료 없음

---

### 문제 3. HTTP API 서버 + 로드밸런싱 + Circuit Breaker (40점)

위 2번 문항까지 구현된 내용을 기준으로, 아래 사항을 추가로 반영하여 **분산 로그 분석 API 서버**를 구현하시오.

- **HTTP Server**로 로그 분석 요청을 수신하고 결과를 응답
- **서버 풀 관리**를 통한 로드밸런싱 구현
- **Circuit Breaker 패턴** 적용으로 장애 전파 방지

#### 상세설명

**※ HTTP API 서버**
- 포트: 8080
- Endpoint: `POST /analyze`
- 요청 형식:
```json
{
  "request_id": "req_001",
  "log_files": ["LOG_DATA.TXT", "LOG_DATA2.TXT"],
  "analysis_type": "full|summary",
  "worker_policy": "parallel|sequential"
}
```

- 응답 형식:
```json
{
  "request_id": "req_001",
  "status": "success|error",
  "total_processed": 500,
  "analysis_result": {
    "log_levels": {...},
    "top_methods": [...],
    "hourly_distribution": {...}
  },
  "processing_time_ms": 2340,
  "worker_details": [...]
}
```

**※ 로드밸런싱 + Circuit Breaker**
- **ServerPool.json**에 정의된 분석 서버들에 대한 Round-Robin 로드밸런싱
- 각 서버별 Circuit Breaker 적용 (실패 임계치: 3회, 타임아웃: 5초)

**※ ServerPool.json 형식**
```json
{
  "servers": [
    {
      "id": "AnalysisServer1",
      "url": "http://localhost:9001/process",
      "weight": 2
    },
    {
      "id": "AnalysisServer2", 
      "url": "http://localhost:9002/process",
      "weight": 3
    }
  ]
}
```

**※ 내부 서버 요청/응답 형식**
- 내부 서버 요청:
```json
{
  "log_data": "processed_log_content",
  "analysis_config": {...}
}
```

- 내부 서버 응답:
```json
{
  "result": "analysis_result_data",
  "processing_time": 1200
}
```

#### 평가대상
프로그램 정상 실행, HTTP 서버 동작, 로드밸런싱, Circuit Breaker, JSON 처리, 프로그램 종료 없음

---

## 구현 가이드

### 필수 구현 사항

1. **멀티스레드 처리**: `CompletableFuture` 또는 `Thread`를 활용한 병렬 처리
2. **파일 I/O**: JSON 파일 읽기/쓰기, 로그 파일 파싱
3. **HTTP 통신**: Jetty를 활용한 서버/클라이언트 구현
4. **로드밸런싱**: Round-Robin 또는 Weighted Round-Robin 구현
5. **Circuit Breaker**: 장애 감지 및 자동 복구 로직
6. **JSON 처리**: Gson을 활용한 직렬화/역직렬화

### 제공 파일 구조
```
SP_TEST1/
├── LOG_DATA.TXT
└── src/
    └── SP_TEST.java

SP_TEST2/
├── LOG_DATA.TXT
├── WorkerPolicy.json
└── src/
    └── SP_TEST.java

SP_TEST3/
├── LOG_DATA.TXT
├── LOG_DATA2.TXT
├── WorkerPolicy.json
├── ServerPool.json
├── lib/
│   ├── gson-2.10.1.jar
│   ├── jetty-server-9.4.53.v20231009.jar
│   ├── jetty-client-9.4.53.v20231009.jar
│   └── ...
└── src/
    └── SP_TEST.java
```

### 실행 및 테스트

**문항 1, 2:**
- `SP_TEST` 실행 후 콘솔 입/출력 결과 확인
- 파일 출력 결과 확인 (문항 2)

**문항 3:**
- `SP_TEST` 실행 후 HTTP 서버 구동 확인
- `MOCK_CLIENT.EXE` 실행하여 API 테스트 수행
- 모든 테스트 시나리오 성공 시: `"모든 테스트에 성공했습니다!"`

---

## 평가 기준

- **기능 완성도** (60%): 요구사항에 따른 정확한 구현
- **멀티스레드 처리** (15%): 안전한 병렬 처리 및 동기화
- **성능 및 안정성** (15%): Circuit Breaker, 예외 처리
- **코드 품질** (10%): 가독성, 유지보수성

---

*본 시험은 실무 환경에서 요구되는 분산 시스템 개발 역량을 종합적으로 평가합니다.*