import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        File git = new File("git");
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

    public static String hashFile(String filePath){    
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath))){
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while((bytesRead = bufferedInputStream.read(buffer)) != -1){
                digest.update(buffer, 0, bytesRead);
            }
            byte[] hashBytes = digest.digest();
            return makeItHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-1 is unavailable right now.");
        }catch (FileNotFoundException e) {
            System.err.println("That file does not exist");
        }catch (IOException e) {
            System.err.println("Cannot read the file!");
        }
        return null;
    }

    public static String makeItHex(byte[] hashers){
        //used stackoverflow for help
        char[] hexArrayFormat = "0123456789ABCDEF".toCharArray();
        char[] hexskis = new char[hashers.length *2];
        for(int j = 0; j < hashers.length; j++){
            int i = hashers[j] & 0xFF;
            hexskis[j * 2] = hexArrayFormat[i >>> 4];
            hexskis[j * 2 + 1] = hexArrayFormat[i & 0x0F];
        }
        return new String(hexskis);
    }

    public static void createBlobFiles(String filePath) throws IOException{
	byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
	String hash = hashFile(filePath);
	File blobFile = new File("git/objects", hash);
        if (!blobFile.exists()) {
            Files.write(blobFile.toPath(), fileBytes);
        }
}

}