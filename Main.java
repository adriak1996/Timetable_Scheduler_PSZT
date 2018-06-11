package com.company;


public class Main {

    public static void main(String[] args) {
	// write your code here
        FirstMatch firstMatch = new FirstMatch();
        SecondMatch secondMatch = new SecondMatch();

        Scheduler.GenerateInputData();

        firstMatch.StartAlgorithmLoop();
        secondMatch.StartAlgorithmLoop();
        try {
            Scheduler.WriteRealizationsToFile();
            Scheduler.WriteScheduleToFile();
        }
        catch(Exception e) {
            e.printStackTrace();
        }


        //for (Room r: Scheduler.rooms) {
        //    r.PrintRoom();
        //}

        //for (Realization r: Scheduler.realizations) {
        //    r.PrintRealization();
        //}

    }
}
