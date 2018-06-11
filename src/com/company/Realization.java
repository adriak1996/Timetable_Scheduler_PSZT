package com.company;

import java.util.Random;
import java.text.*;
public class Realization {

    public int realizationId;
    public int courseTypeId;
    public int courseLength;                 // in hours
    public int maxParticipantAmount;
    public int enrolledParticipantAmount;
    public int costForHiringLecturer;
    public int courseCost;                   // for all semester
    public int roomId;                       // room where course will take place
    public double fitnessFunction;           //value of fitness function for second match

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

    public double RecalculateFitnessFunction()
    {
        if(this.roomId == -1) {fitnessFunction = -10000; return fitnessFunction;}
        Room thisRoom = Scheduler.rooms.get(this.roomId);

        double fitnessFuntionValue;

        int profit = this.enrolledParticipantAmount * this.courseCost;
        int loss = this.costForHiringLecturer + thisRoom.leaseCost * this.courseLength;

        fitnessFuntionValue = profit - loss;
        this.fitnessFunction = fitnessFuntionValue;

        return this.fitnessFunction;
    }


    public String PrintRealizationIO()
    {   DecimalFormat df = new DecimalFormat("#.##");
        String output = ""
                + "Realization:      " + this.realizationId
                + "\n\tFitness:      " + df.format(this.RecalculateFitnessFunction())
                + "\n\tcourseType    " + this.courseTypeId
                + "\n\tcourseLength  " + this.courseLength;
        if(this.roomId != -1) output += "\n\tLease cost    " + (Scheduler.rooms.get(this.roomId)).leaseCost;
        else output += "\n\tLease cost    " + "----";

        output = output
                + "\n\tmaxPartAmount " + this.maxParticipantAmount
                + "\n\tenrolled      " + this.enrolledParticipantAmount
                + "\n\thiringCost    " + this.costForHiringLecturer
                + "\n\tcourseCost    " + this.courseCost
                + "\n\troomId        " + this.roomId + "\n\n";
        return output;
    }


    public double getFitnessFunction() {
        return fitnessFunction;
    }

}
