import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SP_TEST {
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            String logFilePath = scanner.nextLine();
            analyzeLogFile(logFilePath);
        }
    }
    
    private static void analyzeLogFile(String filePath) throws IOException {
        System.out.println("=== Log Analysis Result ===");
        
        // TODO: 1. 파일 읽기
        // TODO: 2. 정규표현식으로 로그 패턴 파싱: \\[(.*?)\\] (\\w+) (\\w+) (.*)
        // TODO: 3. 레벨별/메서드별/시간별 통계 계산
        // TODO: 4. 결과 출력
        
        /* 구현 힌트:
        Map<String, Integer> levelStats = new HashMap<>();
        Map<String, Integer> methodStats = new HashMap<>();
        Map<Integer, Integer> hourStats = new HashMap<>();
        
        Pattern pattern = Pattern.compile("\\[(.*?)\\] (\\w+) (\\w+) (.*)");
        */
    }
}
