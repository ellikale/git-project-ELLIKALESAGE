import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Git{
    public static void main(String[] args) throws IOException {
        makesEntireGitRepo();
    }

    public static void makesEntireGitRepo() throws IOException{
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

    public static void updateIndex(String hashString, String fileName) throws IOException{
        File indexFile = new File("git", "index");
        Path relativeRootDirPath = Paths.get(System.getProperty("user.dir")).toAbsolutePath(); // used stackoverlow to get pwd in java
        Path filePath = Paths.get(fileName).toAbsolutePath();
        String relativePath = relativeRootDirPath.relativize(filePath).toString(); //used baeldung for relativize
        
        List<String> lines = new ArrayList<>();
        if(indexFile.exists()){
            lines = Files.readAllLines(indexFile.toPath());
        }
        String toAdd = hashString + " " + relativePath;
        
        boolean haveYouUpdated = false;
        boolean alreadyThere = false;
        for (int i = 0; i < lines.size(); i++) {
            String theLine = lines.get(i);
            if(theLine.endsWith(" " + relativePath)){
                //if(!theLine.startsWith(hashString + " ")){
                if(theLine.equals(toAdd)){
                    alreadyThere = true;
                }
                else{
                    lines.set(i, toAdd);
                    haveYouUpdated = true;
                }
                break;
            }
        }
        if(alreadyThere){
            return;
        }
        if(!haveYouUpdated){
            lines.add(toAdd);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile))){
            for (int i = 0; i < lines.size(); i++) {
                bw.write(lines.get(i));
                if(i < lines.size() - 1){
                    bw.newLine();
                }
            }
        }
    }

    public static String treeify(String path) throws Exception{
            File dir = new File(path);
            if(!dir.exists()){
                throw new Exception("broski that is not a real path...");
            }
            List<String> treeList = new ArrayList<>();
            File[] everythingList = dir.listFiles();
            if(everythingList == null){
                everythingList = new File[0];
            }
            for (File file : everythingList) {
                if(file.isFile()){
                    createBlobFiles(file.getAbsolutePath());
                    String blobHash = hashFile(file.getAbsolutePath());
                    Path relativeRootDirPath = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                    Path filePath = file.toPath().toAbsolutePath();
                    String relativePath = relativeRootDirPath.relativize(filePath).toString();
                    treeList.add("blob " + blobHash + " " + relativePath);
                }
                if(file.isDirectory()){
                    String subdirHash = treeify(file.getAbsolutePath());
                    Path relativeRootDirPath = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                    Path filePath = file.toPath().toAbsolutePath();
                    String relativePath = relativeRootDirPath.relativize(filePath).toString();

                    treeList.add("tree " + subdirHash + " " + relativePath);
                }
            }

            String everything = String.join("\n", treeList); // stack overflow

            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = everything.getBytes();
            byte[] hashBytes = digest.digest(bytes);
            String treeHash = makeItHex(hashBytes);

            // do i need to save this tree someplace?

            File treeFile = new File("git/objects", treeHash);
            if(!treeFile.exists()){
                Files.write(treeFile.toPath(), everything.getBytes());
            }
            return treeHash;
        }

    public static void parseNormalize() throws IOException{
        File index = new File("git", "index");
        File workingList = new File("git", "workingList");
        List<String> indexLines = Files.readAllLines(index.toPath());
        List<String> workingLines = new ArrayList<>();
        for (String string : indexLines) {
            workingLines.add("blob " + string);
        }
        Files.write(workingList.toPath(), workingLines);
    }

    public static void sortWorkingList() throws IOException{
        File workingList = new File("git", "workingList");
        List<String> workLines = Files.readAllLines(workingList.toPath());
        boolean didYouSwap = true;
        while(didYouSwap){
            didYouSwap = false;
            for (int i = 0; i < workLines.size() - 1; i++) {
                String firstLine = workLines.get(i);
                String secondLine = workLines.get(i + 1);
                String firstPath = getPathFromWorkingListLine(firstLine);
                String secoPath = getPathFromWorkingListLine(secondLine);
                if(firstPath.compareTo(secoPath) > 0){
                    workLines.set(i, secondLine);
                    workLines.set(i + 1, firstLine);
                    didYouSwap = true;

                }
            }
        }
        Files.write(workingList.toPath(), workLines);
    }

    public static String getPathFromWorkingListLine(String line){
        if(line.length() > 46){
            return line.substring(46);
        }
        return line;
    }

    public static String findLeafMostParentDir(List<String> lines){
        String deepest = null;
        int maxDepth = -1;
        for (String string : lines) {
            String path = getPathFromWorkingListLine(string);
            int depth = 0;
            for (int i = 0; i < path.length(); i++) {
                if(path.charAt(i) == '/'){
                    depth ++;
                }
            }
            if(depth > maxDepth && path.contains("/")){
                maxDepth = depth;
                deepest = path.substring(0, path.lastIndexOf("/")); // used w3 schools
            }
        }
        return deepest;
    }

    public static String workToTree() throws Exception{
        File index = new File("git", "index");
        if(index.length() == 0){
            // Handle empty index by creating an empty tree object
            String emptyContent = "";
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = emptyContent.getBytes();
            byte[] hashBytes = digest.digest(bytes);
            String emptyTreeHash = makeItHex(hashBytes);
            
            File emptyTreeFile = new File("git/objects", emptyTreeHash);
            if(!emptyTreeFile.exists()){
                Files.write(emptyTreeFile.toPath(), emptyContent.getBytes());
            }
            return emptyTreeHash;
        }
        parseNormalize();
        File workingListFile = new File("git", "workingList");
        if(!workingListFile.exists()){
            throw new Exception("brochacho, there is no working list file");
        }
        List<String> lines = Files.readAllLines(workingListFile.toPath());
        String lastTreeHash = null;

        sortWorkingList();
        while(true){
            String leafDir = findLeafMostParentDir(lines);
            if(leafDir == null){
                break;
            }

            List<String> children = new ArrayList<>();
            for (String line : lines) {
                String path = getPathFromWorkingListLine(line);
                if(path.startsWith(leafDir + "/")){
                    String relative = path.substring(leafDir.length() + 1);
                    if(!relative.contains("/")){
                        children.add(line);
                    }
                }
            }

            String treeContents = "";
            for (String string : children) {
                String[] parts = string.split(" "); 
                String type = parts[0];
                String hash = parts[1];
                String path = parts[2];
                String name = path.substring(path.lastIndexOf("/") + 1);
                treeContents = treeContents + type + " " + hash + " " + name + "\n";
            }
            //maybe fixes last line issue, only changed this for testing! yay it worked.
            treeContents = treeContents.substring(0, treeContents.length() - 1);

            File tempFileForHashingPurposesBcIDONTWantToCreateANewHelperMethod = File.createTempFile("tree", ".txt");
            Files.write(tempFileForHashingPurposesBcIDONTWantToCreateANewHelperMethod.toPath(), treeContents.getBytes());


            String treeHash = hashFile(tempFileForHashingPurposesBcIDONTWantToCreateANewHelperMethod.getAbsolutePath());
            tempFileForHashingPurposesBcIDONTWantToCreateANewHelperMethod.delete();

            File treeFile = new File("git/objects", treeHash);
            if(!treeFile.exists()){
                Files.write(treeFile.toPath(), treeContents.getBytes());
                lastTreeHash = treeHash;
            }

            List<String> updatedLines = new ArrayList<>();
            for (String line : lines) {
                String path = getPathFromWorkingListLine(line);
                if(!path.equals(leafDir) && !path.startsWith(leafDir + "/")){
                    updatedLines.add(line);
                }
            }
            updatedLines.add("tree " + treeHash + " " + leafDir);
            lines = updatedLines;
            Files.write(workingListFile.toPath(), lines);
            sortWorkingList();
        }
        //just added this for testing, need to add the final thing
        if(!lines.isEmpty()){
            String treeContents = "";
            for (String string : lines) {
                String[] parts = string.split(" "); 
                String type = parts[0];
                String hash = parts[1];
                String path = parts[2];
                String name = path.substring(path.lastIndexOf("/") + 1);
                treeContents = treeContents + type + " " + hash + " " + name + "\n";
            }
            treeContents = treeContents.substring(0, treeContents.length() - 1);
            File tempFileForHashingPurposesBcIDONTWantToCreateANewHelperMethod = File.createTempFile("tree", ".txt");
            Files.write(tempFileForHashingPurposesBcIDONTWantToCreateANewHelperMethod.toPath(), treeContents.getBytes());


            String treeHash = hashFile(tempFileForHashingPurposesBcIDONTWantToCreateANewHelperMethod.getAbsolutePath());
            tempFileForHashingPurposesBcIDONTWantToCreateANewHelperMethod.delete();

            File treeFile = new File("git/objects", treeHash);
            if(!treeFile.exists()){
                Files.write(treeFile.toPath(), treeContents.getBytes());
            }
            String finalContent = "tree " + treeHash + " (root)";
            Files.write(workingListFile.toPath(), finalContent.getBytes());
            return treeHash;
        }
        workingListFile.delete();
        return "you failed";
    }

    // public static void workToTree(){
    //     parseNormalize();
    //     File workingListFile = new File("git", "workingList");
    //     if(!workingListFile.exists()){
    //         throw new Exception("brochacho, there is no working list file");
    //     }
    //     List<String> lines = Files.readAllLines(workingListFile.toPath());\
    //     sortWL();
    //     String rootHash = buildRecursiveTree("", lines);
    // }

    // public static String buildRecursiveTree(String path, List<String> lines){

    // }
}