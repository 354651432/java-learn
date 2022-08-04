package org.example;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws ParseException, IOException {
        try (Sum sumObj = new Sum("data.csv"); Sum aObj = new A("data.csv"); Sum bObj = new B("data.csv"); Sum cObj = new C("data.csv")) {
            int sum = sumObj.summary();
            int a = aObj.summary();
            int b = bObj.summary();
            int c = cObj.summary();

            System.out.printf("all: %d, A: %d, B: %d, C: %d%n", sum, a, b, c);
            double x = (a + b) * 0.6;
            double y = (a + b) * 0.4 + c * 0.55;
            System.out.printf("X: %g, Y: %g%n", x, y);
        }
    }

    static void genateData() throws IOException {
        try (FileWriter fileWriter = new FileWriter("data.csv")) {
            Random random = new Random();
            Random minuteRandom = new Random();
            Random countRandom = new Random();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            for (int i = 0; i < 100; i++) {
                calendar.add(Calendar.DATE, 1);
                for (int j = 0; j < countRandom.nextInt(30); j++) {
                    calendar.add(Calendar.MINUTE, minuteRandom.nextInt(20) % 60);
                    fileWriter.write(String.format(Locale.PRC, "%s,%d\n", dateFormat.format(calendar.getTime()), Math.abs(random.nextInt(1000))));
                }
            }
        }
    }

    static class Sum implements Closeable {
        BufferedReader bufferedReader;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Sum(String path) throws IOException {
            FileReader fileReader = new FileReader(path);
            bufferedReader = new BufferedReader(fileReader);
        }

        @Override
        public void close() throws IOException {
            bufferedReader.close();
        }

        protected boolean F(Calendar calendar, int value) {
            return true;
        }

        int summary() throws IOException, ParseException {
            String line;
            int sum = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] arr = line.split(",");
                if (arr.length < 2) {
                    continue;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(arr[0]));

                int value = Integer.parseInt(arr[1]);
                if (F(calendar, value)) {
                    sum += Integer.parseInt(arr[1]);
                }
            }
            return sum;
        }
    }

    static class A extends Sum {

        A(String path) throws IOException {
            super(path);
        }

        @Override
        protected boolean F(Calendar calendar, int value) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            return hour < 20 || hour >= 22 && minute >= 30;
        }
    }

    static class B extends A {

        B(String path) throws IOException {
            super(path);
        }

        @Override
        protected boolean F(Calendar calendar, int value) {
            if (super.F(calendar, value)) {
                return false;
            }

            int week = calendar.get(Calendar.DAY_OF_WEEK);
            return week == 1 || week == 7;
        }
    }

    static class C extends Sum {

        C(String path) throws IOException {
            super(path);
        }

        @Override
        protected boolean F(Calendar calendar, int value) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            return hour >= 20 && hour < 23;
        }
    }
}
