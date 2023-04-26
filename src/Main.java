import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        int choice = 0;
        int numberOfIterations;
        int frameSize = ThreadLocalRandom.current().nextInt(3, 7);
        String fileName= null;
        String fileName2= null;

        var rows = new ArrayList<Row>(100);
        var pages = pageReplacementValuesGenerator(20,1);
        var choices = new ArrayList<String>(4);
        var statsTable = new ArrayList<ArrayList<Float>>();
        var statsTableForPages = new ArrayList<Integer>();

        /**
         * Przechowywanie nazw plików za pomocą listy
         */
        ArrayList<String> files = new ArrayList<>(5);
        files.add(0,"FCFSwyniki.txt");
        files.add(1,"LCFSwyniki.txt");
        files.add(2,"SJFWwyniki.txt");
        files.add(3,"FIFOwyniki.txt");
        files.add(4,"LFUwyniki.txt");

        /**
         * Wygenerowanie zbioru danych do dalszych operacji
         */
        for (int i = 0; i < 100; i++) {
            var row = new Row(i);
            for (int j = 0; j < 100; j++) {
                var process = new Process(j + i * 100,
                        ThreadLocalRandom.current().nextInt(1, 21),
                        ThreadLocalRandom.current().nextInt(0, 100));

                row.addProces(process);
            }
            rows.add(row);
        }
        /**
         * Wywołanie menu
         * Wczytywanie parametrów wprowadzanych przez użytkownika do zmiennych
         */
        choices=menu();
        choice=Integer.parseInt(choices.get(0));
        fileName=choices.get(1);
        fileName2=choices.get(3);
        int variable = Integer.parseInt(choices.get(2));
        numberOfIterations=variable/100;

        if(choice==1) {
            rows.clear();
            rows=reader(fileName);
            pages.clear();
            pages=pageReader(fileName2);
            frameSize = Integer.parseInt(choices.get(4));
        }

        var clonedPages = makeDeepCopyInteger(pages);
        var clonedList = clonedList(rows);

        clearingFiles(files);
        /**
         * Wykonanie algorytmów zadaną numberOfIterations razy
         */
        for(int n=0;n<numberOfIterations;n++) {

            statsTable.add(Algorithms.fcfs(clonedList, n, 1, files.get(0)));
            clonedList = clonedList(rows);

            statsTable.add(Algorithms.lcfs(clonedList, n, 2, files.get(1)));
            clonedList = clonedList(rows);

            statsTable.add(Algorithms.sjfw(clonedList, n, 3, files.get(2)));
            clonedList = clonedList(rows);

            if(choice != 1) {
                Algorithms.fifo(clonedPages, ThreadLocalRandom.current().nextInt(3, 7), files.get(3), 5, statsTableForPages);
                clonedPages = makeDeepCopyInteger(pages);

                Algorithms.lfu(clonedPages, ThreadLocalRandom.current().nextInt(3, 7), files.get(4), 5, statsTableForPages);
                clonedPages = makeDeepCopyInteger(pages);

                pages = pageReplacementValuesGenerator(20, 1);
            }
        }
        if (choice == 1){
            Algorithms.fifo(clonedPages, frameSize, files.get(3), 5, statsTableForPages);
            clonedPages = makeDeepCopyInteger(pages);

            Algorithms.lfu(clonedPages, frameSize, files.get(4), 5, statsTableForPages);
            clonedPages = makeDeepCopyInteger(pages);
        }

        /**
         * Metoda display wyświetlająca podsumowanie wyników
         */
        display(statsTable, statsTableForPages, numberOfIterations, choice, frameSize);
    }
    static ArrayList<Row> reader(String fileName){
        /**
         * Odczyt danych dla algorytmów przydziału czasu procesora z pliku
         */
        var rows = new ArrayList<Row>();

        String txtFile = fileName;
        BufferedReader br = null;
        String line = "";
        String txtSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(txtFile));
            br.readLine();
            int x = 0;
            int counter = 0;
            for (;;) {
                var row = new Row(x);
                while ((line = br.readLine()) != null && counter < 100) {

                    counter++;
                    if(counter == 99)
                    {
                        x++;
                    }

                    String[] a = line.split(txtSplitBy);
                    var process = new Process(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
                    row.addProces(process);
                }
                counter=0;
                rows.add(row);
                if((line = br.readLine()) == null){
                    break;
                }
            }}catch(FileNotFoundException e){
        }catch(IOException e){
        }

        return rows;
    }
    static ArrayList<Integer> pageReader(String fileName) throws FileNotFoundException {
        /**
         * Odczyt danych dla algorytmów zastępowania stron z pliku
         */
        Scanner scanner = new Scanner(new File(fileName));
        var list = new ArrayList<Integer>();

        while(scanner.hasNextInt())
        {

            list.add(scanner.nextInt());

        }
        return list;
    }
    static ArrayList<Row> clonedList(ArrayList<Row> list) {
        /**
         * Tzw. Deep copy danych wejściowych, aby móc wywoływać różne algorytmy za pomocą tych samych danych wejściowych
         */
        ArrayList<Row> newList = new ArrayList<>(100);
        for (var rows : list) {
            newList.add(rows.clone());
        }
        return newList;

    }
    static ArrayList pageReplacementValuesGenerator(int max, int min) {
        /**
         * Metoda generująca numery stron o określonych w zadaniu parametrach
         */
        ArrayList<Integer> list = new ArrayList<>(100);
        var ints = IntStream.range(min, max+1).boxed().collect(toList());

        for(;;) {
            for (int i = 0; i < 100; i++) {
                list.add(ThreadLocalRandom.current().nextInt(1, max+1));
            }
            if (list.containsAll(ints)) {
                break;
            } else {
                list.clear();
            }
        }
        return list;
    }
    static ArrayList<Integer> makeDeepCopyInteger(ArrayList<Integer> a){
        /**
         * Tzw. Deep copy danych dla algorytmów zastępowania stron
         */
        return (ArrayList<Integer>) a.stream().map(val -> Integer.valueOf(val)).collect(toList());
    }
    static void clearingFiles(List<String> file){
        /**
         * Usuwanie zawartości plików w których zapisywane są dany wyjściowe
         */
        file.forEach(it -> {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(it);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            writer.print("");
            writer.close();
        });

    }
    public static ArrayList<String> menu() {

        /**
         * Menu- interfejs dla użytkownika
         */
        System.out.println("__________Symulacja__________");
        System.out.println("Czy chcesz przeprowadzic operacje na danych z pliku?  1- TAK  2-NIE");
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        String choice2 = String.valueOf(10000);
        choice = scanner.nextInt();
        String choice3 = null;
        String choice4 = null;
        ;
        String path = null;
        if (choice == 1) {
            System.out.println("Wprowadz sciezke pliku dla algorytmów przydzialu czasu procesora: ");
            Scanner scanner2 = new Scanner(System.in);
            path = scanner2.nextLine();
            System.out.println("Podaj liczbę procesów w pliku: ");
            Scanner scanner3 = new Scanner(System.in);
            choice2 = scanner3.nextLine();
            System.out.println("Wprowadz sciezke pliku dla algorytmu zamieniania stron: ");
            Scanner scanner4 = new Scanner(System.in);
            choice3 = scanner4.nextLine();
            System.out.println("Wprowadz wprowdz rozmiar ramki ( bufora ): ");
            Scanner scanner5 = new Scanner(System.in);
            choice4 = scanner5.nextLine();
        }
        var choices = new ArrayList<String>(3);
        choices.add(String.valueOf(choice));
        choices.add(path);
        choices.add(choice2);
        choices.add(choice3);
        choices.add(choice4);

        return choices;
    }
    static void display(ArrayList<ArrayList<Float>> statsTable, ArrayList<Integer> statsTableForPages, int x, int choice, int frameSize){
        /**
         * Metoda obliczająca średnie parametry ze wszystkich wywołań algorytmów oraz wyświetlająca je na ekranie w sposób sformatowany
         */

        Float sumWtFcfs = 0.0F;
        Float sumWtLcfs = 0.0F;
        Float sumWtSjfw = 0.0F;

        Float sumTatFcfs = 0.0F;
        Float sumTatLcfs = 0.0F;
        Float sumTatSjfw = 0.0F;

        Float sumFaultsFifo = 0.0F;
        Float sumFaultsLfu = 0.0F;
        Float hundred = 100.0F;

        for (int i = 0; i < x; i++) {

            sumWtFcfs += statsTable.get(3 * i).get(0);
            sumWtLcfs += statsTable.get(1 + 3 * i).get(0);
            sumWtSjfw += statsTable.get(2 + 3 * i).get(0);

            sumTatFcfs += statsTable.get(3 * i).get(1);
            sumTatLcfs += statsTable.get(1 + 3 * i).get(1);
            sumTatSjfw += statsTable.get(2 + 3 * i).get(1);
            if(choice != 1) {
                sumFaultsFifo += statsTableForPages.get(2 * i);
                sumFaultsLfu += statsTableForPages.get(1 + 2 * i);
            }

        }
        sumWtFcfs = sumWtFcfs / x;
        sumWtLcfs = sumWtLcfs / x;
        sumWtSjfw = sumWtSjfw / x;

        sumTatFcfs = sumTatFcfs / x;
        sumTatLcfs = sumTatLcfs / x;
        sumTatSjfw = sumTatSjfw / x;

        sumFaultsFifo = (sumFaultsFifo * 100) / (x * (statsTableForPages.size() / 2));
        sumFaultsLfu = (sumFaultsLfu * 100) / (x * (statsTableForPages.size() / 2));

        if ( choice == 1){
            sumFaultsFifo = 0.0F;
            sumFaultsLfu = 0.0F;
            int a=0, b=1;
            for ( int i = 0; i<statsTableForPages.size()/2;i++){
                sumFaultsFifo += statsTableForPages.get(a);
                sumFaultsLfu += statsTableForPages.get(b);
                a += 2;
                b += 2;
            }
            sumFaultsFifo = (sumFaultsFifo)/(statsTableForPages.size()/2);
            sumFaultsLfu = (sumFaultsLfu)/(statsTableForPages.size()/2);
        }

        System.out.println("+----------------------+-----------------------+---------------------------+");
        System.out.println("|    Algorithm name    | Avarage waiting time/ | Avarage turn around time/ |");
        System.out.println("|    ______________    | Avarage faults counter|    Avarage hits counter   |");
        System.out.println("+----------------------+----------------------+--------------------------+");
        System.out.println("|         FCFS" + "                 " +sumWtFcfs + "              " + sumTatFcfs);
        System.out.println("+----------------------+----------------------+--------------------------+");
        System.out.println("|         LCFS" + "                 " +sumWtLcfs + "              " + sumTatLcfs);
        System.out.println("+----------------------+----------------------+--------------------------+");
        System.out.println("|         SJFw" + "                 " +sumWtSjfw + "              " + sumTatSjfw);
        System.out.println("+----------------------+----------------------+--------------------------+");
        System.out.println("|         FIFO" + "                 " +sumFaultsFifo + " %            " + (hundred-sumFaultsFifo) + " %");
        System.out.println("+----------------------+----------------------+--------------------------+");
        System.out.println("|         LFU " + "                 " +sumFaultsLfu +  " %            " + (hundred-sumFaultsLfu)  + " %");
        System.out.println("+----------------------+----------------------+--------------------------+");
        System.out.println("|  FIFO/LFU Frame size:            "+ frameSize                            );
        System.out.println("+----------------------+----------------------+--------------------------+");
    }
}


