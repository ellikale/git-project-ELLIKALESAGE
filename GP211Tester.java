import java.io.File;
import java.io.IOException;

public class GP211Tester {
    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 6; i++) {
            System.out.println("Test " + i + ": ");
            repoInitializationTester();
            cleanUpTime();
        }
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
}
