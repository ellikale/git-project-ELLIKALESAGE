import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class GitWrapper {

    /**
     * Initializes a new Git repository.
     * This method creates the necessary directory structure
     * and initial files (index, HEAD) required for a Git repository.
     */
    public void init() {
        try{
            Git.milestone21();
            System.out.println("Repo initialized");
        }
        catch (IOException e) {
            System.out.println("Error initializing repo");
        }

    };

    /**
     * Stages a file for the next commit.
     * This method adds a file to the index file.
     * If the file does not exist, it throws an IOException.
     * If the file is a directory, it throws an IOException.
     * If the file is already in the index, it does nothing.
     * If the file is successfully staged, it creates a blob for the file.
     * @param filePath The path to the file to be staged.
     */
    public void add(String filePath) {
        try{
            File file = new File(filePath);
            if (!file.exists()){
                throw new IOException("File not found " + filePath);
            }
            if (file.isDirectory()){
                throw new IOException("Cannot add a directory directly: " + filePath);
            }
            String hash = Git.hashFile(filePath);
            Git.createBlobFiles(filePath);
            Git.updateIndex(hash, filePath);
            System.out.println("staged " + filePath);
        }
        catch (Exception e) {
            System.out.println("Error adding file: " + e.getMessage());
        }

    }

    /**
     * Creates a commit with the given author and message.
     * It should capture the current state of the repository by building trees based on the index file,
     * writing the tree to the objects directory,
     * writing the commit to the objects directory,
     * updating the HEAD file,
     * and returning the commit hash.
     * 
     * The commit should be formatted as follows:
     * tree: <tree_sha>
     * parent: <parent_sha>
     * author: <author>
     * date: <date>
     * summary: <summary>
     *
     * @param author  The name of the author making the commit.
     * @param message The commit message describing the changes.
     * @return The SHA1 hash of the new commit.
     */
    public String commit(String author, String message) {
        try {
            String rootTreeHash = Git.workToTree();
            if (rootTreeHash == null || rootTreeHash.isEmpty()) {
                System.out.println("Index is empty. Nothing to commit");
                return "";
            }
            String commitHash = Git.createCommit(author, message, rootTreeHash);
            return commitHash;
        } catch (Exception e) {
            System.out.println("error creating commit");
            return "";
        }
    }

     /**
     * EXTRA CREDIT:
     * Checks out a specific commit given its hash.
     * This method should read the HEAD file to determine the "checked out" commit.
     * Then it should update the working directory to match the
     * state of the repository at that commit by tracing through the root tree and
     * all its children.
     *
     * @param commitHash The SHA1 hash of the commit to check out.
     */
     public void checkout(String commitHash) {
        try{
            File commitFile = new File("git/objects", commitHash);
            if (!commitFile.exists()){
                throw new IOException("commit not found");
            }

        
            List<String> lines = Files.readAllLines(commitFile.toPath());
            String treeHash = null;
            // getting the tree hash from commit
            for (String line: lines){
                if (line.startsWith("tree:")){
                    treeHash = line.substring(5).trim();
                    break;
                }
            }

            if (treeHash == null){
                throw new IOException("no tree reference found in commit");
            }

            // want to clear working directory except git/
            File wd = new File(".");
            for (File f: wd.listFiles()){
                if (f.getName().equals("git")){
                    continue;
                }
                deleteRecursively(f);
            }

            restoreTree(treeHash, wd);
        }
        catch (Exception e) {
            System.out.println("error during checkout");
        }

    }



    private void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            for (File ch : f.listFiles()) {
                deleteRecursively(ch);
            }
        }
        Files.deleteIfExists(f.toPath());
    }

    // turns blobs/tree objects into files and folders
    public static void restoreTree(String treeHash, File parentDir) throws IOException {
        File treeFile = new File("git/objects", treeHash);
        if (!treeFile.exists()) {
            System.out.println("Tree object not found: " + treeHash);
            return;
        }

        // Read every line of the tree file
        List<String> lines = Files.readAllLines(treeFile.toPath());
        for (String line : lines) {
            String[] parts = line.split(" ");
            if (parts.length < 3) continue;

            String type = parts[0];   // blob or tree
            String hash = parts[1];
            String name = parts[2];

            if (type.equals("blob")) {
                // Read the blob file and recreate the original file
                File blobFile = new File("git/objects", hash);
                if (blobFile.exists()) {
                    String content = Files.readString(blobFile.toPath());
                    File restoredFile = new File(parentDir, name);
                    Files.writeString(restoredFile.toPath(), content);
                    System.out.println("Restored file: " + restoredFile.getPath());
                } else {
                    System.out.println("Missing blob object: " + hash);
                }
            } else if (type.equals("tree")) {
                File subDir = new File(parentDir, name);
                if (!subDir.exists()) {
                    subDir.mkdirs();
                }
                restoreTree(hash, subDir);
            }
        }
    }


}