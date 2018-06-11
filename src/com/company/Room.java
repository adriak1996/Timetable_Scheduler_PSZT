package com.company;

import java.util.ArrayList;
import java.util.Random;

public class Room {
    public static int maxHoursADay = 10;

    public int roomId;
    public int seatsNumber;
    public int leaseCost;  // for 1 hour
    public int occupiedHoursNumber;    // number of hours when there are courses in the room
    public static int dayQuantity = 3;
    public int[][] hoursSchedule = new int[dayQuantity][maxHoursADay];    // if -1, room is free in that hour
    public int[] freeHoursInEachDay;

    static public int[] possibleSeatsNumbersInRooms = new int[]  {30,35,40,55,60};
    static public int[] possibleLeasingCost = new int[]  {150,200,250,300,350};


    public Room()
    {
        occupiedHoursNumber=0;
        freeHoursInEachDay = new int[]{maxHoursADay, maxHoursADay, maxHoursADay};
        for (int i = 0; i < dayQuantity; i++) {
            for (int j = 0; j < maxHoursADay; j++) {
                hoursSchedule[i][j] = -1;
            }
        }
    }

    void CountOccupiedHoursNumber()
    {
        occupiedHoursNumber=0;
        freeHoursInEachDay = new int[]{maxHoursADay, maxHoursADay, maxHoursADay};
        int k = 0;

        for (int i=0; i < dayQuantity ; i++)
        {
            for (int j=0;j < maxHoursADay; j++ )
            {
                if(hoursSchedule[i][j] != -1) {
                    occupiedHoursNumber+=1;
                    freeHoursInEachDay[k]-=1;
                }
            }
            k += 1;
        }
    }

    public static void GenerateRoom()
    {
        Room temp = new Room();
        temp.roomId = Scheduler.rooms.size();
        Random rand = new Random();

        temp.occupiedHoursNumber = 0;
        temp.freeHoursInEachDay = new int[]{10,10,10};

        int n = rand.nextInt(5);
        temp.seatsNumber = possibleSeatsNumbersInRooms[n];
        int m = rand.nextInt(5);
        temp.leaseCost = possibleLeasingCost[m];

        Scheduler.rooms.add(temp);
    }

    public String PrintRoomIO()
    {   String output = ""
            + "Room #" + roomId
            + "\n\tSeats: " + seatsNumber
            + "\n\tLease cost: " + leaseCost
            + "\n\tOccupied hours: " + occupiedHoursNumber + "\n"
            + "Hours schedule:";

        for (int i=0; i < dayQuantity; i++)
        {   output += "\nDay " + (i+1);
            for (int j=0;j < maxHoursADay; j++ )
            {   output += "\n\t" + (j+8) + ":15 \t->\t#" + hoursSchedule[i][j];
                if(hoursSchedule[i][j] != -1){
                    output += "\t\tppl " + (Scheduler.realizations.get(hoursSchedule[i][j])).enrolledParticipantAmount
                            + "/" + this.seatsNumber;
                }
            }
        }
        return output;
    }


    public int getMaxHoursADay() {return maxHoursADay; }

    public int getHoursScheduleRows() {
        return hoursSchedule.length;
    }

    public int getHoursScheduleCols() {
        return hoursSchedule[0].length;
    }

    public int[][] getHoursSchedule() {
        return hoursSchedule;    }

    public int[] getFreeHoursInEachDay() { return freeHoursInEachDay; }
}
