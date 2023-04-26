import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Row implements Serializable, Cloneable {


    private static final long serialVersionUID = 1L;
    private final int id;
    private final List<Process> processes;

    public Row(int id) {
        processes = new ArrayList<>(100);
        this.id = id;

    }

    @Override
    protected Row clone() {
        Row row = new Row(id);
        for(var proc: processes) {
            row.processes.add(proc.clone());
        }
        return row;
    }

    void addProces(Process process){
        processes.add(process);
    }

    public List<Process> getProcesses() {
        return processes;
    }

    @Override
    public String toString() {
        return "Row{" +
                "id=" + id +
                ", processes=" + processes +
                '}';
    }
}
