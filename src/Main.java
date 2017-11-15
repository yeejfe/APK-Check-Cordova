import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
        System.out.println();
        System.out.println("Exited with status: " + status);
    }

    private static void checkForCordovaFiles(String folderName) {
        String currentDir = new File(".").getAbsolutePath();
        currentDir = currentDir.substring(0, currentDir.length() - 1);
        String dirPath = currentDir + folderName + "\\assets\\www";

        File file = new File(dirPath + "\\cordova.js");

        boolean isCordova = false;
        if (file.exists()) {
            isCordova = true;
        }

        System.out.println();
        System.out.println("Cordova Application: " + isCordova);
        
        if(isCordova){
            checkWhetherEncrypted(dirPath);
        }
    }

    private static void checkWhetherEncrypted(String dirPath) {
        System.out.println("Searching in " + dirPath + " ..." );
        ArrayList<String> fileList = (ArrayList<String>) getFileNames(new ArrayList<String>(), Paths.get(dirPath));
        System.out.println("Number of Encrypted html files: " + fileList.size());
        
        System.out.println("====Encrypted HTML Files====");
        
        for(String fileName : fileList){
            System.out.println(fileName);
        }
    }

    private static List<String> getFileNames(List<String> fileNames, Path dir) {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    getFileNames(fileNames, path);
                } else {
                    String fileName = path.getFileName().toString();
                    if(fileName.endsWith(".html") || fileName.endsWith(".htm")){                      
                        if(isFileEncrypted(path)){
                            fileNames.add(path.toAbsolutePath().toString());
                        }     
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }
    
    private static boolean isFileEncrypted(Path path) {
        Document initialDoc;
        try {
            initialDoc = Jsoup.parse(new File(path.toString()), "UTF-8", "");

            Jsoup.connect("http://validator.w3.org/check")
                    .data("fragment", initialDoc.html())
                    .data("st", "1")
                    .post();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
