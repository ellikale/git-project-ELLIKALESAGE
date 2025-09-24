import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Git{
    public static void main(String[] args) throws IOException {
        createGitDirectory();
        createObjectsDirectory();
        createIndexDirectory();
        createHEADDirectory();
    }

    public static void createGitDirectory(){
        File git = new File("git");
        git.mkdir();
    }

    public static void createObjectsDirectory(){
        //https://www.baeldung.com/java-file-directory-exists 
        File git = new File("git/objects");
        if(!git.exists()){
            git.mkdir();
        }
    }

    public static void createIndexDirectory() throws IOException{
        File indexFile = new File("git", "index");
        if(!indexFile.exists()){
            indexFile.createNewFile();
        }
    }

    public static void createHEADDirectory() throws IOException{
        File headFile = new File("git", "index");
        if(!headFile.exists()){
            headFile.createNewFile();
        }
    }
}