package com.company;

import java.util.Random;

public class Realization {

    public int realizationId;
    public int courseTypeId;
    public int courseLength;                 // in hours
    public int maxParticipantAmount;
    public int enrolledParticipantAmount;
    public int costForHiringLecturer;
    public int courseCost;                   // for all semester
    public int roomId;                       // room where course will take place


    public  static void GetRandom()
    {
        Realization temp = new Realization();
        temp.realizationId = Scheduler.realizations.size();
        Random rand = new Random();

        int  n = rand.nextInt(Scheduler.courseTypesAmount);
        temp.courseTypeId = n;

        temp.courseLength = Scheduler.courseTypes.get(n).courseLength;
        temp.maxParticipantAmount = Scheduler.courseTypes.get(n).maxParticipantAmount;
        temp.enrolledParticipantAmount = 0;
        temp.costForHiringLecturer = Scheduler.courseTypes.get(n).costForHiringLecturer;
        temp.courseCost = Scheduler.courseTypes.get(n).courseCost;
        temp.roomId = -1;      // unassigned at the beginning

        Scheduler.realizations.add(temp);
    }

    public void PrintRealization()
    {
        System.out.println("Realization: " + this.realizationId);
        System.out.println("             " + this.courseTypeId);
        System.out.println("             " + this.courseLength);
        System.out.println("             " + this.maxParticipantAmount);
        System.out.println("             " + this.enrolledParticipantAmount);
        System.out.println("             " + this.costForHiringLecturer);
        System.out.println("             " + this.courseCost);
        System.out.println("             " + this.roomId);
        System.out.println();

    }


}
