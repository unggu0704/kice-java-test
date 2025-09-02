# KICE 시스템&솔루션개발 실기형 문제지
## [2025년 정규 차수]

---

## HTTP 기반 로그 분석 서비스

### 개요

**해당 시스템 구현을 통해 요구사항 분석, 파일 처리, 멀티스레드 프로그래밍, HTTP Server/Client 구현, 로드밸런싱 등의 기술역량 및 프로그램 구현 역량을 측정하기 위한 문제입니다.**

본 프로그램은 로그 데이터를 파싱하고 분석한 후, **HTTP API**를 통해 클라이언트에게 서비스를 제공하는 **HTTP 기반 로그 분석 서비스**입니다.

### 기능 요약
- 로그 파일을 읽어 **패턴 파싱** 및 **통계 분석**을 수행한다.
- **멀티스레드**를 활용한 **병렬 처리**로 성능을 향상시킴다.
- **HTTP 서버**를 구축하여 **REST API** 서비스를 제공한다.
- **로드밸런서**를 통한 요청 분산 처리로 **확장성**을 확보한다.

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
- 정규표현식 사용: `\\[(\\d{4}-\\d{2}-\\d{2} (\\d{2}):\\d{2}:\\d{2})\\] ([A-Z]+) (\\w+).*`
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
INFO: 9
ERROR: 4
WARN: 4
DEBUG: 3

Top 3 Methods:
1. UserService: 6
2. OrderService: 5
3. PaymentService: 5

Hourly Distribution:
08: 4, 09: 4, 10: 4, 11: 3, 12: 3, 13: 2
```

**※ 로그 파일 형식정보**
- 파일명: LOG_DATA.TXT (각 소문항 홈 아래)
- 파일 형식: `[YYYY-MM-DD HH:mm:ss] <LEVEL> <METHOD> <MESSAGE>`

#### 평가대상
프로그램 정상 실행, 콘솔 입/출력 결과, 프로그램 종료 없음

---

### 문제 2. 멀티스레드 분산 처리 + JSON 파일 출력 (35점)

위 1번 문항까지 구현된 내용을 기준으로, 아래 사항을 추가로 반영하여 구현하시오.

- **WorkerPolicy.json** 파일에 정의된 워커 정책에 따라 **멀티스레드로 분산 처리**
- 각 워커별 처리 결과를 **JSON 파일로 출력**
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
- 각 워커는 `CompletableFuture`를 사용하여 비동기 병렬 처리
- 모든 워커의 처리가 완료된 후 결과 통합

**※ JSON 파일 출력**
- 각 워커별 결과 파일: `RESULT_<WorkerID>.json` (예: `RESULT_LogWorker1.json`)
- 파일 형식:
```json
{
  "worker_id": "LogWorker1",
  "processed_lines": 8,
  "log_levels": {
    "ERROR": 2,
    "INFO": 3,
    "DEBUG": 1,
    "WARN": 2
  },
  "processing_time_ms": 48
}
```

#### 평가대상
프로그램 정상 실행, 멀티스레드 처리, JSON 파일 출력, 콘솔 통합 결과, 프로그램 종료 없음

---

### 문제 3. HTTP API 서버 + Round-Robin 로드밸런서 (40점)

위 2번 문항까지 구현된 내용을 기준으로, 아래 사항을 추가로 반영하여 **HTTP 기반 로그 분석 API 서버**를 구현하시오.

- **HTTP Server**로 로그 분석 요청을 수신하고 **JSON**으로 결과 응답
- **Round-Robin 로드밸런서**를 구현하여 여러 분석 서버에 요청 분산
- **Jetty** 기반 HTTP 서버/클라이언트 구현

#### 상세설명

**※ HTTP API 서버**
- 포트: **8080**
- Endpoint: `POST /analyze`
- **Jetty Embedded Server** 사용

**※ HTTP 요청 형식**
```json
{
  "request_id": "req_001",
  "log_file": "LOG_DATA.TXT",
  "worker_count": 2
}
```

**※ HTTP 응답 형식**  
```json
{
  "request_id": "req_001",
  "status": "success",
  "analysis_result": {
    "log_levels": {
      "INFO": 9,
      "ERROR": 4,
      "WARN": 4,
      "DEBUG": 3
    },
    "top_methods": [
      "UserService: 6",
      "OrderService: 5", 
      "PaymentService: 5"
    ],
    "hourly_distribution": {
      "08": 4, "09": 4, "10": 4, "11": 3, "12": 3, "13": 2
    }
  },
  "processing_time_ms": 1250
}
```

**※ Round-Robin 로드밸런서**
- `LoadBalancer.json` 파일에서 분석 서버 목록 로드
- **Round-Robin** 방식으로 요청을 각 서버에 순차 분배
- `active: true`인 서버만 사용
- 서버 장애 시 다음 서버로 자동 전환

**※ LoadBalancer.json 형식**
```json
{
  "servers": [
    {
      "id": "AnalysisServer1",
      "url": "http://localhost:9001/process",
      "active": true
    },
    {
      "id": "AnalysisServer2",
      "url": "http://localhost:9002/process", 
      "active": true
    },
    {
      "id": "AnalysisServer3",
      "url": "http://localhost:9003/process",
      "active": false
    }
  ]
}
```

**※ 내부 서버 통신 (Jetty HttpClient 사용)**
- 로드밸런서가 선택한 서버로 HTTP POST 요청 전송
- 요청 형식:
```json
{
  "log_data": "로그 파일 전체 내용",
  "analysis_type": "full"
}
```

- 응답 형식:
```json
{
  "result": {
    "log_levels": {...},
    "top_methods": [...],
    "hourly_distribution": {...}
  },
  "server_id": "AnalysisServer1",
  "processing_time": 1200
}
```

#### 평가대상
프로그램 정상 실행, HTTP 서버 구동 (8080포트), JSON 요청/응답, Round-Robin 로드밸런싱, 프로그램 종료 없음

---

## 구현 가이드

### 필수 기술 스택

1. **파일 처리**: 로그 파일 파싱, JSON 파일 읽기/쓰기
2. **정규표현식**: 로그 패턴 매칭
3. **멀티스레드**: `CompletableFuture`를 활용한 병렬 처리
4. **HTTP 서버**: **Jetty Embedded Server** 구현
5. **HTTP 클라이언트**: **Jetty HttpClient** 구현
6. **로드밸런싱**: **Round-Robin** 알고리즘
7. **JSON 처리**: **Gson** 라이브러리 활용

### 제공 파일 구조
```
SP_TEST1/
├── LOG_DATA.TXT
├── COMPARE/
│   └── CMP_CONSOLE.TXT
└── src/
    └── SP_TEST.java

SP_TEST2/
├── LOG_DATA.TXT
├── WorkerPolicy.json
├── COMPARE/
│   └── CMP_CONSOLE.TXT
└── src/
    └── SP_TEST.java

SP_TEST3/
├── LOG_DATA.TXT
├── LoadBalancer.json
├── MOCK_CLIENT.EXE
├── lib/
│   ├── gson-2.10.1.jar
│   ├── jetty-server-9.4.53.v20231009.jar
│   ├── jetty-client-9.4.53.v20231009.jar
│   ├── jetty-servlet-9.4.53.v20231009.jar
│   ├── jetty-http-9.4.53.v20231009.jar
│   ├── jetty-io-9.4.53.v20231009.jar
│   ├── jetty-util-9.4.53.v20231009.jar
│   └── javax.servlet-api-3.1.0.jar
└── src/
    └── SP_TEST.java
```

### 실행 및 테스트

**문항 1, 2:**
```bash
# SP_TEST 실행
C:\>SP_TEST
LOG_DATA.TXT         # 콘솔 입력
=== Log Analysis Result ===  # 결과 출력
...
```

**문항 3:**
```bash
# 1. SP_TEST 실행 (HTTP 서버 구동)
C:\>SP_TEST
🚀 HTTP Server starting on port 8080...
✅ Server is running! Ready for requests.

# 2. HTTP 요청 테스트
curl -X POST http://localhost:8080/analyze \
  -H "Content-Type: application/json" \
  -d '{"request_id":"test_001","log_file":"LOG_DATA.TXT","worker_count":2}'

# 3. MOCK_CLIENT.EXE로 자동 테스트
C:\>MOCK_CLIENT.EXE
테스트를 시작합니다...
✅ HTTP 서버 연결 성공
✅ JSON 요청/응답 확인
✅ 로드밸런서 동작 확인
모든 테스트에 성공했습니다!
```

---

## 핵심 구현 패턴

### 1. HTTP 서버 구축 (Jetty)
```java
public static void main(String[] args) throws Exception {
    Server server = new Server(8080);
    ServletHandler handler = new ServletHandler();
    handler.addServletWithMapping(AnalysisServlet.class, "/analyze");
    server.setHandler(handler);
    
    System.out.println("🚀 HTTP Server starting on port 8080...");
    server.start();
    System.out.println("✅ Server is running! Ready for requests.");
    server.join();
}
```

### 2. Round-Robin 로드밸런서
```java
public class LoadBalancer {
    private List<ServerInfo> activeServers = new ArrayList<>();
    private int currentIndex = 0;
    
    public ServerInfo getNextServer() {
        if (activeServers.isEmpty()) return null;
        
        ServerInfo server = activeServers.get(currentIndex);
        currentIndex = (currentIndex + 1) % activeServers.size();
        return server;
    }
    
    public void loadServers(String configPath) {
        // LoadBalancer.json 파일 읽어서 active=true인 서버만 추가
    }
}
```

### 3. HTTP 서블릿 구현
```java
public class AnalysisServlet extends HttpServlet {
    private LoadBalancer loadBalancer = new LoadBalancer();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws IOException {
        
        // 1. JSON 요청 파싱
        Gson gson = new Gson();
        AnalysisRequest request = gson.fromJson(
            new InputStreamReader(req.getInputStream()), 
            AnalysisRequest.class
        );
        
        // 2. 로드밸런서에서 서버 선택
        ServerInfo server = loadBalancer.getNextServer();
        
        // 3. 내부 서버로 HTTP 요청
        String result = callInternalServer(server, request);
        
        // 4. JSON 응답
        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().write(result);
    }
}
```

### 4. HTTP 클라이언트 (Jetty)
```java
private String callInternalServer(ServerInfo server, AnalysisRequest request) {
    HttpClient client = new HttpClient();
    try {
        client.start();
        
        String jsonData = gson.toJson(createInternalRequest(request));
        ContentResponse response = client.POST(server.getUrl())
            .header(HttpHeader.CONTENT_TYPE, "application/json")
            .content(new StringContentProvider(jsonData))
            .send();
            
        return response.getContentAsString();
        
    } catch (Exception e) {
        // 다음 서버로 자동 전환
        return tryNextServer(request);
    } finally {
        try { client.stop(); } catch (Exception e) {}
    }
}
```

### 5. 멀티스레드 처리
```java
List<CompletableFuture<WorkerResult>> futures = new ArrayList<>();

for (WorkerPolicy worker : workers) {
    CompletableFuture<WorkerResult> future = CompletableFuture.supplyAsync(() -> {
        // 워커별 로그 분석 수행
        return processLogData(worker, logLines);
    });
    futures.add(future);
}

// 모든 워커 완료 대기
CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
```

---

## 평가 기준

- **기능 완성도** (50%): 요구사항에 따른 정확한 구현
- **HTTP 통신** (25%): REST API 서버/클라이언트 구현
- **로드밸런싱** (15%): Round-Robin 알고리즘 구현
- **코드 품질** (10%): 예외 처리, 가독성

---

## 주요 학습 목표

✅ **파일 처리 및 정규표현식** 활용  
✅ **멀티스레드** 병렬 처리 구현  
✅ **Jetty** 기반 HTTP 서버/클라이언트 구현  
✅ **Round-Robin** 로드밸런싱 알고리즘  
✅ **REST API** 설계 및 JSON 처리  
✅ **시스템 아키텍처** 설계 능력  

*본 시험은 실무 환경에서 요구되는 HTTP 서비스 개발 및 분산 시스템 역량을 종합적으로 평가합니다.*
