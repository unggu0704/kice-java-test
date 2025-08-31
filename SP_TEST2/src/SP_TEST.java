import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SP_TEST {
    
    static class WorkerPolicy {
        String id;
        int thread_count;
        int processing_ratio;
    }
    
    static class WorkerPolicyConfig {
        List<WorkerPolicy> workers;
    }
    
    static class WorkerResult {
        String worker_id;
        int processed_lines;
        Map<String, Integer> log_levels;
        long processing_time_ms;
    }
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            String logFilePath = scanner.nextLine();
            processWithWorkers(logFilePath);
        }
    }
    
    private static void processWithWorkers(String filePath) throws Exception {
        // TODO: 1. WorkerPolicy.json 로드
        // TODO: 2. 로그 데이터를 비율에 따라 분할
        // TODO: 3. CompletableFuture로 멀티스레드 처리
        // TODO: 4. 각 워커별 결과를 RESULT_<WorkerID>.json에 저장
        // TODO: 5. 통합 결과 콘솔 출력
        

    }
}
