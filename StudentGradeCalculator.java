import java.util.Scanner;

public class StudentGradeCalculator {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Student Name: ");
        String name = sc.nextLine();

        System.out.print("Enter marks in Java: ");
        int java = sc.nextInt();

        System.out.print("Enter marks in Electronics: ");
        int electronics = sc.nextInt();

        System.out.print("Enter marks in Mathematics: ");
        int maths = sc.nextInt();

        System.out.print("Enter marks in Physics: ");
        int physics = sc.nextInt();

        System.out.print("Enter marks in C Programming: ");
        int c = sc.nextInt();

        int total = java + electronics + maths + physics + c;
        double percentage = total / 5.0;

        char grade;

        if (percentage >= 90) {
            grade = 'A';
        } else if (percentage >= 75) {
            grade = 'B';
        } else if (percentage >= 60) {
            grade = 'C';
        } else if (percentage >= 50) {
            grade = 'D';
        } else {
            grade = 'F';
        }

        System.out.println("\n----- Student Result -----");
        System.out.println("Name: " + name);
        System.out.println("Total Marks: " + total + "/500");
        System.out.println("Percentage: " + percentage + "%");
        System.out.println("Grade: " + grade);

        if (percentage >= 40) {
            System.out.println("Result: PASS");
        } else {
            System.out.println("Result: FAIL");
        }

        sc.close();
    }
}
