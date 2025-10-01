import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GP211Tester {
    public static void main(String[] args) throws IOException {
        //Git.milestone21();
        // for (int i = 0; i < 6; i++) {
        //     System.out.println("Test " + i + ": ");
        //     repoInitializationTester();
        //     cleanUpTime();
        // }
        //originalTestHash();
        // for (int i = 0; i < 6; i++) {
        //     System.out.println("Test " + i + ": ");
        //     blobTester();
        //     cleanUpTime();
        //     cleanUpTimeForBlob();
        // }
        // indexingTester();
        // cleanUpTime();
        // cleanUpTimeForBlob();
        indexingTester();
        testingModification();
        robustReset();
    }

    public static void repoInitializationTester() throws IOException{
        Git.milestone21();
        boolean ifSuccessful = true;
        File indexFile = new File("git", "index");
        File headFile = new File("git", "HEAD");
        File git = new File("git");
        File objects = new File("git/objects");

        if(!indexFile.exists()){ 
            System.out.println("You did not successfully create the repo, namely the indexFile");
            ifSuccessful = false;
        }
        if(!headFile.exists()){
            System.out.println("You did not successfully create the repo, namely the headFile");
            ifSuccessful = false;
        }
        if(!git.exists()){
            System.out.println("You did not successfully create the repo, namely the git directory");
            ifSuccessful = false;
        }
        if(!objects.exists()){
            System.out.println("You did not successfully create the repo, namely the objects directory");
            ifSuccessful = false;
        }
        if(ifSuccessful){
            System.out.println("You successfully created the repo!");
        }
    }

    public static void cleanUpTime(){
        deleteRecursively(new File("git"));
        System.out.println("All cleaned up buttercup!");
    }

    public static void cleanUpTimeForBlob(){
        deleteRecursively(new File("samples"));
        System.out.println("All cleaned up buttercup!");
    }

    public static void deleteRecursively(File file){
        if(file.exists()){
            if(file.isDirectory()){
                for (File subFile : file.listFiles()) {
                    deleteRecursively(subFile);
                }
            }
            file.delete();
        }
    }

    //returned the same hash, passed for a normal file
    public static void originalTestHash(){
        try{  
            File tempFile = File.createTempFile("tempSpecial", ".txt");
            String specialString = "hi!";
            Files.write(tempFile.toPath(), specialString.getBytes(StandardCharsets.UTF_8));
            String hash = Git.hashFile(tempFile.getAbsolutePath());
            String expectedHash = "3a987acf8cbc1028b7dbc86bd086831151899a2b";
            System.out.println("Regular File Test");
            System.out.println("Expected: " + expectedHash);
            System.out.println("Actual: " + hash);
            if(hash.equalsIgnoreCase(expectedHash)){
                System.out.println("Passed!"); 
            }
            else{
                System.out.println("Failed!"); 
            }
            tempFile.delete();
        }
        catch (IOException e) {
            System.err.println("something happened..." + e.getMessage());
        }
    }

    private static List<File> createSampleFiles() throws IOException {
        File samplesDir = new File("samples");
        if (!samplesDir.exists()) {
            samplesDir.mkdir();
        }
        List<File> files = new ArrayList<>();
        String[] contents = {"my name is bob", "testers are my fav part of cs", "bababooey", "abcedfg hi jk lmao"};
        for (int i = 0; i < contents.length; i++) {
            File file = new File(samplesDir, "sampleFile" + i + ".txt");
            Files.write(file.toPath(), contents[i].getBytes());
            files.add(file);
        }
        return files;
    }

    public static void blobTester() throws IOException {
    Git.milestone21();
    List<File> sampleFiles = createSampleFiles();
    boolean allPassed = true;
    for (File file : sampleFiles) {
        byte[] originalContent = Files.readAllBytes(file.toPath());
        String expectedHash = Git.hashFile(file.getAbsolutePath());
        Git.createBlobFiles(file.getAbsolutePath());
        File blobFile = new File("git/objects", expectedHash);
        if (!blobFile.exists()) {
            System.out.println("The blob file " + file.getName() + "is missing");
            allPassed = false;
		break;
        }
        byte[] blobContent = Files.readAllBytes(blobFile.toPath());
        if (!java.util.Arrays.equals(originalContent, blobContent)) {
            System.out.println("Content inside mismatches for " + file.getName());
            allPassed = false;
        } else {
            System.out.println("Successful!");
        }
    }
    if (allPassed) {
        System.out.println("CONGRATS");
    } else {
        System.out.println("FAILED");
    }
}

    public static void indexingTester() throws IOException{
        Git.milestone21();
        List<File> sampleFiles = createSampleFiles();
        boolean allPassed = true;
        for (File file : sampleFiles) {
            String hash = Git.hashFile(file.getAbsolutePath());
            Git.createBlobFiles(file.getAbsolutePath());
            Git.updateIndex(hash, file.getAbsolutePath());
        }

        List<String> indexStrings = Files.readAllLines(new File("git/index").toPath());
        for (File file : sampleFiles) {

            Path relativeRootDirPath = Paths.get(System.getProperty("user.dir")).toAbsolutePath(); // used stackoverlow to get pwd in java
            Path filePath = file.toPath().toAbsolutePath();
            String relativePath = relativeRootDirPath.relativize(filePath).toString(); //used baeldung for relativize

            String expectedHash = Git.hashFile(file.getAbsolutePath());
            String letshopeSo = expectedHash + " " + relativePath;
            if(!indexStrings.contains(letshopeSo)){
                allPassed = false;
            }
        }
        if (allPassed) {
        System.out.println("CONGRATS");
        } else {
            System.out.println("FAILED");
        }
    }

    public static void robustReset(){
        try{
            File objectsDir = new File("git/objects");
            if(objectsDir.exists() && objectsDir.isDirectory()){
                for (File random : objectsDir.listFiles()) {
                    if(random.isFile()){
                        random.delete();
                    }
                }
            }
            File indexFile = new File("git", "index");
            if(indexFile.exists()){
                Files.write(indexFile.toPath(), new byte[0]);
            }
            File samplesDir = new File("samples");
            if(samplesDir.exists()){
                deleteRecursively(samplesDir);
            }
            System.out.println("SUCCCCCESS");
        }
        catch (Exception e){
            System.out.println("Uh oh... look what happened: " + e.getMessage());
        }
    }

    public static void testingModification() throws IOException{
        File modFile = new File("samples/sampleFile1.txt");
        Files.write(modFile.toPath(), "hi im new content!".getBytes(StandardCharsets.UTF_8));
        String hash = Git.hashFile(modFile.getAbsolutePath());
        Git.createBlobFiles(modFile.getAbsolutePath());
        Git.updateIndex(hash, modFile.getAbsolutePath());
        boolean bababooey = false;
        List<String> indexStrings = Files.readAllLines(new File("git/index").toPath());
        for (String string : indexStrings) {
            if(string.equals(hash + " " + "samples/sampleFile1.txt")){
                bababooey = true;
                break;
            }
        }
        if (bababooey) {
        System.out.println("modification is turnt!");
    } else {
        System.out.println("modification is not working");
    }
    }
}
