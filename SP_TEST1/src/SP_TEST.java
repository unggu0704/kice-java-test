import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

        // 파일을 한 줄씩 읽기
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {      // 한 줄씩 읽음

            }
        }
    }
}
