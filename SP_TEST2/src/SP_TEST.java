import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public class SP_TEST {
    static final String LOG_PATH = "./SP_TEST2/LOG_DATA.TXT";
    private static final String LOG_PATTERN =
            "\\[\\d{4}-\\d{2}-\\d{2} (\\d{2}):\\d{2}:\\d{2}\\] ([A-Z]+) (\\w+).*";
    
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
        Map<String, Integer> log_levels = new HashMap<>();
        long processing_time_ms;
    }
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            String logFilePath = scanner.nextLine();
            processWithWorkers("./SP_TEST2/WorkerPolicy.json");
        }
    }
    
    private static void processWithWorkers(String filePath) throws Exception {
        // TODO: 1. WorkerPolicy.json 로드
        // TODO: 2. 로그 데이터를 비율에 따라 분할
        // TODO: 3. CompletableFuture로 멀티스레드 처리
        // TODO: 4. 각 워커별 결과를 RESULT_<WorkerID>.json에 저장
        // TODO: 5. 통합 결과 콘솔 출력
        StringBuilder sb;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {      // 한 줄씩 읽음
               sb.append(line);
            }
        }

        Gson gson = new Gson();
        List<WorkerPolicy> workers = gson.fromJson(sb.toString(), WorkerPolicyConfig.class).workers;

        List<String> lines = Files.readAllLines(Paths.get(LOG_PATH));
        int lineCount = lines.size(); // 로그 파일 전체 카운트
        int pivot = 0;

        //비동기 작업 시작
        List<CompletableFuture> futures = new ArrayList<>();
        for (WorkerPolicy workerPolicy : workers) {
            // 읽어야할 페이지를 계산
            int chunks = (int) (lineCount * (workerPolicy.processing_ratio / 100.0));
            int endpoint = Math.min(lineCount, pivot + chunks);

            List<String> processingLine = new ArrayList<>();
            for (;pivot < endpoint; pivot++) {
                processingLine.add(lines.get(pivot));
            }

            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> function(workerPolicy.id, processingLine))
                            .thenAccept( workerResult -> {
                                FileWriter fw = null;
                                try {
                                    fw = new FileWriter("./SP_TEST2/" + workerPolicy.id + ".json");
                                    gson.toJson(workerResult, fw);
                                    fw.flush();
                                    fw.close();

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private static WorkerResult function(String workerid, List<String> processingLine) {
        long beforeTime = System.currentTimeMillis();
        Pattern pattern = Pattern.compile(LOG_PATTERN);
        WorkerResult workerResult = new WorkerResult();
        Map<String,Integer> logLevelMap = new HashMap<>();

        for (String line : processingLine) {
            Matcher matcher = pattern.matcher(line);
            matcher.matches();

            String logLevel = matcher.group(2);
            logLevelMap.put(logLevel, logLevelMap.getOrDefault(logLevel, 0) + 1);
        }

        List<String> logLevelList = new ArrayList<>(logLevelMap.keySet());
        logLevelList.sort( (i1, i2) -> logLevelMap.get(i2) -logLevelMap.get(i1) );

        for (String key : logLevelList)
            workerResult.log_levels.put(key, logLevelMap.get(key));

        workerResult.worker_id = workerid;
        workerResult.processed_lines = processingLine.size();
        long afterTime = System.currentTimeMillis();
        workerResult.processing_time_ms = (afterTime - beforeTime)/10;

        return workerResult;
    }
}
