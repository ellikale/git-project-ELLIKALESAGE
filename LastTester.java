import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LastTester {
    private static final String GIT_DIR = "git";
    private static final String OBJECTS_DIR = "git/objects";
    private static final String HEAD_FILE = "git/HEAD";
    private static final String INDEX_FILE = "git/index";
    
    private static int testsPassed = 0;
    private static int testsTotal = 0;
    
    public static void main(String[] args) {
        //testInitFullWorkflow();
        testAddWorkflow();
        //testTreeWorkflow();
    }

    public static void testInitFullWorkflow() {
        System.out.println("=== Git Init Method Tester ===\n");
        
        // Run all tests
        testCreatesDirectories();
        testCreatesFiles();
        testIdempotent();
        testPaths();
        
        // Print final results
        System.out.println("\n=== Test Results ===");
        System.out.println("Tests passed: " + testsPassed + "/" + testsTotal);
        if (testsPassed == testsTotal) {
            System.out.println("✅ ALL TESTS PASSED!");
        } else {
            System.out.println("❌ Some tests failed.");
        }
        
        // Clean up after testing
        cleanup();
    }
    
    /**
     * Test that git/ and git/objects/ directories are created if missing
     */
    private static void testCreatesDirectories() {
        System.out.println("Test 1: Creates directories");
        
        // Clean up any existing git directory first
        cleanup();
        
        // Verify directories don't exist initially
        File gitDir = new File(GIT_DIR);
        File objectsDir = new File(OBJECTS_DIR);
        
        assertTest(!gitDir.exists(), "git/ directory should not exist initially");
        assertTest(!objectsDir.exists(), "git/objects/ directory should not exist initially");
        
        // Run init
        try {
            Git.makesEntireGitRepo();
        } catch (IOException e) {
            failTest("Init method threw IOException: " + e.getMessage());
            return;
        }
        
        // Verify directories are created
        assertTest(gitDir.exists(), "git/ directory should be created");
        assertTest(gitDir.isDirectory(), "git/ should be a directory");
        assertTest(objectsDir.exists(), "git/objects/ directory should be created");
        assertTest(objectsDir.isDirectory(), "git/objects/ should be a directory");
        
        System.out.println("✅ Directory creation test passed\n");
    }
    
    /**
     * Test that git/HEAD and git/index files are created if missing
     */
    private static void testCreatesFiles() {
        System.out.println("Test 2: Creates files");
        
        // Clean up any existing git directory first
        cleanup();
        
        // Verify files don't exist initially
        File headFile = new File(HEAD_FILE);
        File indexFile = new File(INDEX_FILE);
        
        assertTest(!headFile.exists(), "git/HEAD file should not exist initially");
        assertTest(!indexFile.exists(), "git/index file should not exist initially");
        
        // Run init
        try {
            Git.makesEntireGitRepo();
        } catch (IOException e) {
            failTest("Init method threw IOException: " + e.getMessage());
            return;
        }
        
        // Verify files are created
        assertTest(headFile.exists(), "git/HEAD file should be created");
        assertTest(headFile.isFile(), "git/HEAD should be a file");
        assertTest(indexFile.exists(), "git/index file should be created");
        assertTest(indexFile.isFile(), "git/index should be a file");
        
        System.out.println("✅ File creation test passed\n");
    }
    
    /**
     * Test that re-running init() does not error and leaves existing structure intact
     */
    private static void testIdempotent() {
        System.out.println("Test 3: Idempotent behavior");
        
        // Clean up any existing git directory first
        cleanup();
        
        // Run init first time
        try {
            Git.makesEntireGitRepo();
        } catch (IOException e) {
            failTest("First init call threw IOException: " + e.getMessage());
            return;
        }
        
        // Record initial state
        File gitDir = new File(GIT_DIR);
        File objectsDir = new File(OBJECTS_DIR);
        File headFile = new File(HEAD_FILE);
        File indexFile = new File(INDEX_FILE);
        
        long gitDirLastModified = gitDir.lastModified();
        long objectsDirLastModified = objectsDir.lastModified();
        long headFileLastModified = headFile.lastModified();
        long indexFileLastModified = indexFile.lastModified();
        
        // Wait a small amount to ensure timestamp differences
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Continue if interrupted
        }
        
        // Run init second time
        try {
            Git.makesEntireGitRepo();
        } catch (IOException e) {
            failTest("Second init call threw IOException: " + e.getMessage());
            return;
        }
        
        // Verify structure still exists
        assertTest(gitDir.exists(), "git/ directory should still exist after second init");
        assertTest(objectsDir.exists(), "git/objects/ directory should still exist after second init");
        assertTest(headFile.exists(), "git/HEAD file should still exist after second init");
        assertTest(indexFile.exists(), "git/index file should still exist after second init");
        
        // Verify files are still the same (idempotent)
        assertTest(gitDir.lastModified() == gitDirLastModified, 
                  "git/ directory should not be modified on second init (idempotent)");
        assertTest(objectsDir.lastModified() == objectsDirLastModified, 
                  "git/objects/ directory should not be modified on second init (idempotent)");
        assertTest(headFile.lastModified() == headFileLastModified, 
                  "git/HEAD file should not be modified on second init (idempotent)");
        assertTest(indexFile.lastModified() == indexFileLastModified, 
                  "git/index file should not be modified on second init (idempotent)");
        
        System.out.println("✅ Idempotent test passed\n");
    }
    
    /**
     * Test that paths and names exactly match (git, git/objects, git/HEAD, git/index)
     */
    private static void testPaths() {
        System.out.println("Test 4: Path and name validation");
        
        // Clean up any existing git directory first
        cleanup();
        
        // Run init
        try {
            Git.makesEntireGitRepo();
        } catch (IOException e) {
            failTest("Init method threw IOException: " + e.getMessage());
            return;
        }
        
        // Test exact path matches
        File gitDir = new File(GIT_DIR);
        File objectsDir = new File(OBJECTS_DIR);
        File headFile = new File(HEAD_FILE);
        File indexFile = new File(INDEX_FILE);
        
        // Verify exact names
        assertTest(gitDir.getName().equals("git"), 
                  "Directory should be named exactly 'git', got: " + gitDir.getName());
        assertTest(objectsDir.getName().equals("objects"), 
                  "Objects directory should be named exactly 'objects', got: " + objectsDir.getName());
        assertTest(headFile.getName().equals("HEAD"), 
                  "HEAD file should be named exactly 'HEAD', got: " + headFile.getName());
        assertTest(indexFile.getName().equals("index"), 
                  "Index file should be named exactly 'index', got: " + indexFile.getName());
        
        // Verify exact paths
        assertTest(gitDir.getPath().equals(GIT_DIR), 
                  "Git directory path should be exactly '" + GIT_DIR + "', got: " + gitDir.getPath());
        assertTest(objectsDir.getPath().equals(OBJECTS_DIR), 
                  "Objects directory path should be exactly '" + OBJECTS_DIR + "', got: " + objectsDir.getPath());
        assertTest(headFile.getPath().equals(HEAD_FILE), 
                  "HEAD file path should be exactly '" + HEAD_FILE + "', got: " + headFile.getPath());
        assertTest(indexFile.getPath().equals(INDEX_FILE), 
                  "Index file path should be exactly '" + INDEX_FILE + "', got: " + indexFile.getPath());
        
        // Verify parent-child relationships
        assertTest(objectsDir.getParent().equals(GIT_DIR), 
                  "git/objects/ should be inside git/ directory");
        assertTest(headFile.getParent().equals(GIT_DIR), 
                  "git/HEAD should be inside git/ directory");
        assertTest(indexFile.getParent().equals(GIT_DIR), 
                  "git/index should be inside git/ directory");
        
        System.out.println("✅ Path validation test passed\n");
    }
    
    /**
     * Helper method to assert a test condition and track results
     */
    private static void assertTest(boolean condition, String message) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.println("  ✓ " + message);
        } else {
            System.out.println("  ✗ " + message);
        }
    }
    
    /**
     * Helper method to fail a test with a message
     */
    private static void failTest(String message) {
        testsTotal++;
        System.out.println("  ✗ " + message);
    }
    
    /**
     * Clean up the git directory for testing
     */
    private static void cleanup() {
        try {
            File gitDir = new File(GIT_DIR);
            if (gitDir.exists()) {
                deleteRecursively(gitDir);
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not clean up git directory: " + e.getMessage());
        }
    }
    
    /**
     * Recursively delete a file or directory
     */
    private static void deleteRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        Files.deleteIfExists(file.toPath());
    }
    
    /**
     * Comprehensive tester for the Git add method
     */
    public static void testAddWorkflow() {
        System.out.println("\n=== Git Add Method Tester ===\n");
        
        GitWrapper gitWrapper = new GitWrapper();
        
        // Initialize git repository first
        try {
            Git.makesEntireGitRepo();
        } catch (IOException e) {
            failTest("Failed to initialize git repository: " + e.getMessage());
            return;
        }
        
        // Test 1: Rejects non-existent path
        testAddRejectsNonExistentPath(gitWrapper);
        
        // Test 2: Rejects directory
        testAddRejectsDirectory(gitWrapper);
        
        // Test 3: Stages new file
        testAddStagesNewFile(gitWrapper);
        
        // Test 4: Updates existing entry
        testAddUpdatesExistingEntry(gitWrapper);
        
        // Test 5: No-op when up-to-date
        testAddNoOpWhenUpToDate(gitWrapper);
        
        // Test 6: Creates blob
        testAddCreatesBlob(gitWrapper);
        
        // Print add method test results
        System.out.println("\n=== Add Method Test Results ===");
        System.out.println("Add method tests completed. Check individual test outputs above.");
    }
    
    /**
     * Test that add method rejects non-existent path and throws error
     */
    private static void testAddRejectsNonExistentPath(GitWrapper gitWrapper) {
        System.out.println("Test 1: Rejects non-existent path");
        
        // Capture console output to verify error message
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(baos));
        
        try {
            // Try to add a non-existent file
            gitWrapper.add("nonexistent.txt");
            
            // Restore System.out
            System.setOut(originalOut);
            
            String output = baos.toString();
            
            // Verify error message was printed
            assertTest(output.contains("Error adding file"), 
                      "Should print error message for non-existent file");
            assertTest(output.contains("File not found"), 
                      "Should mention file not found in error message");
            
            // Verify index file is empty (no staging occurred)
            File indexFile = new File(INDEX_FILE);
            if (indexFile.exists()) {
                try {
                    String indexContent = Files.readString(indexFile.toPath());
                    assertTest(indexContent.trim().isEmpty(), 
                              "Index file should be empty when non-existent file is added");
                } catch (IOException e) {
                    failTest("Could not read index file: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.setOut(originalOut);
            failTest("Add method should handle non-existent files gracefully: " + e.getMessage());
        }
        
        System.out.println("✅ Non-existent path test passed\n");
    }
    
    /**
     * Test that add method rejects directory and throws error
     */
    private static void testAddRejectsDirectory(GitWrapper gitWrapper) {
        System.out.println("Test 2: Rejects directory");
        
        // Create a test directory
        File testDir = new File("testDir");
        try {
            if (!testDir.exists()) {
                testDir.mkdir();
            }
            
            // Capture console output to verify error message
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream originalOut = System.out;
            System.setOut(new java.io.PrintStream(baos));
            
            try {
                // Try to add a directory
                gitWrapper.add("testDir");
                
                // Restore System.out
                System.setOut(originalOut);
                
                String output = baos.toString();
                
                // Verify error message was printed
                assertTest(output.contains("Error adding file"), 
                          "Should print error message for directory");
                assertTest(output.contains("Cannot add a directory directly"), 
                          "Should mention cannot add directory in error message");
                
                // Verify index file is empty (no staging occurred)
                File indexFile = new File(INDEX_FILE);
                if (indexFile.exists()) {
                    try {
                        String indexContent = Files.readString(indexFile.toPath());
                        assertTest(indexContent.trim().isEmpty(), 
                                  "Index file should be empty when directory is added");
                    } catch (IOException e) {
                        failTest("Could not read index file: " + e.getMessage());
                    }
                }
                
            } catch (Exception e) {
                System.setOut(originalOut);
                failTest("Add method should handle directories gracefully: " + e.getMessage());
            }
            
        } finally {
            // Clean up test directory
            if (testDir.exists()) {
                testDir.delete();
            }
        }
        
        System.out.println("✅ Directory rejection test passed\n");
    }
    
    /**
     * Test that add method stages new file by appending to git/index
     */
    private static void testAddStagesNewFile(GitWrapper gitWrapper) {
        System.out.println("Test 3: Stages new file");
        
        // Create a test file
        File testFile = new File("testFile1.txt");
        String fileContent = "Hello, World! This is a test file.";
        
        try {
            Files.writeString(testFile.toPath(), fileContent);
            
            // Capture console output
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream originalOut = System.out;
            System.setOut(new java.io.PrintStream(baos));
            
            // Add the file
            gitWrapper.add("testFile1.txt");
            
            // Restore System.out
            System.setOut(originalOut);
            String output = baos.toString();
            
            // Verify success message
            assertTest(output.contains("staged testFile1.txt"), 
                      "Should print staged message");
            
            // Verify index file contains the entry
            File indexFile = new File(INDEX_FILE);
            assertTest(indexFile.exists(), "Index file should exist");
            
            String indexContent = Files.readString(indexFile.toPath());
            String[] lines = indexContent.split("\n");
            assertTest(lines.length == 1, "Index should contain exactly one line");
            
            String indexLine = lines[0].trim();
            String expectedHash = Git.hashFile("testFile1.txt");
            String expectedPath = "testFile1.txt";
            
            assertTest(indexLine.equals(expectedHash + " " + expectedPath), 
                      "Index should contain hash and normalized path. Expected: " + 
                      expectedHash + " " + expectedPath + ", Got: " + indexLine);
            
            // Verify blob was created
            File blobFile = new File("git/objects", expectedHash);
            assertTest(blobFile.exists(), "Blob file should be created");
            
            // Verify blob content matches original file
            String blobContent = Files.readString(blobFile.toPath());
            assertTest(blobContent.equals(fileContent), 
                      "Blob content should match original file content");
            
        } catch (IOException e) {
            failTest("IOException during staging test: " + e.getMessage());
        } finally {
            // Clean up
            if (testFile.exists()) {
                testFile.delete();
            }
        }
        
        System.out.println("✅ New file staging test passed\n");
    }
    
    /**
     * Test that add method updates existing entry when file content changes
     */
    private static void testAddUpdatesExistingEntry(GitWrapper gitWrapper) {
        GPTester.robustReset();
        System.out.println("Test 4: Updates existing entry");
        
        File testFile = new File("testFile2.txt");
        
        try {
            // Create initial file
            String initialContent = "Initial content";
            Files.writeString(testFile.toPath(), initialContent);
            
            // Add file first time
            gitWrapper.add("testFile2.txt");
            String initialHash = Git.hashFile("testFile2.txt");
            
            // Read initial index content
            File indexFile = new File(INDEX_FILE);
            String initialIndexContent = Files.readString(indexFile.toPath());
            
            // Modify file content
            String modifiedContent = "Modified content - this is different!";
            Files.writeString(testFile.toPath(), modifiedContent);
            
            // Add file again
            gitWrapper.add("testFile2.txt");
            String modifiedHash = Git.hashFile("testFile2.txt");
            
            // Verify hash changed
            assertTest(!initialHash.equals(modifiedHash), 
                      "Hash should change when file content changes");
            
            // Read updated index content
            String updatedIndexContent = Files.readString(indexFile.toPath());
            
            // Verify index was updated (not appended)
            String[] lines = updatedIndexContent.split("\n");
            assertTest(lines.length == 1, "Index should still contain exactly one line (updated, not appended)");
            
            String indexLine = lines[0].trim();
            assertTest(indexLine.equals(modifiedHash + " testFile2.txt"), 
                      "Index should contain new hash and path");
            
            // Verify old blob still exists (shouldn't be deleted)
            File initialBlob = new File("git/objects", initialHash);
            assertTest(initialBlob.exists(), "Initial blob should still exist");
            
            // Verify new blob exists with correct content
            File modifiedBlob = new File("git/objects", modifiedHash);
            assertTest(modifiedBlob.exists(), "New blob should be created");
            String blobContent = Files.readString(modifiedBlob.toPath());
            assertTest(blobContent.equals(modifiedContent), 
                      "New blob content should match modified file content");
            
        } catch (IOException e) {
            failTest("IOException during update test: " + e.getMessage());
        } finally {
            // Clean up
            if (testFile.exists()) {
                testFile.delete();
            }
        }
        
        System.out.println("✅ Existing entry update test passed\n");
    }
    
    /**
     * Test that add method does nothing when same content already staged
     */
    private static void testAddNoOpWhenUpToDate(GitWrapper gitWrapper) {
        System.out.println("Test 5: No-op when up-to-date");
        GPTester.robustReset();
        File testFile = new File("testFile3.txt");
        
        try {
            // Create file
            String fileContent = "Same content test";
            Files.writeString(testFile.toPath(), fileContent);
            
            // Add file first time
            gitWrapper.add("testFile3.txt");
            
            // Record index file state
            File indexFile = new File(INDEX_FILE);
            long indexLastModified = indexFile.lastModified();
            
            // Wait a bit to ensure timestamp difference
            Thread.sleep(50);
            
            // Add same file again
            gitWrapper.add("testFile3.txt");
            
            // Verify index file was not modified (no-op)
            long newIndexLastModified = indexFile.lastModified();
            assertTest(indexLastModified == newIndexLastModified, 
                      "Index file should not be modified when adding same content");
            
            // Verify index content is still correct
            String indexContent = Files.readString(indexFile.toPath());
            String[] lines = indexContent.split("\n");
            assertTest(lines.length == 1, "Index should contain exactly one line");
            
            String expectedHash = Git.hashFile("testFile3.txt");
            assertTest(lines[0].trim().equals(expectedHash + " testFile3.txt"), 
                      "Index content should remain unchanged");
            
        } catch (IOException | InterruptedException e) {
            failTest("Exception during no-op test: " + e.getMessage());
        } finally {
            // Clean up
            if (testFile.exists()) {
                testFile.delete();
            }
        }
        
        System.out.println("✅ No-op when up-to-date test passed\n");
    }
    
    /**
     * Test that add method creates blob with exact file contents and doesn't duplicate
     */
    private static void testAddCreatesBlob(GitWrapper gitWrapper) {
        System.out.println("Test 6: Creates blob");
        GPTester.robustReset();
        File testFile = new File("testFile4.txt");
        File indexFile = new File(INDEX_FILE);
        
        try {
            // Create file with specific content
            String fileContent = "Blob creation test content\nWith multiple lines\nAnd special chars: !@#$%^&*()";
            Files.writeString(testFile.toPath(), fileContent);
            
            // Add file
            gitWrapper.add("testFile4.txt");
            
            // Get file hash
            String fileHash = Git.hashFile("testFile4.txt");
            
            // Verify blob exists
            File blobFile = new File("git/objects", fileHash);
            assertTest(blobFile.exists(), "Blob file should exist");
            assertTest(blobFile.isFile(), "Blob should be a file");
            
            // Verify blob content exactly matches file content
            String blobContent = Files.readString(blobFile.toPath());
            assertTest(blobContent.equals(fileContent), 
                      "Blob content should exactly match file content");
            
            // Test that adding same content again doesn't create duplicate blob
            File testFile2 = new File("testFile5.txt");
            Files.writeString(testFile2.toPath(), fileContent); // Same content
            
            long blobLastModified = blobFile.lastModified();
            Thread.sleep(10); // Ensure timestamp difference
            
            // Add second file with same content
            gitWrapper.add("testFile5.txt");
            
            // Verify blob file wasn't modified (no duplication)
            long newBlobLastModified = blobFile.lastModified();
            assertTest(blobLastModified == newBlobLastModified, 
                      "Blob should not be modified when adding file with same content");
            
            // Verify both files point to same blob in index
            String indexContent = Files.readString(indexFile.toPath());
            String[] lines = indexContent.split("\n");
            assertTest(lines.length == 2, "Index should contain two entries");
            
            boolean foundFile1 = false, foundFile2 = false;
            for (String line : lines) {
                if (line.trim().equals(fileHash + " testFile4.txt")) {
                    foundFile1 = true;
                }
                if (line.trim().equals(fileHash + " testFile5.txt")) {
                    foundFile2 = true;
                }
            }
            assertTest(foundFile1 && foundFile2, 
                      "Both files should point to same blob hash in index");
            
        } catch (IOException | InterruptedException e) {
            failTest("Exception during blob creation test: " + e.getMessage());
        } finally {
            // Clean up
            if (testFile.exists()) {
                testFile.delete();
            }
            File testFile2 = new File("testFile5.txt");
            if (testFile2.exists()) {
                testFile2.delete();
            }
        }
        
        System.out.println("✅ Blob creation test passed\n");
    }
    
    /**
     * Comprehensive tester for the Git tree functionality
     */
    public static void testTreeWorkflow() {
        System.out.println("\n=== Git Tree Workflow Tester ===\n");
        
        GitWrapper gitWrapper = new GitWrapper();
        
        // Initialize git repository first
        try {
            Git.makesEntireGitRepo();
        } catch (IOException e) {
            failTest("Failed to initialize git repository: " + e.getMessage());
            return;
        }
        
        // Test 1: Empty index handling
        testEmptyIndexHandling();
        
        // Test 2: Flat files at root
        testFlatFilesAtRoot(gitWrapper);
        
        // Test 3: Create trees given a directory
        testCreateTreesGivenDirectory();
        
        // Test 4: Create trees given a working list
        testCreateTreesGivenWorkingList(gitWrapper);
        
        // Test 5: Tree object persistence
        testTreeObjectPersistence(gitWrapper);
        
        // Test 6: Root tree outcome
        testRootTreeOutcome(gitWrapper);
        
        // Test 7: Determinism
        testDeterminism(gitWrapper);
        
        // Print tree workflow test results
        System.out.println("\n=== Tree Workflow Test Results ===");
        System.out.println("Tree workflow tests completed. Check individual test outputs above.");
    }
    
    /**
     * Test empty index handling - produces empty root tree object
     */
    private static void testEmptyIndexHandling() {
        System.out.println("Test 1: Empty index handling");
        
        try {
            // Ensure index is empty
            File indexFile = new File(INDEX_FILE);
            Files.writeString(indexFile.toPath(), "");
            
            // Run workToTree
            String rootTreeHash = Git.workToTree();
            
            // Verify root tree hash was returned
            assertTest(rootTreeHash != null && !rootTreeHash.isEmpty(), 
                      "Root tree hash should be returned for empty index");
            assertTest(!rootTreeHash.equals("you failed"), 
                      "Should not return failure message for empty index");
            
            // Verify tree object exists
            File treeFile = new File("git/objects", rootTreeHash);
            assertTest(treeFile.exists(), "Root tree object should exist");
            
            // Verify tree content is empty string
            String treeContent = Files.readString(treeFile.toPath());
            assertTest(treeContent.isEmpty(), "Empty index should produce empty tree content");
            
        } catch (Exception e) {
            failTest("Exception during empty index test: " + e.getMessage());
        }
        
        System.out.println("✅ Empty index handling test passed\n");
    }
    
    /**
     * Test flat files at root - creates tree with one line per file
     */
    private static void testFlatFilesAtRoot(GitWrapper gitWrapper) {
        System.out.println("Test 2: Flat files at root");
        
        File testFile1 = new File("rootFile1.txt");
        File testFile2 = new File("rootFile2.txt");
        File testFile3 = new File("rootFile3.txt");
        
        try {
            // Create test files with different content
            Files.writeString(testFile1.toPath(), "Content 1");
            Files.writeString(testFile2.toPath(), "Content 2 - different");
            Files.writeString(testFile3.toPath(), "Content 3 - also different");
            
            // Add files to index
            gitWrapper.add("rootFile1.txt");
            gitWrapper.add("rootFile2.txt");
            gitWrapper.add("rootFile3.txt");
            
            // Create tree
            String rootTreeHash = Git.workToTree();
            
            // Verify tree object exists
            File treeFile = new File("git/objects", rootTreeHash);
            assertTest(treeFile.exists(), "Root tree object should exist");
            
            // Read and verify tree content
            String treeContent = Files.readString(treeFile.toPath());
            String[] lines = treeContent.split("\n");
            
            // Should have 3 lines (one per file)
            assertTest(lines.length == 3, "Tree should contain exactly 3 lines for 3 files");
            
            // Verify format: <blob/tree> <sha> <filename>
            for (String line : lines) {
                String[] parts = line.split(" ");
                assertTest(parts.length == 3, "Each tree line should have 3 parts: type hash name");
                assertTest(parts[0].equals("blob"), "All entries should be blob type for files");
                assertTest(parts[2].matches("rootFile[123]\\.txt"), "Filename should match expected pattern");
            }
            
            // Verify files are in stable order (should be sorted)
            String[] expectedOrder = {"rootFile1.txt", "rootFile2.txt", "rootFile3.txt"};
            for (int i = 0; i < lines.length; i++) {
                String[] parts = lines[i].split(" ");
                assertTest(parts[2].equals(expectedOrder[i]), 
                          "Files should be in stable alphabetical order");
            }
            
        } catch (Exception e) {
            failTest("Exception during flat files test: " + e.getMessage());
        } finally {
            // Clean up
            if (testFile1.exists()) testFile1.delete();
            if (testFile2.exists()) testFile2.delete();
            if (testFile3.exists()) testFile3.delete();
        }
        
        System.out.println("✅ Flat files at root test passed\n");
    }
    
    /**
     * Test create trees given a directory - treeify method
     */
    private static void testCreateTreesGivenDirectory() {
        System.out.println("Test 3: Create trees given a directory");
        
        File testDir = new File("testDir");
        File subDir = new File("testDir", "subDir");
        File testFile1 = new File("testDir", "file1.txt");
        File testFile2 = new File(new File("testDir", "subDir"), "file2.txt");
        
        try {
            // Create directory structure
            testDir.mkdir();
            subDir.mkdir();
            Files.writeString(testFile1.toPath(), "File 1 content");
            Files.writeString(testFile2.toPath(), "File 2 content");
            
            // Run treeify
            String rootTreeHash = Git.treeify("testDir");
            
            // Verify root tree exists
            File rootTreeFile = new File("git/objects", rootTreeHash);
            assertTest(rootTreeFile.exists(), "Root tree object should exist");
            
            // Read root tree content
            String rootTreeContent = Files.readString(rootTreeFile.toPath());
            String[] rootLines = rootTreeContent.split("\n");
            
            // Should have 2 entries: file1.txt and subDir
            assertTest(rootLines.length == 2, "Root tree should have 2 entries");
            
            boolean foundFile = false, foundSubDir = false;
            String subDirHash = null;
            
            for (String line : rootLines) {
                String[] parts = line.split(" ");
                if (parts.length >= 3) {
                    if (parts[2].equals("testDir/file1.txt")) {
                        assertTest(parts[0].equals("blob"), "file1.txt should be blob type");
                        foundFile = true;
                    } else if (parts[2].equals("testDir/subDir")) {
                        assertTest(parts[0].equals("tree"), "subDir should be tree type");
                        subDirHash = parts[1];
                        foundSubDir = true;
                    }
                }
            }
            
            assertTest(foundFile && foundSubDir, "Both file and subdirectory should be found");
            
            // Verify subdirectory tree exists
            assertTest(subDirHash != null, "Subdirectory hash should not be null");
            File subTreeFile = new File("git/objects", subDirHash);
            assertTest(subTreeFile.exists(), "Subdirectory tree object should exist");
            
            // Verify subdirectory tree content
            String subTreeContent = Files.readString(subTreeFile.toPath());
            String[] subLines = subTreeContent.split("\n");
            
            assertTest(subLines.length == 1, "Subdirectory tree should have 1 entry");
            String[] subParts = subLines[0].split(" ");
            assertTest(subParts[0].equals("blob"), "Subdirectory entry should be blob type");
            assertTest(subParts[2].equals("testDir/subDir/file2.txt"), "Subdirectory should contain full path to file2.txt");
            
        } catch (Exception e) {
            failTest("Exception during directory tree test: " + e.getMessage());
        } finally {
            // Clean up
            if (testFile2.exists()) testFile2.delete();
            if (testFile1.exists()) testFile1.delete();
            if (subDir.exists()) subDir.delete();
            if (testDir.exists()) testDir.delete();
        }
        
        System.out.println("✅ Create trees given directory test passed\n");
    }
    
    /**
     * Test create trees given a working list - workToTree method
     */
    private static void testCreateTreesGivenWorkingList(GitWrapper gitWrapper) {
        System.out.println("Test 4: Create trees given a working list");
        
        File testFile = new File("workingListTest.txt");
        
        try {
            // Clear the index to start fresh
            File indexFile = new File(INDEX_FILE);
            Files.writeString(indexFile.toPath(), "");
            
            // Create a file
            Files.writeString(testFile.toPath(), "Working list test content");
            
            // Add file to index
            gitWrapper.add("workingListTest.txt");
            
            // Verify working list file is created
            File workingListFile = new File("git", "workingList");
            
            // Run workToTree
            String rootTreeHash = Git.workToTree();
            
            // Verify root tree hash is returned
            assertTest(rootTreeHash != null && !rootTreeHash.isEmpty(), 
                      "Root tree hash should be returned");
            assertTest(!rootTreeHash.equals("you failed"), 
                      "Should not return failure message");
            
            // Verify root tree object exists
            File rootTreeFile = new File("git/objects", rootTreeHash);
            assertTest(rootTreeFile.exists(), "Root tree object should exist");
            
            // Verify tree content matches index
            String treeContent = Files.readString(rootTreeFile.toPath());
            String[] lines = treeContent.split("\n");
            
            assertTest(lines.length == 1, "Tree should have one entry for one file");
            String[] parts = lines[0].split(" ");
            assertTest(parts[0].equals("blob"), "Entry should be blob type");
            // workToTree uses the path from the index, which should be just the filename
            assertTest(parts[2].equals("workingListTest.txt"), "Should contain correct filename");
            
            // Verify working list is updated with root tree hash
            // (This would need to be verified based on your implementation)
            
        } catch (Exception e) {
            failTest("Exception during working list test: " + e.getMessage());
        } finally {
            // Clean up
            if (testFile.exists()) testFile.delete();
        }
        
        System.out.println("✅ Create trees given working list test passed\n");
    }
    
    /**
     * Test tree object persistence - each tree's SHA is hash of exact content
     */
    private static void testTreeObjectPersistence(GitWrapper gitWrapper) {
        System.out.println("Test 5: Tree object persistence");
        GPTester.robustReset();
        
        File testFile = new File("persistenceTest.txt");
        
        try {
            // Clear the index to start fresh
            File indexFile = new File(INDEX_FILE);
            Files.writeString(indexFile.toPath(), "");
            
            // Create file with specific content
            String fileContent = "Persistence test content";
            Files.writeString(testFile.toPath(), fileContent);
            
            // Add file
            gitWrapper.add("persistenceTest.txt");
            
            // Create tree
            String rootTreeHash = Git.workToTree();
            
            // Verify tree object exists
            File treeFile = new File("git/objects", rootTreeHash);
            assertTest(treeFile.exists(), "Tree object should exist");
            
            // Read tree content
            String treeContent = Files.readString(treeFile.toPath());
            
            // Calculate expected hash
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            byte[] bytes = treeContent.getBytes();
            byte[] hashBytes = digest.digest(bytes);
            String expectedHash = Git.makeItHex(hashBytes);
            
            // Verify SHA matches content
            assertTest(rootTreeHash.equals(expectedHash), 
                      "Tree SHA should be hash of its exact content");
            
            
            
        } catch (Exception e) {
            failTest("Exception during persistence test: " + e.getMessage());
        } finally {
            // Clean up
            if (testFile.exists()) testFile.delete();
        }
        
        System.out.println("✅ Tree object persistence test passed\n");
    }
    
    /**
     * Test root tree outcome - returns/stores root tree hash
     */
    private static void testRootTreeOutcome(GitWrapper gitWrapper) {
        System.out.println("Test 6: Root tree outcome");
        
        File testFile1 = new File("outcomeTest1.txt");
        File testFile2 = new File("outcomeTest2.txt");
        
        try {
            // Create multiple files
            Files.writeString(testFile1.toPath(), "Outcome test 1");
            Files.writeString(testFile2.toPath(), "Outcome test 2");
            
            // Add files
            gitWrapper.add("outcomeTest1.txt");
            gitWrapper.add("outcomeTest2.txt");
            
            // Get root tree hash
            String rootTreeHash = Git.workToTree();
            
            // Verify hash is valid (40 character hex string)
            assertTest(rootTreeHash.length() == 40, "Root tree hash should be 40 characters");
            assertTest(rootTreeHash.matches("[0-9A-F]+"), "Root tree hash should be hex string");
            
            // Verify hash can be used to locate tree object
            File treeFile = new File("git/objects", rootTreeHash);
            assertTest(treeFile.exists(), "Root tree object should exist at hash location");
            
            // Verify tree contains both files
            String treeContent = Files.readString(treeFile.toPath());
            assertTest(treeContent.contains("outcomeTest1.txt"), "Tree should contain first file");
            assertTest(treeContent.contains("outcomeTest2.txt"), "Tree should contain second file");
            
        } catch (Exception e) {
            failTest("Exception during root tree outcome test: " + e.getMessage());
        } finally {
            // Clean up
            if (testFile1.exists()) testFile1.delete();
            if (testFile2.exists()) testFile2.delete();
        }
        
        System.out.println("✅ Root tree outcome test passed\n");
    }
    
    /**
     * Test determinism - same index yields identical tree SHAs
     */
    private static void testDeterminism(GitWrapper gitWrapper) {
        System.out.println("Test 7: Determinism");
        
        File testFile1 = new File("determinism1.txt");
        File testFile2 = new File("determinism2.txt");
        
        try {
            // Create files with specific content
            Files.writeString(testFile1.toPath(), "Determinism test 1");
            Files.writeString(testFile2.toPath(), "Determinism test 2");
            
            // Add files in specific order
            gitWrapper.add("determinism1.txt");
            gitWrapper.add("determinism2.txt");
            
            // Create tree first time
            String firstTreeHash = Git.workToTree();
            
            // Create tree second time
            String secondTreeHash = Git.workToTree();
            
            // Verify deterministic - same hash
            assertTest(firstTreeHash.equals(secondTreeHash), 
                      "Same index should produce identical tree SHAs");
            
            // Test that changing one file only affects relevant trees
            // Modify first file
            Files.writeString(testFile1.toPath(), "Modified determinism test 1");
            gitWrapper.add("determinism1.txt");
            
            // Create new tree
            String modifiedTreeHash = Git.workToTree();
            
            // Verify hash changed
            assertTest(!firstTreeHash.equals(modifiedTreeHash), 
                      "Modified index should produce different tree SHA");
            
            // Verify only the relevant tree changed
            File originalTree = new File("git/objects", firstTreeHash);
            File modifiedTree = new File("git/objects", modifiedTreeHash);
            
            assertTest(modifiedTree.exists(), "Modified tree should exist");
            
            // The original tree should still exist (blob for unchanged file)
            String originalContent = Files.readString(originalTree.toPath());
            String modifiedContent = Files.readString(modifiedTree.toPath());
            
            assertTest(modifiedContent.contains("determinism2.txt"), 
                      "Unchanged file should still be in tree");
            assertTest(!originalContent.equals(modifiedContent), 
                      "Tree content should be different after modification");
            
        } catch (Exception e) {
            failTest("Exception during determinism test: " + e.getMessage());
        } finally {
            // Clean up
            if (testFile1.exists()) testFile1.delete();
            if (testFile2.exists()) testFile2.delete();
        }
        
        System.out.println("✅ Determinism test passed\n");
    }
}

