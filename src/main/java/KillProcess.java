public class KillProcess {
    public static void main(String[] args) {
        try {
            // 要杀死的进程名称（这里以notepad.exe为例）
            String processName = "chromedriver.exe";
            // 执行taskkill命令
            Process process = Runtime.getRuntime().exec("taskkill /F /IM " + processName);
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}