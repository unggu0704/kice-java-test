import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import com.google.gson.*;

public class SP_TEST {
    
    static class ServerInfo {
        String id;
        String url;
        int weight;
    }
    
    static class ServerPool {
        List<ServerInfo> servers;
    }
    
    static class CircuitBreaker {
        private int failureCount = 0;
        private final int threshold = 3;
        private final long timeout = 5000;
        private long lastFailureTime = 0;
        private boolean isOpen = false;
        
        public boolean canExecute() {
            if (!isOpen) return true;
            return (System.currentTimeMillis() - lastFailureTime) > timeout;
        }
        
        public void onSuccess() {
            failureCount = 0;
            isOpen = false;
        }
        
        public void onFailure() {
            failureCount++;
            if (failureCount >= threshold) {
                isOpen = true;
                lastFailureTime = System.currentTimeMillis();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(AnalysisServlet.class, "/analyze");
        server.setHandler(handler);
        
        System.out.println("🚀 KICE Analysis Server starting on port 8080...");
        server.start();
        System.out.println("✅ Server is running! Ready for analysis requests.");
        server.join();
    }
    
    public static class AnalysisServlet extends HttpServlet {
        private static final Map<String, CircuitBreaker> circuitBreakers = new HashMap<>();
        private static int currentServerIndex = 0;
        
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
            
            resp.setContentType("application/json; charset=utf-8");
            
            try {
                // TODO: 1. JSON 요청 파싱
                // TODO: 2. ServerPool.json 로드  
                // TODO: 3. Round-Robin 로드밸런싱
                // TODO: 4. Circuit Breaker 패턴 적용
                // TODO: 5. 내부 서버로 HTTP 요청
                // TODO: 6. 결과 통합 후 JSON 응답
                

                
            } catch (Exception e) {
                resp.setStatus(500);
                JsonObject error = new JsonObject();
                error.addProperty("status", "error");
                error.addProperty("message", e.getMessage());
                resp.getWriter().write(new Gson().toJson(error));
            }
        }
    }
}
