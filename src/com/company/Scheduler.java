package com.company;

import java.util.ArrayList;

public class Scheduler {


    static public ArrayList<Participant> participants = new ArrayList<>();
    static public ArrayList<CourseType> courseTypes = new ArrayList<>();
    static public ArrayList<Realization> realizations = new ArrayList<>();
    static public ArrayList<Room> rooms = new ArrayList<>();

    static public int participantsAmount = 500;
    static public int courseTypesAmount = 6;
    static public int realizationsAmount = 40;
    static public int roomsAmount = 8;

    static public int participantsIndexJump = participantsAmount/8;
    static public int realizationsIndexJump = realizationsAmount/8;
    static public int roomsIndexJump = roomsAmount/4;
    static public double finalProfit = 0;

    public static void GenerateInputData()
    {
        for(int i = 0; i < Scheduler.participantsAmount; i++)
        {
            Participant.GenerateParticipants();
        }

        for(int i = 0; i < Scheduler.participantsAmount; i++)
        {
            participants.get(i).GeneratePreferences();
        }

        CourseType.GetCourseTypes();

        for(int i = 0; i < Scheduler.realizationsAmount; i++)
        {
            Realization.GenerateRealization();
        }

        for(int i = 0; i < Scheduler.roomsAmount; i++)
        {
            Room.GenerateRoom();
        }
//----------------drukowanie na ekran
        //for (Participant p: Scheduler.participants) {
        //    p.printParticipant();
        //}

        //for (Realization r: Scheduler.realizations) {
        //    r.PrintRealization();
        //}

        //for (Room r: Scheduler.rooms) {
        //    r.PrintRoom();
        //}


    }


    public static void CalculateFinalProfit()
    {
        finalProfit = 0;
        for (Realization r: Scheduler.realizations) {
            finalProfit += r.fitnessFunction;
        }
        System.out.println("Final: " + finalProfit);
    }
}
