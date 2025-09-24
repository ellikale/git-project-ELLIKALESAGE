import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Git{
    public static void main(String[] args) throws IOException {
        milestone21();
    }

    public static void milestone21() throws IOException{
        createGitDirectory();
        createObjectsDirectory();
        createIndexDirectory();
        createHEADDirectory();
        System.out.println("Git Repository Created");
    }

    public static void originalCheck(){
        File indexFile = new File("git", "index");
        File headFile = new File("git", "HEAD");
        File git = new File("git/objects");
        File objects = new File("git/objects");
        if(indexFile.exists() && headFile.exists() && git.exists() && objects.exists()){
            System.out.println("Git Repository Already Exists");
        }
    }

    public static void createGitDirectory(){
        File git = new File("git");
        if(!git.exists()){
            git.mkdir();
        }
    }

    public static void createObjectsDirectory(){
        //https://www.baeldung.com/java-file-directory-exists 
        File objects = new File("git/objects");
        if(!objects.exists()){
            objects.mkdir();
        }
    }

    public static void createIndexDirectory() throws IOException{
        File indexFile = new File("git", "index");
        if(!indexFile.exists()){
            indexFile.createNewFile();
        }
    }

    public static void createHEADDirectory() throws IOException{
        File headFile = new File("git", "HEAD");
        if(!headFile.exists()){
            headFile.createNewFile();
        }
    }
}