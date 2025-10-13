import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class GP41Tester {
    public static void main(String[] args) throws Exception {

        // 1. init repository
        Git.milestone21();

        // 2. create nested folder structure
    
        // File root = new File("Samples");
        // File dirA = new File(root, "A");
        // File dirB = new File(dirA, "B");
        // File dirC = new File(dirB, "C");
        // dirC.mkdirs();

        // File file1 = new File(root, "file1.txt");
        // File file2 = new File(dirA, "file2.txt");
        // File file3 = new File(dirB, "file3.txt");
        // File file4 = new File(dirC, "file4.txt");
        // File file5 = new File(dirC, "file5.txt");

        // Files.writeString(file1.toPath(), "root level file");
        // Files.writeString(file2.toPath(), "file in A");
        // Files.writeString(file3.toPath(), "file in B");
        // Files.writeString(file4.toPath(), "first file in C");
        // Files.writeString(file5.toPath(), "second file in C");

        // System.out.println("created nested files");

        // // 3. Stage all files (adds blobs and updates index)
        // for (File f : List.of(file1, file2, file3, file4, file5)) {
        //     String hash = Git.hashFile(f.getAbsolutePath());
        //     Git.createBlobFiles(f.getAbsolutePath());
        //     Git.updateIndex(hash, f.getAbsolutePath());
        //     System.out.println("Staged " + f.getPath());
        // }

        // // 4. Generate trees from index
        // System.out.println("Building trees");
        // String rootHash = null;
        // try {
        //     // if workToTree returns a String
        //     rootHash = Git.workToTree();
        // } catch (Exception e) {
        //     // if it’s void
        //     Git.workToTree();
        // }

        // // 5. Print root tree hash if available
        // if (rootHash != null) {
        //     System.out.println("Root tree SHA: " + rootHash);
        // } else {
        //     System.out.println("Root tree created (check git/objects for the newest tree file).");
        // }

        // // 6. List objects for manual verification
        // File objectsDir = new File("git/objects");
        // File[] objs = objectsDir.listFiles();
        // if (objs == null || objs.length == 0) {
        //     System.out.println("No objects found.");
        // } else {
        //     System.out.println("Objects in git/objects:");
        //     for (File o : objs) {
        //         System.out.println(" - " + o.getName());
        //     }
        // }

        // System.out.println("Done. Now open your tree files to manually verify structure.");

        GP211Tester.cleanUpTime();
        Git.milestone21();

        // creating nested folders and files
        File root = new File("Samples");
        File sub = new File(root, "Inner");
        sub.mkdirs();
        File file1 = new File(root, "alpha.txt");
        File file2 = new File(sub, "beta.txt");
        Files.writeString(file1.toPath(), "rahh");
        Files.writeString(file2.toPath(), "bnadd");

        // staging the files
        String hash = Git.hashFile(file1.getAbsolutePath());
        Git.createBlobFiles(file1.getAbsolutePath());
        Git.updateIndex(hash, file1.getAbsolutePath());

        String hash2 = Git.hashFile(file2.getAbsolutePath());
        Git.createBlobFiles(file2.getAbsolutePath());
        Git.updateIndex(hash2, file2.getAbsolutePath());

        // building trees
        String rootTreeHash = Git.workToTree();
        System.out.println("rth: " + rootTreeHash);
        String firstCommit = Git.createCommit("BT", "Initial commit", rootTreeHash);
        System.out.println("first c: " + firstCommit);


        //editing one file
        Files.writeString(file2.toPath(), "BTBTBBTBTBTBBTBTBTB");
        // Re-stage the changed file
        String hash3 = Git.hashFile(file2.getAbsolutePath());
        Git.createBlobFiles(file2.getAbsolutePath());
        Git.updateIndex(hash3, file2.getAbsolutePath());

        //  new tree snapshot
        String newRootTree = Git.workToTree();
        System.out.println("new rth: " + newRootTree);

        String secondCommit = Git.createCommit("Ryan", "Updated beta.txt", newRootTree);
        System.out.println("second c created: " + secondCommit);

        System.out.println("\nCheck git/objects for commit files and verify that the second commit lists the first as its parent.");





    }
}
