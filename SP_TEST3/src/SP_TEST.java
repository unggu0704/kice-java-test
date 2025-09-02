import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import com.google.gson.*;

public class SP_TEST {

    // 서버 정보 클래스
    static class ServerInfo {
        String id;
        String url;
        boolean active;
    }

    // 로드밸런서 설정 클래스
    static class LoadBalancerConfig {
        List<ServerInfo> servers;
    }

    // HTTP 요청 클래스
    static class AnalysisRequest {
        String request_id;
        String log_file;
        int worker_count;
    }

    // HTTP 응답 클래스
    static class AnalysisResponse {
        String request_id;
        String status;
        AnalysisResult analysis_result;
        long processing_time_ms;
    }

    // 분석 결과 클래스
    static class AnalysisResult {
        Map<String, Integer> log_levels;
        List<String> top_methods;
        Map<String, Integer> hourly_distribution;
    }

    // Round-Robin 로드밸런서
    static class LoadBalancer {
        private List<ServerInfo> activeServers = new ArrayList<>();
        private int currentIndex = 0;

        public void loadServers(String configPath) throws IOException {
            // TODO: LoadBalancer.json 파일을 읽어서 active=true인 서버만 activeServers에 추가
            // 힌트: Gson을 사용하여 JSON 파일을 LoadBalancerConfig 객체로 변환
            Gson gson = new Gson();
            Reader reader = new FileReader("./SP_TEST3/LoadBalancer.json");

            LoadBalancerConfig loadBalancerConfig = gson.fromJson(reader, LoadBalancerConfig.class);

            for (ServerInfo serverInfo : loadBalancerConfig.servers) {
                if (serverInfo.active)
                    activeServers.add(serverInfo);
            }
        }

        public ServerInfo getNextServer() {
            currentIndex++;

            if (currentIndex > activeServers.size()) {
                currentIndex = currentIndex % activeServers.size();
            }

            return activeServers.get(currentIndex);
        }
    }

    public static void main(String[] args) throws Exception {
        // TODO: Jetty HTTP 서버를 8080 포트에서 시작
        // 힌트:
        // 1. Server 객체 생성
        // 2. ServletHandler 생성 및 AnalysisServlet을 "/analyze" 경로에 매핑
        // 3. 서버 시작
        Server server = new Server(8080); // 8080 포트로 Jetty 서버 인스턴스 생성

        ServletHandler handler = new ServletHandler(); // 서블릿 핸들러 준비
        handler.addServletWithMapping(AnalysisServlet.class, "/"); // "/hello" 경로에 서블릿 매핑
        server.setHandler(handler); // 서버에 핸들러 등록

        server.start(); // 서버 시작
        server.join();  // 메인 스레드 대기(서버 종료까지)
        System.out.println("🚀 HTTP Log Analysis Server starting on port 8080...");

        System.out.println("✅ Server is running! Ready for analysis requests.");

    }

    public static class AnalysisServlet extends HttpServlet {
        private static LoadBalancer loadBalancer = new LoadBalancer();
        private static final String LOG_PATTERN =
                "\\[\\d{4}-\\d{2}-\\d{2} (\\d{2}):\\d{2}:\\d{2}\\] ([A-Z]+) (\\w+).*";

        static {
            try {
                loadBalancer.loadServers("LoadBalancer.json");
            } catch (IOException e) {
                System.err.println("❌ Failed to load LoadBalancer.json: " + e.getMessage());
            }
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {

            resp.setContentType("application/json; charset=utf-8");
            long startTime = System.currentTimeMillis();

            try {
                // TODO: 1. JSON 요청 파싱
                // 힌트: Gson을 사용하여 req.getInputStream()을 AnalysisRequest 객체로 변환
                Gson gson = new Gson();
                AnalysisRequest analysisRequest = gson.fromJson(new InputStreamReader(req.getInputStream()), AnalysisRequest.class);


                // TODO: 2. 로그 파일 읽기
                // 힌트: request.log_file 경로의 파일을 읽어서 문자열로 변환
                List<String> lines = Files.readAllLines(Paths.get(analysisRequest.log_file));  // 로그 읽기

                // TODO: 3. 로드밸런서에서 다음 서버 선택
                // 힌트: loadBalancer.getNextServer() 호출
                ServerInfo serverInfo = loadBalancer.getNextServer();


                // TODO: 4. 로그 분석 수행 (1, 2번 문제에서 구현한 로직 활용)
                // 힌트: performAnalysis(logData) 메서드 구현
                performAnalysis(lines);

                // TODO: 5. HTTP 응답 객체 생성
                // 힌트: AnalysisResponse 객체 생성 후 필드 설정


                // TODO: 6. JSON으로 응답
                // 힌트: Gson을 사용하여 응답 객체를 JSON 문자열로 변환 후 resp.getWriter().write() 호출


                System.out.println("✅ Analysis completed for: [request_id]");

            } catch (Exception e) {
                System.err.println("❌ Analysis failed: " + e.getMessage());

                resp.setStatus(500);
                JsonObject error = new JsonObject();
                error.addProperty("status", "error");
                error.addProperty("message", e.getMessage());
                resp.getWriter().write(new Gson().toJson(error));
            }
        }

        // TODO: 로그 파일 읽기 메서드 구현
        private String readLogFile(String filePath) throws IOException {
            // 힌트: BufferedReader를 사용하여 파일을 한 줄씩 읽어서 StringBuilder에 추가

            return null;
        }

        // TODO: 로그 분석 수행 메서드 구현
        private AnalysisResult performAnalysis(List<String> lines) {
            // 힌트:
            // 1. 정규표현식을 사용하여 각 라인에서 시간, 레벨, 메서드 추출
            // 2. Map을 사용하여 통계 집계
            // 3. AnalysisResult 객체에 결과 설정

            AnalysisResult result = new AnalysisResult();
            result.log_levels = new HashMap<>();
            result.top_methods = new ArrayList<>();
            result.hourly_distribution = new HashMap<>();

            Pattern pattern = Pattern.compile(LOG_PATTERN);

            // TODO: 로그 데이터를 줄별로 분할하여 처리

            // TODO: 각 줄에 대해 정규표현식 매칭 수행

            // TODO: 통계 데이터 집계 (log_levels, methods, hours)

            // TODO: TOP 3 메서드 계산하여 result.top_methods에 추가

            return result;
        }

        // 선택사항: 실제 내부 서버 호출 (고급 구현)
        private String callInternalServer(ServerInfo server, String logData) {
            HttpClient client = new HttpClient();
            try {
                client.start();

                // TODO: 내부 서버로 HTTP POST 요청 전송
                // 힌트:
                // 1. JSON 요청 데이터 생성 {"log_data": logData, "analysis_type": "full"}
                // 2. HttpClient를 사용하여 server.url로 POST 요청
                // 3. 응답 문자열 반환

                HttpClient httpClient = new HttpClient();
                httpClient.start();

                String data = String.format("{\"log_data\":\"%s\",\"analysis_type\":\"full\"}");
                ContentResponse contentResponse = httpClient.POST("localhost")
                        .header(HttpHeader.CONTENT_TYPE, "application/json")                  // Content-Type 헤더 지정
                        .content(new StringContentProvider(data), "application/json")          // 요청 바디에 JSON 데이터 설정
                        .send();

                return contentResponse.getContentAsString(); // 임시 반환값

            } catch (Exception e) {
                System.err.println("❌ Failed to call internal server: " + e.getMessage());

                // TODO: 다음 서버로 자동 전환 (선택사항)

                throw new RuntimeException("Server call failed");
            } finally {
                try {
                    client.stop();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }
}