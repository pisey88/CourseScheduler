# CourseScheduler

A Java-based course scheduling system that uses **Kahn’s Algorithm** to help students and universities build valid class schedules while respecting prerequisites, avoiding conflicts, and promoting on-time graduation.

---

## 🎯 Features

- ✅ Detects missing prerequisites
- ✅ Prevents invalid course enrollments
- ✅ Avoids circular dependencies
- ✅ Uses **Kahn’s Algorithm (BFS)** to sort and schedule courses
- ✅ Handles Directed Acyclic Graphs (DAG) efficiently
- ✅ Scalable and easy to use in real-world academic systems

---

## 🧠 Algorithm

We implement **Kahn’s Algorithm** to sort courses based on prerequisites:

1. **Indegree Calculation** – Count how many prerequisites each course has
2. **Queue Initialization** – Enqueue all courses with 0 prerequisites
3. **BFS Traversal** – Process courses in order and reduce indegrees of dependent courses
4. **Cycle Detection** – If unprocessed nodes remain, a cycle exists (invalid plan)

### ⏱️ Time Complexity

- **O(V + E)**  
  - V = Number of courses (vertices)  
  - E = Number of prerequisite pairs (edges)  
- Efficient for all cases — best, worst, and average

---

## 📷 Example
<img width="1911" height="1137" alt="Image" src="https://github.com/user-attachments/assets/407af47f-f300-4ff9-bd72-1993be011028" />
<img width="1900" height="1135" alt="Image" src="https://github.com/user-attachments/assets/35546012-4197-4b63-b1d0-cf2e88f09e9f" />
<img width="1919" height="1134" alt="Image" src="https://github.com/user-attachments/assets/f3884a8f-f8e2-486b-93e6-08e43d32fcbd" />


