import java.io.Serializable;

public class Process implements Serializable, Cloneable {

    private static final long serialVersionUID = 2L;
    private int id;
    private int durationTime;
    private int arrivalTime;

    public Process(int id, int durationTime, int arrivalTime){

        this.id = id;
        this.durationTime = durationTime;
        this.arrivalTime = arrivalTime;

    }

    @Override
    protected Process clone() {
        Process p = new Process(id, durationTime, arrivalTime);
        p.id = this.id;
        p.durationTime = this.durationTime;
        p.arrivalTime = this.arrivalTime;
        return p;
    }

    public int getId() {
        return id;
    }

    public int getDurationTime() {
        return durationTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public String toString() {
        return "Process{" +
                "pid=" + id +
                ", duration=" + durationTime +
                ", joinTime=" + arrivalTime +
                '}';
    }
}
