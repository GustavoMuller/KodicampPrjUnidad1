import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int op;

        do {
            System.out.print("""
                
                =============== REGISTRO DE NOTAS ===============
                Menú:
                1. Ingresar registros.
                2. Ver registros.
                3. Salir.
                
                """);

                //Verificando que la opción ingresada es válida
                while (true){
                    System.out.print("Opción: ");
                    try {
                        op = scan.nextInt();
    
                        if (op >= 1 && op <= 3)
                            break;
                        else
                            System.out.println("¡Ingrese una opción válida!");
                    } catch (InputMismatchException e) {
                        System.out.println("¡Ingrese un valor numérico válido!");
                        scan.nextLine();
                    }
                }

            switch (op) {
                case 1 -> saveData(readConsoleData(scan));
                case 2 -> readData();
                case 3 -> System.out.println("\nSaliendo...");
            }

        } while (op != 3);
    }

    public static LinkedHashMap<String,Float> readConsoleData(Scanner s){
        LinkedHashMap<String,Float> grades = new LinkedHashMap<>();
        float grade;
        String name;
        int n;

        //verificando que el número ingresado es válido
        while (true){
            System.out.print("\nIngrese el número de registros a guardar: ");
            try {
                n = s.nextInt();

                if (n > 0)
                    break;
                else
                    System.out.println("¡Ingrese un valor mayor a cero!");
            } catch (InputMismatchException e) {
                System.out.println("¡Ingrese un valor numérico válido!");
                s.nextLine();
            }
        }

        //Lectura de N registros
        for (int i = 1; i <= n; i++) {
            System.out.println("\nREGISTRO " + i);
            System.out.print("\tNombre: ");
            s.nextLine();
            name = s.nextLine();

            while (true){
                System.out.print("\tNota: ");
                try {
                    grade = s.nextFloat();

                    if (grade >= 0 && grade <= 10)
                        break;
                    else
                        System.out.println("\t¡Ingrese un valor entre 1 y 10!");
                } catch (InputMismatchException e) {
                    System.out.println("\t¡Ingrese un valor numérico válido!");
                    s.nextLine();
                }
            }

            grades.put(name,grade);
        }

        return grades;
    }

    public static void saveData(LinkedHashMap<String,Float> data){
        try {
            StringBuilder sb = new StringBuilder();
            PrintStream fileStream = new PrintStream("data.txt");

            //Guardando registros de notas
            for (String name : data.keySet()) {
                fileStream.println(sb.append(name).append("|").append(data.get(name)));
                sb.setLength(0);
            }

            //Concatenando las estadísticas
            sb.append("\n*Estadísticas de los registros: ");
            sb.append("\nMáximo: ").append(getMax(data));
            sb.append("\nMínimo: ").append(getMin(data));
            sb.append("\nPromedio: ").append(getAverage(data));

            ArrayList<Float> repetitions = getMostRepeated(data);
            int timesRepeated = repetitions.get(repetitions.size()-1).intValue();
            repetitions.remove(repetitions.size()-1);
            sb.append("\nMás repetido: ").append(Arrays.toString(repetitions.toArray())).append(". Repeticiones: ").append(timesRepeated);

            repetitions = getLeastRepeated(data);
            timesRepeated = repetitions.get(repetitions.size()-1).intValue();
            repetitions.remove(repetitions.size()-1);
            sb.append("\nMenos repetido: ").append(Arrays.toString(repetitions.toArray())).append(". Repeticiones: ").append(timesRepeated);

            //Imprimiendo las estadísticas
            System.out.println(sb);
            fileStream.println(sb);

            fileStream.close();
        } catch (IOException e){
            System.out.println("Ha ocurrido un error.");
            e.printStackTrace();
        }
    }

    public static void readData(){
        try {
            File f = new File("data.txt");
            Scanner s = new Scanner(f);
            String line;
            String[] student;
            StringBuilder sb = new StringBuilder();

            //Leer cada linea
            while (s.hasNextLine()){
                line = s.nextLine();

                //Solo leer las lineas con registros
                if (!line.isEmpty() && line.charAt(0) != '*'){
                    student = line.split("\\|");
                    sb.append("\t").append(student[0]).append(" => ").append(student[1]);
                    System.out.println(sb);
                    sb.setLength(0);
                }
                else{
                    break;
                }
            }

            s.close();
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no existe!");
        }
    }

    public static float getMax(LinkedHashMap<String,Float> data){
        float max = 0;

        for (float x: data.values())
            if (x > max) max = x;

        return max;
    }

    public  static float getMin(LinkedHashMap<String,Float> data){
        float min = 10;

        for (float x: data.values())
            if (x < min) min = x;

        return min;
    }

    public static float getAverage(LinkedHashMap<String,Float> data){
        float sum = 0;

        for (float x: data.values())
            sum += x;

        return sum / data.size();
    }

    public static ArrayList<Float> getMostRepeated(LinkedHashMap<String,Float> data){
        ArrayList<Float> values = new ArrayList<>(data.values());
        LinkedHashMap<Float,Integer> counts = new LinkedHashMap<>();
        ArrayList<Float> mostRepeated = new ArrayList<>();

        int count = 0;
        int currentCount = 0;

        for (float currentVal : values) {
            //Si el valor actual ya fue contado, se pasa al siguiente valor
            if (!counts.containsKey(currentVal)){
                //contando las repeticiones
                for (float val : values)
                    if (currentVal == val) currentCount++;

                //Se guarda el valor y su cuenta
                counts.put(currentVal, currentCount);

                //Evaluando el resultado
                if (currentCount > count) count = currentCount;

                //Se reinicia la cuenta
                currentCount = 0;
            }
        }

        //Se compara que valores coinciden con la cuenta más repetida
        for (Map.Entry<Float,Integer> entry : counts.entrySet())
            if (Objects.equals(count, entry.getValue()))
                mostRepeated.add(entry.getKey());

        mostRepeated.add((float) count);

        return mostRepeated;
    }

    public static ArrayList<Float> getLeastRepeated(LinkedHashMap<String,Float> data){
        ArrayList<Float> values = new ArrayList<>(data.values());
        LinkedHashMap<Float,Integer> counts = new LinkedHashMap<>();
        ArrayList<Float> leastRepeated = new ArrayList<>();

        int count = values.size()+1;
        int currentCount = 0;

        for (float currentVal : values) {
            //Si el valor actual ya fue contado, se pasa al siguiente valor
            if (!counts.containsKey(currentVal)) {
                //contando las repeticiones
                for (float val : values) 
                    if (currentVal == val) currentCount++;

                //Se guarda el valor y su cuenta
                counts.put(currentVal, currentCount);

                //Evaluando el resultado
                if (currentCount < count) count = currentCount;

                //Se reinicia la cuenta
                currentCount = 0;
            }
        }

        //Se compara que valores coinciden con la cuenta menos repetida
        for (Map.Entry<Float,Integer> entry : counts.entrySet())
            if (Objects.equals(count, entry.getValue()))
                leastRepeated.add(entry.getKey());

        leastRepeated.add((float) count);

        return leastRepeated;
    }
}
