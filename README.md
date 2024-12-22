# CPU Scheduling Algorithms Documentation

## General Overview
This project demonstrates the implementation of multiple CPU scheduling algorithms in Java. Each algorithm adheres to specific rules for process scheduling and computes metrics like waiting time and turnaround time. The project includes the following algorithms:

- **First Come First Serve (FCFS)**
- **Shortest Job First (SJF)**
- **Shortest Remaining Time (SRT)**
- **Priority Scheduling (Preemptive and Non-Preemptive)**
- **Round Robin (RR)**

These algorithms are implemented in separate classes, each extending a common `CPUScheduler` abstract class. The project also includes utility classes for managing processes (`Row`) and events (`Event`).

---

## Explanation of Each Algorithm

### 1. First Come First Serve (FCFS)
**Description:**
Processes are scheduled in the order of their arrival times. The CPU executes each process to completion before moving to the next one.

**Code Snippet:**
```java
public class FirstComeFirstServe extends CPUScheduler {
    @Override
    public void process() {
        Collections.sort(this.getRows(), (o1, o2) -> {
            return Integer.compare(((Row) o1).getArrivalTime(), ((Row) o2).getArrivalTime());
        });

        List<Event> timeline = this.getTimeline();

        for (Row row : this.getRows()) {
            if (timeline.isEmpty()) {
                timeline.add(new Event(row.getProcessName(), row.getArrivalTime(), row.getArrivalTime() + row.getBurstTime()));
            } else {
                Event event = timeline.get(timeline.size() - 1);
                timeline.add(new Event(row.getProcessName(), event.getFinishTime(), event.getFinishTime() + row.getBurstTime()));
            }
        }

        for (Row row : this.getRows()) {
            row.setWaitingTime(this.getEvent(row).getStartTime() - row.getArrivalTime());
            row.setTurnaroundTime(row.getWaitingTime() + row.getBurstTime());
        }
    }
}
```

**Distinctive Elements:**
- Simple and easy to implement.
- Non-preemptive.

**Outcome:**
May lead to the convoy effect, where shorter processes wait behind longer ones.

---

### 2. Shortest Job First (SJF)
**Description:**
The process with the smallest burst time is executed first. Ties are resolved using arrival times.

**Code Snippet:**
```java
public class ShortestJobFirst extends CPUScheduler {
    @Override
    public void process() {
        Collections.sort(this.getRows(), (o1, o2) -> Integer.compare(((Row) o1).getArrivalTime(), ((Row) o2).getArrivalTime()));
        
        List<Row> rows = Utility.deepCopy(this.getRows());
        int time = rows.get(0).getArrivalTime();

        while (!rows.isEmpty()) {
            List<Row> availableRows = new ArrayList<>();

            for (Row row : rows) {
                if (row.getArrivalTime() <= time) {
                    availableRows.add(row);
                }
            }

            Collections.sort(availableRows, (o1, o2) -> Integer.compare(((Row) o1).getBurstTime(), ((Row) o2).getBurstTime()));

            Row row = availableRows.get(0);
            this.getTimeline().add(new Event(row.getProcessName(), time, time + row.getBurstTime()));
            time += row.getBurstTime();

            rows.remove(row);
        }

        for (Row row : this.getRows()) {
            row.setWaitingTime(this.getEvent(row).getStartTime() - row.getArrivalTime());
            row.setTurnaroundTime(row.getWaitingTime() + row.getBurstTime());
        }
    }
}
```

**Distinctive Elements:**
- Non-preemptive.
- Optimal for minimizing average waiting time.

---

### 3. Priority Scheduling (Preemptive and Non-Preemptive)
**Description:**
Processes are scheduled based on their priority levels. Lower values indicate higher priority.

**Code Snippet (Preemptive):**
```java
public class PriorityPreemptive extends CPUScheduler {
    @Override
    public void process() {
        Collections.sort(this.getRows(), (o1, o2) -> Integer.compare(((Row) o1).getArrivalTime(), ((Row) o2).getArrivalTime()));
        
        List<Row> rows = Utility.deepCopy(this.getRows());
        int time = rows.get(0).getArrivalTime();

        while (!rows.isEmpty()) {
            List<Row> availableRows = new ArrayList<>();

            for (Row row : rows) {
                if (row.getArrivalTime() <= time) {
                    availableRows.add(row);
                }
            }

            Collections.sort(availableRows, (o1, o2) -> Integer.compare(((Row) o1).getPriorityLevel(), ((Row) o2).getPriorityLevel()));

            Row row = availableRows.get(0);
            this.getTimeline().add(new Event(row.getProcessName(), time, ++time));
            row.setBurstTime(row.getBurstTime() - 1);

            if (row.getBurstTime() == 0) {
                rows.remove(row);
            }
        }

        // Additional calculations for waiting and turnaround times.
    }
}
```

**Distinctive Elements:**
- Includes both preemptive and non-preemptive variations.
- Useful when processes have different importance levels.

---

### 4. Round Robin (RR)
**Description:**
Processes are executed in a cyclic order, each receiving a fixed time slice (quantum).

**Code Snippet:**
```java
public class RoundRobin extends CPUScheduler {
    @Override
    public void process() {
        if (this.getRows().isEmpty() || this.getTimeQuantum() <= 0) {
            return;
        }

        Collections.sort(this.getRows(), (o1, o2) -> Integer.compare(((Row) o1).getArrivalTime(), ((Row) o2).getArrivalTime()));

        List<Row> queue = new ArrayList<>();
        List<Row> rows = Utility.deepCopy(this.getRows());
        int currentTime = rows.get(0).getArrivalTime();
        int timeQuantum = this.getTimeQuantum();

        while (!rows.isEmpty() || !queue.isEmpty()) {
            while (!rows.isEmpty() && rows.get(0).getArrivalTime() <= currentTime) {
                queue.add(rows.remove(0));
            }

            if (!queue.isEmpty()) {
                Row currentProcess = queue.remove(0);
                int executionTime = Math.min(currentProcess.getBurstTime(), timeQuantum);
                this.getTimeline().add(new Event(currentProcess.getProcessName(), currentTime, currentTime + executionTime));
                currentProcess.setBurstTime(currentProcess.getBurstTime() - executionTime);
                currentTime += executionTime;

                if (currentProcess.getBurstTime() > 0) {
                    queue.add(currentProcess);
                }
            } else {
                currentTime = rows.get(0).getArrivalTime();
            }
        }

        // Additional calculations for waiting and turnaround times.
    }
}
```

**Distinctive Elements:**
- Preemptive by design.
- Fair sharing of CPU time among processes.

---

## Summary
This project provides a comprehensive exploration of CPU scheduling algorithms. Each class demonstrates the logic and computation necessary for scheduling processes, making it an excellent resource for learning and comparison. These implementations highlight trade-offs between fairness, efficiency, and response time in process scheduling.

---

## Team Members:
1. Abdalrhman Osama
2. Abdulrhman Ashraf
3. Ahmed Emad
4. Abdelrhman Mohamed
