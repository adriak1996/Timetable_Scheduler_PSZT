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

    public static void GenerateInputData()
    {


        for(int i = 0; i < Scheduler.participantsAmount; i++)
        {
            Participant.GetRandom();
        }

        for(int i = 0; i < Scheduler.participantsAmount; i++)
        {
            participants.get(i).GetPreferences();
        }

        CourseType.GetCourseTypes();

        for(int i = 0; i < Scheduler.realizationsAmount; i++)
        {
            Realization.GetRandom();
        }

        for(int i = 0; i < Scheduler.roomsAmount; i++)
        {
            Room.GetRandom();
        }

        for (Participant p: Scheduler.participants) {
            p.printParticipant();
        }

        for (Realization r: Scheduler.realizations) {
            r.PrintRealization();
        }

        for (Room r: Scheduler.rooms) {
            r.PrintRoom();
        }


    }


}
