import java.io.File;

public class Git{
    public static void main(String[] args) {
        
    }

    public static void createGitDirectory(){
        File git = new File("git");
        git.mkdir();
    }

    public static void createObjectsDirectory(){
        File git = new File("git/objects");
        git.mkdir();
    }
}