import java.util.Random;

class tNode {
    int data;
    tNode left, right;
    
    public tNode(int data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}

class BinarySearchTree {
    private tNode root;
    
    public BinarySearchTree() {
        root = null;
    }
    
    // Insert method
    public void insert(int value) {
        root = insertRec(root, value);
    }
    
    private tNode insertRec(tNode root, int value) {
        if (root == null) {
            return new tNode(value);
        }
        
        if (value < root.data) {
            root.left = insertRec(root.left, value);
        } else if (value > root.data) {
            root.right = insertRec(root.right, value);
        }
        
        return root;
    }
    
    // Check if tree is BST
    public boolean isBST() {
        return isBSTRec(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    private boolean isBSTRec(tNode node, int min, int max) {
        if (node == null) {
            return true;
        }
        
        if (node.data < min || node.data > max) {
            return false;
        }
        
        return isBSTRec(node.left, min, node.data - 1) &&
               isBSTRec(node.right, node.data + 1, max);
    }
    
    // Build perfect BST using divide and conquer
    public void buildPerfectBST(int[] numbers, int start, int end) {
        if (start > end) {
            return;
        }
        
        int mid = start + (end - start) / 2;
        insert(numbers[mid]);
        
        buildPerfectBST(numbers, start, mid - 1);
        buildPerfectBST(numbers, mid + 1, end);
    }
    
    // Delete nodes with even values
    public void deleteEvenNumbers() {
        root = deleteEvenRec(root);
    }
    
    private tNode deleteEvenRec(tNode node) {
        if (node == null) {
            return null;
        }
        
        node.left = deleteEvenRec(node.left);
        node.right = deleteEvenRec(node.right);
        
        if (node.data % 2 == 0) {
            return deleteNode(node);
        }
        
        return node;
    }
    
    private tNode deleteNode(tNode node) {
        // Case 1: Leaf node
        if (node.left == null && node.right == null) {
            return null;
        }
        
        // Case 2: Only right child
        if (node.left == null) {
            return node.right;
        }
        
        // Case 3: Only left child
        if (node.right == null) {
            return node.left;
        }
        
        // Case 4: Two children - find inorder successor
        tNode successor = findMin(node.right);
        node.data = successor.data;
        node.right = deleteNodeRec(node.right, successor.data);
        
        return node;
    }
    
    private tNode findMin(tNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
    
    private tNode deleteNodeRec(tNode root, int value) {
        if (root == null) {
            return null;
        }
        
        if (value < root.data) {
            root.left = deleteNodeRec(root.left, value);
        } else if (value > root.data) {
            root.right = deleteNodeRec(root.right, value);
        } else {
            // Found the node to delete
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            
            tNode successor = findMin(root.right);
            root.data = successor.data;
            root.right = deleteNodeRec(root.right, successor.data);
        }
        
        return root;
    }
    
    // Reset tree
    public void reset() {
        root = null;
    }
    
    // For debugging - inorder traversal
    public void inorder() {
        inorderRec(root);
        System.out.println();
    }
    
    private void inorderRec(tNode root) {
        if (root != null) {
            inorderRec(root.left);
            System.out.print(root.data + " ");
            inorderRec(root.right);
        }
    }
    
    // Count nodes (for verification)
    public int countNodes() {
        return countNodesRec(root);
    }
    
    private int countNodesRec(tNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + countNodesRec(node.left) + countNodesRec(node.right);
    }
}

class TimingResult {
    double average;
    double stdDev;
    
    TimingResult(double average, double stdDev) {
        this.average = average;
        this.stdDev = stdDev;
    }
}

public class tryBST {
    
    private static int[] generateSortedNumbers(int numKeys) {
        int[] numbers = new int[numKeys];
        for (int i = 0; i < numKeys; i++) {
            numbers[i] = i + 1;
        }
        return numbers;
    }
    
    private static TimingResult calculateStatistics(long[] times) {
        double sum = 0;
        for (long time : times) {
            sum += time;
        }
        double mean = sum / times.length;
        
        double variance = 0;
        for (long time : times) {
            variance += Math.pow(time - mean, 2);
        }
        variance /= times.length;
        double stdDev = Math.sqrt(variance);
        
        return new TimingResult(mean, stdDev);
    }
    
    private static TimingResult measurePopulateTime(int numKeys, int repetitions) {
        long[] times = new long[repetitions];
        
        for (int i = 0; i < repetitions; i++) {
            BinarySearchTree tree = new BinarySearchTree();
            int[] numbers = generateSortedNumbers(numKeys);
            
            long startTime = System.nanoTime();
            tree.buildPerfectBST(numbers, 0, numbers.length - 1);
            long endTime = System.nanoTime();
            
            times[i] = (endTime - startTime) / 1000000; // Convert to milliseconds
            
            // Verify BST property
            if (!tree.isBST()) {
                System.out.println("Warning: Tree is not a BST at repetition " + (i+1));
            }
        }
        
        return calculateStatistics(times);
    }
    
    private static TimingResult measureDeleteTime(int numKeys, int repetitions) {
        long[] times = new long[repetitions];
        
        for (int i = 0; i < repetitions; i++) {
            BinarySearchTree tree = new BinarySearchTree();
            int[] numbers = generateSortedNumbers(numKeys);
            tree.buildPerfectBST(numbers, 0, numbers.length - 1);
            
            long startTime = System.nanoTime();
            tree.deleteEvenNumbers();
            long endTime = System.nanoTime();
            
            times[i] = (endTime - startTime) / 1000000; // Convert to milliseconds
        }
        
        return calculateStatistics(times);
    }
    
    private static void testWithSmallN() {
        System.out.println("\n=== TESTING WITH SMALL n VALUES ===");
        System.out.println("Verifying tree construction and deletion...\n");
        
        int[] testValues = {2, 3, 4}; // n = 2, 3, 4
        
        for (int n : testValues) {
            int numKeys = (int) Math.pow(2, n) - 1;
            System.out.println("Testing with n = " + n + " (keys: 1 to " + numKeys + ")");
            
            BinarySearchTree tree = new BinarySearchTree();
            int[] numbers = generateSortedNumbers(numKeys);
            
            // Build tree
            tree.buildPerfectBST(numbers, 0, numbers.length - 1);
            
            // Check if BST
            boolean isBST = tree.isBST();
            System.out.println("  Is BST? " + isBST);
            
            // Count nodes before deletion
            int nodeCountBefore = tree.countNodes();
            System.out.println("  Nodes before deletion: " + nodeCountBefore);
            
            // Delete even numbers
            tree.deleteEvenNumbers();
            
            // Count nodes after deletion
            int nodeCountAfter = tree.countNodes();
            System.out.println("  Nodes after deletion: " + nodeCountAfter);
            
            // Expected: all odd numbers remain
            int expectedCount = (numKeys + 1) / 2;
            System.out.println("  Expected nodes after deletion: " + expectedCount);
            System.out.println("  Verification: " + (nodeCountAfter == expectedCount ? "PASSED" : "FAILED"));
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("CSC 211 - Fundamental Algorithms and Data Structures");
        System.out.println("Practical 7: Perfect Binary Search Tree with Even Number Deletion");
        System.out.println("============================================================\n");
        
        // First test with small values to verify correctness
        testWithSmallN();
        
        // Determine appropriate n to get times > 1000ms
        System.out.println("=== PERFORMANCE TESTING ===");
        System.out.println("Finding appropriate n to achieve times > 1000ms...\n");
        
        int repetitions = 30;
        int[] nCandidates = {10, 12, 14, 16, 18, 20};
        int selectedN = 0;
        
        for (int n : nCandidates) {
            int numKeys = (int) Math.pow(2, n) - 1;
            System.out.print("Testing n = " + n + " (" + numKeys + " keys)... ");
            
            // Quick test with 5 repetitions to estimate time
            BinarySearchTree tree = new BinarySearchTree();
            int[] numbers = generateSortedNumbers(numKeys);
            
            long startTime = System.currentTimeMillis();
            tree.buildPerfectBST(numbers, 0, numbers.length - 1);
            long buildTime = System.currentTimeMillis() - startTime;
            
            if (buildTime > 1000) {
                selectedN = n;
                System.out.println("build time = " + buildTime + "ms (EXCEEDS 1000ms)");
                break;
            } else {
                System.out.println("build time = " + buildTime + "ms");
            }
        }
        
        if (selectedN == 0) {
            selectedN = 20;
            System.out.println("\nUsing maximum n = 20 for final testing");
        }
        
        int numKeys = (int) Math.pow(2, selectedN) - 1;
        System.out.println("\n=== FINAL RESULTS ===");
        System.out.println("Using n = " + selectedN + " with " + numKeys + " keys");
        System.out.println("Running " + repetitions + " repetitions for accurate statistics...\n");
        
        // Measure populate time
        System.out.println("Measuring populate tree time...");
        TimingResult populateResult = measurePopulateTime(numKeys, repetitions);
        
        // Measure delete time
        System.out.println("Measuring delete even numbers time...");
        TimingResult deleteResult = measureDeleteTime(numKeys, repetitions);
        
        // Display results in table format
        System.out.println("\n");
        System.out.println("+------------------------+---------------------+------------------------+------------------------+");
        System.out.println("| Method                 | Number of keys n    | Average time in ms    | Standard Deviation     |");
        System.out.println("+------------------------+---------------------+------------------------+------------------------+");
        System.out.printf("| %-22s | %,19d | %22.2f | %22.2f |\n", 
                          "Populate tree", numKeys, populateResult.average, populateResult.stdDev);
        System.out.printf("| %-22s | %,19d | %22.2f | %22.2f |\n", 
                          "Remove evens from tree", numKeys, deleteResult.average, deleteResult.stdDev);
        System.out.println("+------------------------+---------------------+------------------------+------------------------+");
        
        // Additional statistics
        System.out.println("\n=== ADDITIONAL INFORMATION ===");
        System.out.println("Total keys in tree: " + numKeys);
        System.out.println("Expected odd numbers (remaining after deletion): " + ((numKeys + 1) / 2));
        System.out.println("Repetitions used: " + repetitions);
        System.out.println("\nNote: All times are in milliseconds");
        System.out.println("Populate tree time includes building perfectly balanced BST");
        System.out.println("Delete time includes removing all nodes with even values");
        
        // Verify that average times exceed 1000ms
        if (populateResult.average >= 1000 || deleteResult.average >= 1000) {
            System.out.println("\n✓ SUCCESS: Average times meet the requirement (>1000ms)");
        } else {
            System.out.println("\n⚠ WARNING: Average times are below 1000ms. Consider increasing n further.");
        }
    }
}
