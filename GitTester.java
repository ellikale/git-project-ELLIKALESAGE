import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitTester {

    public static void main(String args[]) throws IOException {
    
        /* Your tester code goes here */
            
        // initializing repo
            GitWrapper gw = new GitWrapper();
            gw.init();
            System.out.println("respository initialized; should now see a 'git' folder with 'objects', 'index', and HEAD ");

            // creating test files
            File root = new File("myProgram");
            File inner = new File(root, "inner");
            inner.mkdirs();


            File file1 = new File(root, "hello.txt");
            File file2 = new File(inner, "world.txt");

            Files.writeString(file1.toPath(), "Hello world");
            Files.writeString(file2.toPath(), "nested directory");

        

        // staging files
        gw.add("myProgram/hello.txt");
        gw.add("myProgram/inner/world.txt");
        System.out.println("git/index file should now list both files with their blob hashes; check that new blob files were created in git/objects");

        // commit files
        String firstCommit = gw.commit("John Doe", "Initial commit");
        System.out.println("first commit sha" + firstCommit);
        System.out.println("expect a new commit file and head file should now contain this commit hash");

        // modifying one file and recommit
        Files.writeString(file1.toPath(), "btbtbtb");
        gw.add("myProgram/hello.txt");
        String secondCommit = gw.commit("John Doe", "updated");
        System.out.println("second commit sha " + secondCommit);
        System.out.println("expect a new commit file and if you open it it hsould have a parent line");


        gw.checkout(firstCommit);
        System.out.println("after first checkout expecting the wd to match the state of the first commit (hellotxt should revert to its og content)");

        // verifying restored contents
        String hello = Files.readString(Path.of("myProgram/hello.txt"));
        String world = Files.readString(Path.of("myProgram/inner/world.txt"));
        System.out.println("hello.txt: " + hello);
        System.out.println("world.txt: " + world);

        System.out.println("hellotxt should say hello world and worldtxt should say nested direc");
    }
    
}