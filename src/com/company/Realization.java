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

    public double fitnessFunction;

    public static void GenerateRealization()
    {
        Realization temp = new Realization();
        temp.realizationId = Scheduler.realizations.size();
        Random rand = new Random();

        int  whichCourse = rand.nextInt(Scheduler.courseTypesAmount);
        temp.courseTypeId = whichCourse;

        temp.courseLength = Scheduler.courseTypes.get(whichCourse).courseLength;
        temp.maxParticipantAmount = Scheduler.courseTypes.get(whichCourse).maxParticipantAmount;
        temp.enrolledParticipantAmount = 0;
        temp.costForHiringLecturer = Scheduler.courseTypes.get(whichCourse).costForHiringLecturer;
        temp.courseCost = Scheduler.courseTypes.get(whichCourse).courseCost;
        temp.roomId = -1;      // unassigned at the beginning

        Scheduler.realizations.add(temp);
    }

    public void PrintRealization()
    {
        System.out.println("Realization:     " + this.realizationId);
        System.out.println("   courseType    " + this.courseTypeId);
        System.out.println("   courseLength  " + this.courseLength);
        System.out.println("   maxPartAmount " + this.maxParticipantAmount);
        System.out.println("   enrolled      " + this.enrolledParticipantAmount);
        System.out.println("   hiringCost    " + this.costForHiringLecturer);
        System.out.println("   courseCost    " + this.courseCost);
        System.out.println("   roomId        " + this.roomId);
        System.out.println();

    }

    public double getFitnessFunction() {
        return fitnessFunction;
    }

}
