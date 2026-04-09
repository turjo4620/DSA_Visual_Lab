# 🚀 DSA Visual Lab

**DSA Visual Lab** is a standalone, fully responsive desktop application designed to make learning Data Structures and Algorithms visual, interactive, and intuitive. 

Built entirely in Java and JavaFX, it acts as an offline, native alternative to web-based visualizers. Users can control animation speeds, input custom data, and watch complex algorithms execute step-by-step alongside synchronized, real-time pseudocode.

## ✨ Features

### 🔄 Sorting Algorithms
* **Merge Sort:** Features a dynamic Y-axis recursion tree visualization.
* **Quick Sort:** Visualizes in-place partitioning with strict semantic color coding.
* **$O(N^2)$ Sorts:** Bubble, Insertion, and Selection Sorts with element tracking.

### 📏 Linear Data Structures
* **Dynamic Arrays & Linked Lists:** Interactive memory visualization.
* **Stacks & Queues:** LIFO and FIFO environments with live Push, Pop, Enqueue, and Dequeue mechanics.

### 🌳 Trees & Graphs
* **Binary Search Tree (BST):** Basic insertions/deletions and animated traversals (Pre-order, In-order, Post-order).
* **Graph Traversals:** Breadth-First Search (BFS) and Depth-First Search (DFS).
* **Minimum Spanning Tree:** Kruskal's Algorithm.

### 🧠 Dynamic Programming
* **0/1 Knapsack:** Real-time, interactive DP table calculation and backtracking animation.

---

## 🛠️ Technical Architecture

This project was built to prioritize UI thread safety and performance:
* **State-Recording Animation Engine:** Bypasses the traditional, glitch-prone `Thread.sleep()` multithreading approach. The algorithms execute in pure memory and record their state changes into an event queue. This queue is then played back sequentially via JavaFX `PauseTransition`, resulting in perfectly smooth, synchronized animations that never block the main UI thread.
* **Responsive Layouts:** Utilizes dynamic root-swapping and flexible JavaFX containers (`StackPane`, `HBox` with `vgrow`) to ensure the canvas scales infinitely without breaking the visual boundaries.

---

## 🚀 How to Install & Run

### Option 1: For Users (The Easy Way)
You **do not** need Java or an IDE installed to run this application! It comes packaged with its own custom, lightweight Java runtime.

1. Go to the [Releases](../../releases) page on the right side of this repository.
2. Download the latest **`DSA_Visual_Lab-1.0.exe`** installer.
3. Double-click the file to install the application.
4. Launch it directly from your Desktop shortcut!

### Option 2: For Developers (Build from Source)
If you want to view the code, explore the animation engine, or contribute:

**Prerequisites:** Git and JDK 21+.

1. Clone the repository:
   ```bash
   git clone [https://github.com/turjo4620/DSA_Visual_Lab.git](https://github.com/turjo4620/DSA_Visual_Lab.git)

📸 Screenshots
<img width="1917" height="1005" alt="image" src="https://github.com/user-attachments/assets/a3406532-b8c1-4d31-9a84-ee1091030593" />
<img width="1919" height="999" alt="image" src="https://github.com/user-attachments/assets/39045f96-20eb-4808-9636-484a9f326572" />
<img width="1917" height="996" alt="image" src="https://github.com/user-attachments/assets/9a94a5cd-3e0b-4ba6-ad0a-f2180af5b3d7" />
<img width="1919" height="998" alt="image" src="https://github.com/user-attachments/assets/5da6930a-0376-4428-b8ca-f9e5af694462" />
<img width="1919" height="989" alt="image" src="https://github.com/user-attachments/assets/72df8c5f-0eaf-4d4d-8db5-be9e0e9c9d5b" />



   
