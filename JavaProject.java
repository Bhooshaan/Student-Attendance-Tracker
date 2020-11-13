package com.company;
import java.io.*;
import java.util.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


interface display //To avoid clash between print method in multiple classes
{
    void print();
}

class StudentDetails implements display  //Super class containing details as protected parameters and has a print method to display
{
    protected String name,rno;
    protected String[] courses=new String[9];
    protected double[][] sub=new double[9][5];          //present,late,excused,unexcused,percent of 9 subjects
    StudentDetails(String rno)
    {
        this.rno=rno;
    }
    public void print()
    {
        int maxlen=50;
        System.out.println("\nROLL NUMBER: "+rno+"\nNAME: "+name+"\n");

        for(int i=0;i<50;i++)
        {
            System.out.print(" ");
        }

        System.out.println("\tP\tL\tE\tU\tPERCENTAGE");

        for(int i=0;i<9;i++)
        {
            int diff=maxlen-courses[i].length();
            System.out.print(courses[i]);
            for(int j=0;j<diff;j++)
            {
                if(i==4 &&  j>=diff-4)
                    continue;
                System.out.print(" ");
            }
            System.out.print("\t");
            for(int j=0;j<5;j++)
            {
                if (j == 4)
                {
                    System.out.format("%.2f\t",sub[i][j]);
                    if(sub[i][j]<75.00)
                    {
                        System.out.print("\t-LAG");
                    }
                }
                else
                {
                    System.out.format("%d\t",(int)(sub[i][j]));
                }
            }
            System.out.println();
        }
        System.out.println("\nP- PRESENT\nL- LATE\nE- EXCUSED LEAVE\nU- UNEXCUSED LEAVE");
    }
}

class Attendance extends StudentDetails  //Sub class of StudentDetails containing all important functions that calculates percentages and stores them in parameters
{
    Attendance(String rno)        //Constructor that initializes the parameters to values corresponding the given roll number
    {
        super(rno);
        this.store(rno);
        this.newFile();
    }
    void newFile()                  //Called to store the calculated percentages in the Percentages file
    {
        File f=new File("C:\\Users\\Bhooshaan\\Desktop\\Percentages.csv");
        try
        {
            CSVReader r=new CSVReader(new FileReader(f));
            List<String[]> csv = r.readAll();
            int rowno=-1;
            for (int i = 4; i < 75; i++)
            {
                if (csv.get(i)[0].equals(rno))
                {
                    rowno=i;
                    break;
                }
            }

            for(int i=0;i<9;i++)
            {
                csv.get(rowno)[i+2]=Double.toString(sub[i][4]);
            }
            FileWriter o=new FileWriter(new File("C:\\Users\\Bhooshaan\\Desktop\\Percentages.csv"));
            CSVWriter writer = new CSVWriter(o);
            writer.writeAll(csv);
            writer.flush();
            writer.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    void getPercent(ArrayList<String> arr)  //Actual arithmetic part of the class performing all calculations
    {
        name=arr.get(1);

        for(int i=2;i<38;i+=4)
        {
            double present = Integer.parseInt(arr.get(i));
            double late = Integer.parseInt(arr.get(i + 1));
            double e = Integer.parseInt(arr.get(i + 2));
            double u = Integer.parseInt(arr.get(i + 3));
            double total = present + late + e + u;
            double ll = late / 3;
            double p = present + e + late - ll;
            sub[i/4][0]=present;
            sub[i/4][1]=late;
            sub[i/4][2]=e;
            sub[i/4][3]=u;
            sub[i/4][4]=(p * 100) / total;
        }
    }

    void store(String rno)     //This method used getPercent to store calculated details of all students in the file
    {
        File f=new File("C:\\Users\\Bhooshaan\\Desktop\\Complete.csv");
        try
        {
            CSVReader reader = new CSVReader(new FileReader(f));
            List<String[]> csvBody = reader.readAll();
            ArrayList<ArrayList<String>> sheet = new ArrayList<ArrayList<String>>();
            int z = 0;
            for(int i=2;i<38;i+=4)
            {
                this.courses[i/4]=csvBody.get(0)[i];
            }
            for (int j = 4; j < 75; j++)
            {
                ArrayList<String> row = new ArrayList<String>();
                for (int i = 0; i < 38; i++)
                {
                    row.add(csvBody.get(j)[i]);
                }
                sheet.add(row);
                z++;
            }
            for (int i = 0; i < z; i++)
            {
                if (sheet.get(i).get(0).equals(rno))
                {
                    getPercent(sheet.get(i));
                    break;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

class Teacher implements display   //Class containing parameters and methods related to teacher access
{
    protected String coursecode;
    protected int count=0;
    protected ArrayList<ArrayList<String>> stud= new ArrayList<>();  //Arraylist storing defaulters of each subject
    Teacher(String c)    //Constructor
    {
        coursecode=c;
        Students();
    }
    public void print()     //print  method printing all defaulters of the required course
    {
        System.out.println("Course Code: "+coursecode);
        System.out.println("Number of students lacking attendance: "+count);
        if(count==0)
        {
            System.out.println("All Students have ample Attendance");
        }
        else
        {
            for(int i=0;i<count;i++)
            {
                System.out.println("\nStudent "+(int)(i+1));
                System.out.println("R.NO: "+stud.get(i).get(0));
                System.out.println("Name: "+stud.get(i).get(1));
                System.out.format("Attendance Percentage: %.2f\n",Double.parseDouble(stud.get(i).get(2)));
            }
        }
    }
    void Students()         //Method that loads the array list with defaulters of the course
    {
        File f=new File("C:\\Users\\Bhooshaan\\Desktop\\Percentages.csv");
        try
        {
            CSVReader reader = new CSVReader(new FileReader(f));
            List<String[]> csvBody = reader.readAll();
            ArrayList<ArrayList<String>> sheet = new ArrayList<ArrayList<String>>();
            int z = 0;
            for (int j = 3; j < 75; j++)
            {
                ArrayList<String> row = new ArrayList<String>();
                for (int i = 0; i < 11; i++)
                {
                    row.add(csvBody.get(j)[i]);
                }
                sheet.add(row);
                z++;
            }
            int code=-1;
            for(int i=0;i<9;i++)
            {
                if(sheet.get(0).get(i+2).equals(coursecode))
                {
                    code=i+2;
                    break;
                }
            }
            for(int i=1;i<z;i++)
            {
                double percen=Double.parseDouble(sheet.get(i).get(code));
                if(percen<75.00)
                {
                    ArrayList<String> row=new ArrayList<String>();
                    row.add(sheet.get(i).get(0));
                    row.add(sheet.get(i).get(1));
                    row.add(sheet.get(i).get(code));
                    stud.add(row);
                    count++;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

class Tutor         //Class with privileged access to tutor as access to both specific student and course is granted
{
    int choice;
    String Password;
    Tutor() {
        try
        {
            BufferedReader b = new BufferedReader(new FileReader("C:\\Users\\Bhooshaan\\Desktop\\pw.txt"));
            Password = b.readLine();
            b.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        choice=0;
    }   //Constructor of tutor that initialises the password by reading a file
    void getchoice()        //Method to get the choice of action to be performed
    {
        while(true)
        {
            System.out.print("Welcome to BE CSE G2:\n");
            System.out.println("\n1- GET SPECIFIC STUDENT DETAILS");
            System.out.println("2- GET COURSE DETAILS");
            System.out.println("3- CHANGE PASSWORD");
            System.out.print("ENTER YOUR CHOICE: ");
            Scanner sc = new Scanner(System.in);
            int c = 0;
            c = sc.nextInt();
            sc.nextLine();
            if (c < 1 || c > 3) {
                System.out.println("Invalid Choice");
                continue;
            }
            choice=c;
            break;
        }
    }
    boolean checkPassword(String s)     //Checks whether the password passed equals the original password
    {
        return s.equals(Password);
    }
    boolean setPassword()       //By default password will be BECSEG2 and this method allows to change the password
    {
        Scanner sc=new Scanner(System.in);
        int turns=5;
        while(turns>0)
        {
            System.out.print("Enter Password ("+turns+" turns left): ");
            String pass=sc.nextLine();
            if(checkPassword(pass))
            {
                break;
            }
            turns--;
        }
        if(turns==0)
        {
            System.out.println("No Turns Left");
            System.exit(0);
        }
        else
        {
            while(true)
            {
                System.out.print("Enter New Password: ");
                String newpass = sc.nextLine();
                System.out.print("Re-Enter Password : ");
                String x=sc.nextLine();
                if(x.equals(newpass))
                {
                    try
                    {
                        System.out.println("Password Changed");
                        Password = newpass;
                        PrintWriter p = new PrintWriter(new FileWriter("C:\\Users\\Bhooshaan\\Desktop\\pw.txt"));
                        p.println(newpass);
                        p.flush();
                        p.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return true;
                }
                else
                {
                    System.out.print("Wrong Entry\nPress 0 to retry: ");
                    int retry=sc.nextInt();
                    sc.nextLine();
                    if(retry==0)
                    {
                        continue;
                    }
                    return false;
                }
            }
        }
        return false;
    }
    void getInfo()      //Gives specific student info or specific course info or enables the innterface to change password
    {
        int flag=0;
        Scanner sc=new Scanner(System.in);
        if(choice==1)
        {
            while (flag==0)
            {
                System.out.print("Please enter your Register Number:");
                String r = sc.nextLine();
                r = r.toUpperCase();
                try {
                    JavaProject.validate(r);
                    Attendance s = new Attendance(r);
                    s.print();
                    flag=1;

                }
                catch (InvalidRno e)
                {
                    System.out.println(e);
                }
            }
        }
        else if(choice==2)
        {
            while(flag==0)
            {
                System.out.print("Please enter the course code:");
                String c = sc.nextLine();
                c = c.toUpperCase();
                try
                {
                    JavaProject.check(c);
                    Teacher t = new Teacher(c);
                    t.print();
                    flag = 1;
                }
                catch (InvalidCourse e)
                {
                    System.out.println(e);
                }
            }
        }
        else
        {
            setPassword();
        }
    }
}

class createBasics          //Class that actually prepares all basic files required for processing data
{
    void prepare()      //When this method is called,total attendance for 3 months and percentages of all students are written in a file
    {
        File f1=new File("C:\\Users\\Bhooshaan\\Desktop\\August.csv");
        File f2=new File("C:\\Users\\Bhooshaan\\Desktop\\September.csv");
        File f3=new File("C:\\Users\\Bhooshaan\\Desktop\\October.csv");
        try
        {
            CSVReader r1 = new CSVReader(new FileReader(f1));
            CSVReader r2 = new CSVReader(new FileReader(f2));
            CSVReader r3 = new CSVReader(new FileReader(f3));
            List<String[]> csv1 = r1.readAll();
            List<String[]> csv2 = r2.readAll();
            List<String[]> csv3 = r3.readAll();

            FileWriter o=new FileWriter(new File("C:\\Users\\Bhooshaan\\Desktop\\Complete.csv"));
            CSVWriter writer = new CSVWriter(o);
            writer.writeAll(csv1);
            writer.flush();
            writer.close();

            CSVReader r4 = new CSVReader(new FileReader(new File("C:\\Users\\Bhooshaan\\Desktop\\Complete.csv")));
            List<String[]> csv4 = r4.readAll();
            for(int i=4;i<75;i++)
            {
                for(int j=2;j<38;j++)
                {
                    String tot=Integer.toString(Integer.parseInt(csv1.get(i)[j])+Integer.parseInt(csv2.get(i)[j])+Integer.parseInt(csv3.get(i)[j]));
                    csv4.get(i)[j]=tot;
                }
            }
            o=new FileWriter(new File("C:\\Users\\Bhooshaan\\Desktop\\Complete.csv"));
            writer = new CSVWriter(o);
            writer.writeAll(csv4);
            writer.flush();
            writer.close();

            for(int i=4;i<75;i++)
            {
                Attendance a=new Attendance(csv4.get(i)[0]);
            }
            r1.close();
            r2.close();
            r3.close();
            r4.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

class InvalidRno extends Exception      //User defined exception
{
    String w;
    InvalidRno(String s)
    {
        w=s;
    }
    public String toString()
    {
        return w+" Try Again!";
    }
}

class InvalidCourse extends Exception   //User defined exception
{
    String w;
    InvalidCourse(String s)
    {
        w=s;
    }
    public String toString()
    {
        return w+" Try Again!";
    }
}

public class JavaProject                //Class that contains main method
{
    public static void validate(String r) throws InvalidRno     //Method that checks whether entered roll number is valid or not
    {
        if(r.length()==6)
        {
            if(!(r.substring(0,4).equals("19Z3")))
            {
                throw new InvalidRno("Invalid RollNumber");
            }
            else
            {
                int n=Integer.parseInt(r.substring(4,6));
                if(n<1 || n>64 || n==19 || n==25)
                {
                    throw new InvalidRno("Invalid RollNumber");
                }
            }

        }
        else if(r.length()==9)
        {
            if(!(r.substring(0,6).equals("19IZUS")))
            {
                throw new InvalidRno("Invalid RollNumber");
            }
            else
            {
                int n=Integer.parseInt(r.substring(6,9));
                if(n<9 || n>17)
                {
                    throw new InvalidRno("Invalid RollNumber");
                }
            }
        }
        else
        {
            throw new InvalidRno("Invalid RollNumber");
        }
    }
    public static void check(String r) throws InvalidCourse     //Method that checks whether entered course code is valid or not
    {
        if(r.length()==6)
        {
            if(!(r.substring(0,4).equals("19Z3")) && !(r.substring(0,4).equals("19K3")) && !(r.substring(0,4).equals("19O3")))
            {
                throw new InvalidCourse("Invalid Course Code");
            }
            else
            {
                int n=Integer.parseInt(r.substring(4,6));
                if(n<1)
                {
                    throw new InvalidCourse("Invalid Course Code");
                }
                else if(n>5 && n<10)
                {
                    throw new InvalidCourse("Invalid Course Code");
                }
                else if(n>12)
                {
                    throw new InvalidCourse("Invalid Course Code");
                }
            }
        }
        else
        {
            throw new InvalidCourse("Invalid Course Code");
        }
    }
    public static void main(String[] args)      //Main method that acts as bridge between all classes and methods
    {
        Scanner sc=new Scanner(System.in);
        createBasics b=new createBasics();
        b.prepare();
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tSTUDENT ATTENDANCE TRACKER");
        System.out.println("1- STUDENT");
        System.out.println("2- TEACHER");
        System.out.println("3- TUTOR");
        System.out.print("Please enter the choice:");
        int ch=sc.nextInt();
        sc.nextLine();
        int flag=0;
        while(flag==0)
        {
            if (ch == 1)
            {
                System.out.print("Please enter your Register Number:");
                String r = sc.nextLine();
                r=r.toUpperCase();
                try
                {
                    validate(r);
                    Attendance s = new Attendance(r);
                    s.print();
                    flag = 1;

                }
                catch (InvalidRno e)
                {
                    System.out.println(e);
                }
            }
            else if(ch==2)
            {
                System.out.print("Please enter the course code:");
                String c=sc.nextLine();
                c=c.toUpperCase();
                try
                {
                    check(c);
                    Teacher t=new Teacher(c);
                    t.print();
                    flag=1;
                }
                catch(InvalidCourse e)
                {
                    System.out.println(e);
                }
            }
            else if(ch==3)
            {
                Tutor tu=new Tutor();
                int turns=5;
                while(turns>0)
                {
                    System.out.print("Enter Password ("+turns+" turns left): ");
                    String pass=sc.nextLine();
                    if(tu.checkPassword(pass))
                    {
                        break;
                    }
                    turns--;
                }
                if(turns==0)
                {
                    System.out.println("No Turns Left");
                    System.exit(0);
                }
                tu.getchoice();
                tu.getInfo();
            }
            System.out.println("Enter 0 to continue: ");
            flag=sc.nextInt();
            sc.nextLine();


        }
    }
}

