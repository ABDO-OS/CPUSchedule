
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundRobin extends CPUScheduler
{
    @Override
    public void process() {
        if (this.getRows().isEmpty() || this.getTimeQuantum() <= 0) {
            return;
        }

        // Sort by arrival time, then by process name for ties
        Collections.sort(this.getRows(), (o1, o2) -> {
            Row r1 = (Row) o1;
            Row r2 = (Row) o2;
            int arrivalCompare = Integer.compare(r1.getArrivalTime(), r2.getArrivalTime());
            return arrivalCompare != 0 ? arrivalCompare : r1.getProcessName().compareTo(r2.getProcessName());
        });

        List<Row> queue = new ArrayList<>();
        List<Row> rows = Utility.deepCopy(this.getRows());
        int currentTime = rows.get(0).getArrivalTime();
        int timeQuantum = this.getTimeQuantum();

        // Track original burst times for correct turnaround calculation
        Map<String, Integer> originalBurstTimes = new HashMap<>();
        rows.forEach(r -> originalBurstTimes.put(r.getProcessName(), r.getBurstTime()));

        while (!rows.isEmpty() || !queue.isEmpty()) {
            // Add newly arrived processes to queue
            while (!rows.isEmpty() && rows.get(0).getArrivalTime() <= currentTime) {
                queue.add(rows.remove(0));
            }

            if (!queue.isEmpty()) {
                Row currentProcess = queue.remove(0);
                int executionTime = Math.min(currentProcess.getBurstTime(), timeQuantum);

                this.getTimeline().add(new Event(currentProcess.getProcessName(),
                        currentTime,
                        currentTime + executionTime));

                currentProcess.setBurstTime(currentProcess.getBurstTime() - executionTime);
                currentTime += executionTime;

                if (currentProcess.getBurstTime() > 0) {
                    queue.add(currentProcess);
                }
            } else {
                // Idle time until next process arrives
                currentTime = rows.get(0).getArrivalTime();
            }
        }

        // Calculate waiting and turnaround times
        for (Row row : this.getRows()) {
            int waitingTime = 0;
            int lastFinishTime = 0;

            for (Event event : this.getTimeline()) {
                if (event.getProcessName().equals(row.getProcessName())) {
                    waitingTime += event.getStartTime() - Math.max(lastFinishTime, row.getArrivalTime());
                    lastFinishTime = event.getFinishTime();
                }
            }

            row.setWaitingTime(waitingTime);
            row.setTurnaroundTime(waitingTime + originalBurstTimes.get(row.getProcessName()));
        }
    }
}
