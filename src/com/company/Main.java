package com.company;

public class Main {

    public static void main(String[] args) {
        FirstMatch firstMatch = new FirstMatch();
        SecondMatch secondMatch = new SecondMatch();

        Scheduler.GenerateInputData();

        firstMatch.StartAlgorithmLoop();
        secondMatch.StartAlgorithmLoop();
        try {
            Scheduler.WriteRealizationsToFile();
            Scheduler.WriteScheduleToFile();
            Scheduler.WriteParticipantsToFile();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
