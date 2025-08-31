import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SP_TEST {
    private static final String LOG_PATTERN =
            "\\[\\d{4}-\\d{2}-\\d{2} (\\d{2}):\\d{2}:\\d{2}\\] ([A-Z]+) (\\w+).*";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String logFilePath = scanner.nextLine();
            analyzeLogFile("./SP_TEST1/LOG_DATA.TXT");
        }
    }
    
    private static void analyzeLogFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Log Analysis Result === \n");

        Map<String,Integer> logLevelMap = new HashMap<>();
        Map<String,Integer> serviceMap = new HashMap<>();
        Map<String,Integer> timeMap = new HashMap<>();

        // TODO: 1. 파일 읽기
        // TODO: 2. 정규표현식으로 로그 패턴 파싱: \\[(.*?)\\] (\\w+) (\\w+) (.*)
        // TODO: 3. 레벨별/메서드별/시간별 통계 계산
        // TODO: 4. 결과 출력

        Pattern pattern = Pattern.compile(LOG_PATTERN);
        // 파일을 한 줄씩 읽기
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {      // 한 줄씩 읽음
                Matcher matcher = pattern.matcher(line);
                matcher.matches();

                String time = matcher.group(1);
                String logLevel = matcher.group(2);
                String service = matcher.group(3);

                logLevelMap.put(logLevel, logLevelMap.getOrDefault(logLevel, 0) + 1);
                timeMap.put(time, timeMap.getOrDefault(time, 0) + 1);
                serviceMap.put(service, serviceMap.getOrDefault(service, 0) + 1);
            }
        }

        List<String> logLevelList = new ArrayList<>(logLevelMap.keySet());
        List<String> timeList = new ArrayList<>(timeMap.keySet());
        List<String> serviceList = new ArrayList<>(serviceMap.keySet());

        logLevelList.sort( (i1, i2) -> logLevelMap.get(i2) -logLevelMap.get(i1) );
        timeList.sort( (t1, t2) -> timeMap.get(t2) - timeMap.get(t1) );
        serviceList.sort( (s1, s2) -> serviceMap.get(s2) - serviceMap.get(s1) );

        sb.append("Log Level Statistics:\n");
        for (String key : logLevelList) {
            sb.append(key).append(": ").append(logLevelMap.get(key)).append("\n");
        }
        sb.append("\n");
        int index = 1;
        sb.append("Top 3 Methods: \n");
        for (String key : serviceList) {
            sb.append(index++).append(". ").append(key).append(": ").append(serviceMap.get(key)).append("\n");
        }
        sb.append("\n");
        sb.append("Hourly Distribution:\n");
        for (String key : timeList) {
            sb.append(key).append(": ").append(timeMap.get(key)).append(", ");
        }
        sb.append("\n");

        System.out.println(sb);
    }
}
