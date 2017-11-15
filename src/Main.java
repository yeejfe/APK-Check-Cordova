import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static String codePath = new File(".").getAbsolutePath();

    private static String[] command = new String[] { "java", "-jar",
            codePath.substring(0, codePath.length() - 1) + "apktool.jar", "-f", "d", "" };

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                run(args[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Usage: java -jar checkCordova.jar <target.apk>");
            System.out.println("apktool.jar has to be in the current directory!");
        }
    }

    private static void run(String apkPath) throws IOException, InterruptedException {
        if (checkFileExists(apkPath)) {
            runApktool(apkPath);
        } else {
            System.out.println("Invalid File path!");
        }
    }

    private static boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    private static void runApktool(String apkPath) throws IOException, InterruptedException {
        command[5] = apkPath;

        String fileName = new File(apkPath).getName();
        fileName = fileName.substring(0, fileName.length() - 4);

        Process p = Runtime.getRuntime().exec(command);

        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = "";
        while ((s = in.readLine()) != null) {
            System.out.println(s);
        }
        int status = p.waitFor();
        if (status == 0) {
            checkForCordovaFiles(fileName);
        } else {
            System.out.println("Is APK tool in current directory?");
        }
        System.out.println("Exited with status: " + status);
    }

    private static void checkForCordovaFiles(String folderName) {
        String currentDir = new File(".").getAbsolutePath();
        currentDir = currentDir.substring(0, currentDir.length() - 1);

        File file = new File(currentDir + folderName + "\\assets\\www\\cordova.js");

        boolean isCordova = false;
        if (file.exists()) {
            isCordova = true;
        }

        System.out.println();
        System.out.println("Cordova Application: " + isCordova);
    }
}
