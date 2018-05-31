package com.company;

import java.util.ArrayList;
import java.util.Random;

public class Room {
    public int roomId;
    public int seatsNumber;
    public int leaseCost;  // for 1 hour
    public int occupiedHoursNumber;    // number of hours when there are courses in the room

    public int[][] hoursSchedule = new int[3][10];    // if 0, room is free in that hour
    public int[] freeHoursInEachDay = new int[3];

    static public int[] possibleSeatsNumbersInRooms = new int[]  {30,40,50,60,70};
    static public int[] possibleLeasingCost = new int[]  {150,200,250,300,350};


    void CountOccupiedHoursNumber()
    {
        occupiedHoursNumber=0;
        freeHoursInEachDay = new int[]{10, 10, 10};
        int k = 0;

        for (int[] i: hoursSchedule)
        {

            for (int j:i)
            {
                if(j!=0)
                {occupiedHoursNumber=+1;
                freeHoursInEachDay[k]-=1;}
            }
            k+=1;
        }

    };

    static public void GetRandom()
    {
        Room temp = new Room();
        temp.roomId = Scheduler.rooms.size();
        Random rand = new Random();

        temp.occupiedHoursNumber = 0;
        temp.freeHoursInEachDay = new int[]{0, 0, 0};
        for (int[] i: temp.hoursSchedule)
        {
            for (int j:i)
            {
                j = 0;
            }
        }

        int n = rand.nextInt(5);
        temp.seatsNumber = possibleSeatsNumbersInRooms[n];
        int m = rand.nextInt(5);
        temp.leaseCost = possibleLeasingCost[m];

        Scheduler.rooms.add(temp);
    }

    public void PrintRoom()
    {
        System.out.println("Room #" + roomId);
        System.out.println("      " + seatsNumber);
        System.out.println("      " + leaseCost);
        System.out.println("      " + occupiedHoursNumber);

        System.out.println("Free hours in each day:" );
        for (int i: freeHoursInEachDay)
        {
                System.out.println("      " + i);
        }


        System.out.println("Hours schedule:" );
        for (int[] i: hoursSchedule)
        {
            for (int j:i)
            {
                System.out.println("      " + j);
            }
        }
        System.out.println();


    }
}
