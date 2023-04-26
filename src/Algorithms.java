import java.io.*;
import java.util.*;



public class Algorithms {
    /**
     * Listy oraz zmienne przyjmujące dane ze strumienia wejściowego oraz listy i zmienne na których są przeprowadzane kolejne kroki algorytmów
     */
    static List<Integer> id = new ArrayList<>(100);
    static List<Integer> durationTime = new ArrayList<>(100);
    static List<Integer> arrivalTime = new ArrayList<>(100);
    static List<Integer> waitingTime = new ArrayList<>(100);
    static List<Integer> realizationTime = new ArrayList<>(100);
    static List<Integer> turnAroundTime = new ArrayList<>(100);

    static double fullTurnAroundTime = 0;
    static double fullWaitingTime = 0;

    static int tickPosition = 0;
    static int minValue = Integer.MAX_VALUE;
    static int expireTime = 0;
    static int finishTime;
    static int shortest = 0;
    static boolean check = false;
    static int counterOfFaults = 0;
    static int counterOfHits = 0;

    static ArrayList<Float> fcfs(ArrayList<Row> list, int n, int param, String fileName) {

        clearingListsAndVariables();
        sortingInitialization(list,param, n);

        for (int i = 0; i < arrivalTime.size(); i++) {
            if (i == 0) {
                realizationTime.add((arrivalTime.get(i) + durationTime.get(i)));
            } else {
                if (arrivalTime.get(i) > realizationTime.get(i - 1)) {
                    realizationTime.add((arrivalTime.get(i) + durationTime.get(i)));
                } else {
                    realizationTime.add(realizationTime.get(i - 1) + durationTime.get(i));
                }
            }
            turnAroundTime.set(i,realizationTime.get(i) - arrivalTime.get(i));
            waitingTime.set(i,turnAroundTime.get(i) - durationTime.get(i));
            fullWaitingTime += waitingTime.get(i);
            fullTurnAroundTime += turnAroundTime.get(i);
        }

        fullTurnAroundTime = fullTurnAroundTime / (turnAroundTime.size());
        fullWaitingTime = fullWaitingTime / (waitingTime.size());
        saveStatsToTheFile(fileName);
        saveSubmissionToTheFile(fileName,param);

        ArrayList<Float> data = new ArrayList<Float>(2);
        data.add((float)fullTurnAroundTime);
        data.add((float)fullWaitingTime);

        return data;

    }
    static ArrayList<Float> sjfw(ArrayList<Row> list, int n, int param, String fileName) {
        clearingListsAndVariables();
        sortingInitialization(list,param, n);

        ArrayList<Integer> duration = new ArrayList<Integer>();

        while(expireTime != list.get(n).getProcesses().size()){

            for (int i = 0; i < list.get(n).getProcesses().size(); i++) {
                duration.add(durationTime.get(i));
                if ((arrivalTime.get(i) <= tickPosition) &&
                        (durationTime.get(i) < minValue) && (durationTime.get(i) > 0)) {
                    minValue = durationTime.get(i);
                    shortest = i;
                    check = true;
                }
            }

            if (!check) {
                tickPosition++;
                continue;
            }

            durationTime.set(shortest, durationTime.get(shortest)-1);
            minValue = durationTime.get(shortest);

            if (minValue == 0) {
                minValue = Integer.MAX_VALUE;
            }

            if(durationTime.get(shortest)==0){

                expireTime++;
                check = false;
                finishTime= tickPosition +1;

                waitingTime.set(shortest, finishTime -duration.get(shortest)- arrivalTime.get(shortest));
                if(waitingTime.get(shortest)<0){
                    waitingTime.set(shortest,0);
                }

            }

            tickPosition++;
        }

        for(int i=0;i<list.get(n).getProcesses().size(); i++){
            turnAroundTime.set(i, duration.get(i)+waitingTime.get(i));

        }

        for(int i = 0; i< list.get(n).getProcesses().size(); i++){
            fullTurnAroundTime += turnAroundTime.get(i);
            fullWaitingTime += waitingTime.get(i);
        }

        fullTurnAroundTime = fullTurnAroundTime / (turnAroundTime.size());
        fullWaitingTime = fullWaitingTime / (waitingTime.size());
        saveStatsToTheFile(fileName);
        saveSubmissionToTheFile(fileName, param);

        ArrayList<Float> data = new ArrayList<>(2);
        data.add((float)fullTurnAroundTime);
        data.add((float)fullWaitingTime);

        return data;
    }
    static ArrayList<Float> lcfs(ArrayList<Row> list, int n, int param, String fileName) {
        clearingListsAndVariables();
        sortingInitialization(list,param, n);

        for (int i = 0; i < arrivalTime.size(); i++) {
            if (i == 0) {
                realizationTime.add((arrivalTime.get(i) + durationTime.get(i)));
            } else {
                if (arrivalTime.get(i) > realizationTime.get(i - 1)) {
                    realizationTime.add((arrivalTime.get(i) + durationTime.get(i)));
                } else {
                    realizationTime.add(realizationTime.get(i - 1) + durationTime.get(i));
                }
            }
            turnAroundTime.set(i,realizationTime.get(i) - arrivalTime.get(i));
            waitingTime.set(i,turnAroundTime.get(i) - durationTime.get(i));
            fullWaitingTime += waitingTime.get(i);
            fullTurnAroundTime += turnAroundTime.get(i);
        }

        fullTurnAroundTime = fullTurnAroundTime / (turnAroundTime.size());
        fullWaitingTime = fullWaitingTime / (waitingTime.size());
        saveStatsToTheFile(fileName);
        saveSubmissionToTheFile(fileName, param);

        ArrayList<Float> data = new ArrayList<>(2);
        data.add((float)fullTurnAroundTime);
        data.add((float)fullWaitingTime);

        return data;
    }
    static String fifo(List<Integer> input, int capacity, String fileName, int param, ArrayList<Integer> stats) {

        LinkedHashSet<Integer> frames = new LinkedHashSet<>(capacity);
        Queue<Integer> queue = new LinkedList<>() ;


        counterOfFaults=0;
        counterOfHits=0;

        for (Integer integer : input) {
            if (frames.size() < capacity) {
                if (!frames.contains(integer)) {

                    frames.add(integer);

                    queue.add(integer);

                    counterOfFaults++;
                }

            } else {
                if (!frames.contains(integer)) {

                    frames.remove(queue.poll());

                    frames.add(integer);

                    queue.add(integer);

                    counterOfFaults++;
                }

            }
        }
        counterOfHits= input.size()-counterOfFaults;
        saveSubmissionToTheFile(fileName, param);
        stats.add(counterOfFaults);
        return "Counter of faults: " + counterOfFaults + " Hits: " + (input.size()-counterOfFaults);
    }
    static String lfu(List<Integer> input, int capacity, String fileName, int param, ArrayList<Integer> stats) {
        HashMap<Integer,Integer> frames = new LinkedHashMap<>(Collections.max(input));
        ArrayList<Integer> bufor = new ArrayList<>(capacity);


        counterOfFaults=0;
        counterOfHits=0;

        for (Integer integer : input) {

            if (!frames.containsKey(integer)) {
                frames.put(integer, 1);
            }
            if (!bufor.contains(integer)) {

                if (bufor.size() < capacity) {

                    bufor.add(frames.get(integer));

                } else {
                    bufor.remove(0);
                    bufor.add(integer);
                }

                counterOfFaults++;
            } else {
                frames.replace(integer, frames.get(integer) + 1);
            }
            /////
            var entry = frames.entrySet();
            List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(entry);
            Collections.sort(entryList, new Comparator<Map.Entry<Integer, Integer>>() {
                @Override
                public int compare(Map.Entry<Integer, Integer> t1, Map.Entry<Integer, Integer> t2) {
                    return t1.getValue().compareTo(t2.getValue());
                }
            });
            frames.clear();
            entryList.forEach(v -> {
                frames.put(v.getKey(), v.getValue());
            });
            /////
        }
        counterOfHits= input.size()-counterOfFaults;
        saveSubmissionToTheFile(fileName, param);
        stats.add(counterOfFaults);
        return "Counter of faults: " + counterOfFaults + " Hits: " + (input.size()-counterOfFaults);
    }
    static void clearingListsAndVariables() {

        /**
         *Czyszczenie list i zmiennych odbywa się przed każdym wywołaniem metody
         */

        id.clear();
        durationTime.clear();
        arrivalTime.clear();
        waitingTime.clear();
        realizationTime.clear();
        turnAroundTime.clear();

        fullTurnAroundTime = 0;
        fullWaitingTime = 0;

        tickPosition = 0;
        minValue =Integer.MAX_VALUE;
        expireTime =0;
        shortest=0;
        check=false;

        counterOfFaults = 0;
        counterOfHits = 0;

    }
    static void sortingInitialization(ArrayList<Row> list, int param, int n) {

        /**
         * Zależnie od algorytmu dochodzi tutaj do sortowania w odpowiedni sposób
         * oraz pobrania danych wejściowych i zapisania ich do list będących kolekcjami pochodzącymi z klasy Algorithms
         */

        clearingListsAndVariables();

        var process = list.get(n).getProcesses();

        if(param==1){
            process.sort(Comparator.comparing(Process::getArrivalTime));
        }
        if (param==2){
            process.sort(Comparator.comparing(Process::getArrivalTime).reversed());
        }

        for (var p : process) {
            id.add(p.getId());
            durationTime.add(p.getDurationTime());
            arrivalTime.add(p.getArrivalTime());
        }
        for (int i =0;i<100;i++){
            waitingTime.add(0);
            turnAroundTime.add(0);
        }
    }
    static void saveStatsToTheFile(String name){
        /**
         * Zapis części statystyk do pliku przekazanego w argumencie metody
         */
        try {
            Writer fw = new BufferedWriter(new FileWriter(name, true));
            fw.append("Id: " +id + "\t\t\t\t\t" + "\njoinTime: "+ arrivalTime + "\t\t\t\t\t" + "\ndurationTime: " +
                    durationTime + "\t\t\t\t\t" + "\nrealizationTime: " + realizationTime + "\t\t\t\t\t" +
                    "\nturnAroundTime: " + turnAroundTime + "\t\t\t\t\t" + "\nwaitingTime: " + waitingTime);
            System.out.println();
            fw.close();
        } catch (Exception error) {
            System.out.println(error);
        }
    }
    static void saveSubmissionToTheFile(String name, int param){
        /**
         *Zapis pozostałych statystyk podsumowujących do plików
         */
        try {
            Writer fw = new BufferedWriter(new FileWriter(name, true));
            if(param<4){
                fw.append("\n\nAvarage turn around time: : " + fullTurnAroundTime + "\nAvarage waiting time: "+ fullWaitingTime + "\n\n\n");
            }else{
                fw.append("\nCounter of faults: " + counterOfFaults + " Hits: " + counterOfHits);
            }
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}

