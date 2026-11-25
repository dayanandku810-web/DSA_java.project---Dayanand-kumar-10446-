import java.util.*;

public class TaskSchedulerSystem {

    static Scanner sc = new Scanner(System.in);

    // Array to store all tasks
    static Task[] tasks = new Task[100];
    static int count = 0;
    static int id = 1;

    // Normal Queue (FIFO)
    static Queue<Task> normalQueue = new LinkedList<>();

    // Priority Queue (Heap)
    static PriorityQueue<Task> priorityQueue =
        new PriorityQueue<>(
            (a, b) -> {
                if (a.priority != b.priority)
                    return b.priority - a.priority;
                return a.id - b.id;
            }
        );
k
    // Task class
    static class Task {
        int id;
        String name;
        int priority;
        boolean completed;
        boolean cancelled;

        Task(int id, String name, int priority) {
            this.id = id;
            this.name = name;
            this.priority = priority;
            this.completed = false;
            this.cancelled = false;
        }

        public String toString() {
            String status = completed ? "Completed" :
                (cancelled ? "Cancelled" : "Pending");

            return "ID: " + id +
                   " | Task: " + name +
                   " | Priority: " + priority +
                   " | Status: " + status;
        }
    }

    // Main method
    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n===== TASK SCHEDULER SYSTEM =====");
            System.out.println("1. Add Normal Task");
            System.out.println("2. Add Priority Task");
            System.out.println("3. Execute Next Task");
            System.out.println("4. Display Pending Tasks");
            System.out.println("5. Search Task");
            System.out.println("6. Sort Tasks (Heap Sort)");
            System.out.println("7. Cancel Task");
            System.out.println("8. Display Completed Tasks");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addTask(false);
                    break;

                case 2:
                    addTask(true);
                    break;

                case 3:
                    executeTask();
                    break;

                case 4:
                    displayTasks();
                    break;

                case 5:
                    searchTask();
                    break;

                case 6:
                    sortTasks();
                    break;

                case 7:
                    cancelTask();
                    break;

                case 8:
                    displayCompleted();
                    break;

                case 0:
                    System.out.println("Thank you!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 0);
    }

    // Add Task (Insertion)
    static void addTask(boolean isPriority) {

        if (count == 100) {
            System.out.println("Task storage is full!");
            return;
        }

        System.out.print("Enter task name: ");
        String name = sc.nextLine();

        System.out.println("Priority:");
        System.out.println("1. Low");
        System.out.println("2. Medium");
        System.out.println("3. High");
        System.out.println("4. Urgent");
        System.out.println("5. Critical");

        System.out.print("Enter priority: ");
        int priority = sc.nextInt();
        sc.nextLine();

        if (priority < 1 || priority > 5) {
            System.out.println("Invalid priority!");
            return;
        }

        Task t = new Task(id++, name, priority);

        // Store in array
        tasks[count++] = t;

        if (isPriority) {
            priorityQueue.add(t);
            System.out.println("Priority task added!");
        } else {
            normalQueue.add(t);
            System.out.println("Normal task added!");
        }

        System.out.println("Task ID: " + t.id);
    }

    // Execute Task (Deletion)
    static void executeTask() {

        Task t = null;

        // Priority tasks execute first
        while (!priorityQueue.isEmpty()) {
            Task temp = priorityQueue.poll();

            if (!temp.cancelled && !temp.completed) {
                t = temp;
                break;
            }
        }

        // Otherwise execute normal FIFO task
        if (t == null) {
            while (!normalQueue.isEmpty()) {
                Task temp = normalQueue.poll();

                if (!temp.cancelled && !temp.completed) {
                    t = temp;
                    break;
                }
            }
        }

        if (t == null) {
            System.out.println("No pending tasks!");
            return;
        }

        t.completed = true;

        System.out.println("Executing Task:");
        System.out.println(t);
    }

    // Display Tasks (Array Traversal)
    static void displayTasks() {

        System.out.println("\n--- Pending Tasks ---");

        boolean found = false;

        for (int i = 0; i < count; i++) {

            if (!tasks[i].completed && !tasks[i].cancelled) {
                System.out.println(tasks[i]);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No pending tasks!");
        }
    }

    // Linear Search
    static void searchTask() {

        System.out.print("Enter Task ID: ");
        int searchId = sc.nextInt();

        for (int i = 0; i < count; i++) {

            if (tasks[i].id == searchId) {
                System.out.println("Task Found:");
                System.out.println(tasks[i]);
                return;
            }
        }

        System.out.println("Task not found!");
    }

    // Heap Sort
    static void sortTasks() {

        Task[] arr = new Task[count];
        int n = 0;

        for (int i = 0; i < count; i++) {
            if (!tasks[i].completed && !tasks[i].cancelled) {
                arr[n++] = tasks[i];
            }
        }

        if (n == 0) {
            System.out.println("No pending tasks!");
            return;
        }

        arr = Arrays.copyOf(arr, n);

        // Build Min Heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Heap Sort
        for (int i = n - 1; i > 0; i--) {

            Task temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            heapify(arr, i, 0);
        }

        System.out.println("\n--- Sorted Tasks (High to Low) ---");

        for (Task t : arr) {
            System.out.println(t);
        }
    }

    // Heapify using Recursion
    static void heapify(Task[] arr, int n, int i) {

        int smallest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n &&
            arr[left].priority < arr[smallest].priority) {
            smallest = left;
        }

        if (right < n &&
            arr[right].priority < arr[smallest].priority) {
            smallest = right;
        }

        if (smallest != i) {

            Task temp = arr[i];
            arr[i] = arr[smallest];
            arr[smallest] = temp;

            heapify(arr, n, smallest);
        }
    }

    // Cancel Task
    static void cancelTask() {

        System.out.print("Enter Task ID to cancel: ");
        int searchId = sc.nextInt();

        for (int i = 0; i < count; i++) {

            if (tasks[i].id == searchId) {

                if (tasks[i].completed || tasks[i].cancelled) {
                    System.out.println("Task already completed or cancelled!");
                } else {
                    tasks[i].cancelled = true;
                    System.out.println("Task cancelled successfully!");
                }

                return;
            }
        }

        System.out.println("Task not found!");
    }

    // Display Completed Tasks
    static void displayCompleted() {

        System.out.println("\n--- Completed Tasks ---");

        boolean found = false;

        for (int i = 0; i < count; i++) {

            if (tasks[i].completed) {
                System.out.println(tasks[i]);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No completed tasks!");
        }
    }
}
