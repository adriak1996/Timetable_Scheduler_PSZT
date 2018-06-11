package com.company;

import java.io.Writer;
import java.util.ArrayList;
import java.io.*;

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
    static public double finalProfit;

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
    }


    public static double CalculateFinalProfit()
    {
        finalProfit = 0;
        for (Realization r: Scheduler.realizations)
        {
            if(r.roomId != -1) {
                finalProfit += r.getFitnessFunction();
            }
        }
        return finalProfit;
    }



    public static void WriteRealizationsToFile() throws IOException {
        String fileName = "Realizations.txt";
        int realizationsAssigned=0;
        String output = "";

        for(Realization r: Scheduler.realizations)
        {
            output = output + r.PrintRealizationIO() + "\n";
        }

        for(Realization r: Scheduler.realizations) {
            if(r.roomId != -1) realizationsAssigned++;
        }
        output = output + "\n\n\n" + "Realizations assigned: " + realizationsAssigned + "/" + realizationsAmount;
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "utf-8"))) {
            writer.write(output);
        }
    }

    public static void WriteScheduleToFile() throws IOException {
        String fileName = "Room_Schedule.txt";
        String output = "";

        for(Room r: Scheduler.rooms)
        {
            output = output + r.PrintRoomIO() + "\n";
            output += "\n-----------------------------------------------------\n";
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "utf-8"))) {
            writer.write(output);
        }
    }

    public static void WriteFinalProfitToFile(String output) throws IOException {
        String fileName = "Final_Profit.txt";

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "utf-8"))) {
            writer.write(output);
        }
    }

    public static void WriteParticipantsToFile() throws IOException {
        String fileName = "Participants_Courses.txt";
        String output = "";

        for(Participant p: Scheduler.participants)
        {
            output = output + p.PrintParticipantIO() + "\n";
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "utf-8"))) {
            writer.write(output);
        }
    }
}
