If you run the the main method, it will run my milestone21 method. This method first ensures that the git repo does not already exist by checking all paths. Next, it will create a git repository, and an objects subfolder, then create the 2 files in seperate helper methods. I had to figure out how to deal with the exists function and found some helpful information through research which is attached at the method "createObjectsDirectory."

My tester ensures that all the git repository initialization is working. I ran it 5 times for maximum robustness and printed the number of the test and when it was created vs erased/deleted. I also made sure that everything is named something that is understandable. I considered edge cases where each directory or file already existed.

I used the same functionality that was used in FileWriter to create a SHA-1 hash code which is the same as 256 except you just rewrite 256 to 1. Also I used a helper to convert from bytes to hexidecimal.


GP-4.1: 
    Issue (Before Fix): The code successfully staged files and createf blob objects, but the tree-building process (workToTree) never returned or finalized the root tree. It stopped part-way through collapsing directories. As a result, there was no way to identify the final root tree hash or verify that the directory snapshot was complete.

    Fix Implemented:
    To resolve this, I modified the method header from
        public static void workToTree() throws Exception
    to
        public static String workToTree() throws Exception.

    Inside the method, I:
        Added a variable lastTreeHash to track the most recently created tree.
        Updated the loop so that each new tree stores its hash in lastTreeHash.
        Added a final step after the collapsing loop that builds the root tree from any remaining lines in the working list.
        Returned the final lastTreeHash so the root tree’s SHA can be printed and verified.

    Verification:
    I wrote a simple tester (GP41Tester.java) to initialize the respository, create nested folders and sample files, stage all files (generate blobs and udpate the index), and call workToTree() and print the root tree SHA. After running the tester, tree and blob files were successfully created in git/objects/, and the root tree SHA printed correctly.


GP-4.3: 
    bugs discovered and fixed:
        The workToTree() method originally didn't return the root tree hash or fully build the final tree. I changed its header from void to String and added logic to track and return the final tree SHA

    missing functionality implemented:
        I implemented a full GitWrapper class so all Git operations can be called through one consistent interface. The method init() initializes the repository. Add() stages files adn creates blob objects. Commit() builds tree and commit objects and updates HEAD. Checkout() restores the workign directory to match a selected commit.
        I also added a recursive restoreTree() helper method to rebuild files and folders from stored tree adn blob objects for the checkout() feature
        I created a complete tester GitTester.java that runs the full Git cycle (initializing, adding, commiting twice, checking out the first commit, adn verifying file restoration).

