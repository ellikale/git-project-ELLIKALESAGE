import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class GP211Tester {
    public static void main(String[] args) throws IOException {
        // for (int i = 0; i < 6; i++) {
        //     System.out.println("Test " + i + ": ");
        //     repoInitializationTester();
        //     cleanUpTime();
        // }
        originalTestHash();
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
}
