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
    public double fitnessFunction=-2;           //value of fitness function for second match

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

    public String PrintRealization()
    {   DecimalFormat df = new DecimalFormat("#.##");
        String output = ""
        + "Realization:      " + this.realizationId
        + "\n\tFitness:      " + df.format(this.fitnessFunction)
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

    public double RecalculateFitnessFunction() {
        Room thisRoom = Scheduler.rooms.get(this.roomId);
        int maxH = thisRoom.maxHoursADay;
        //System.out.println("FF: " + this.fitnessFunction + "\t\tenrolled: " + this.enrolledParticipantAmount );
        double fitnessFuntionValue = 0;

        int profit = this.enrolledParticipantAmount * this.courseCost;
        int loss = this.costForHiringLecturer + thisRoom.leaseCost * this.courseLength;

        fitnessFuntionValue = profit - loss;
        //if (thisRoom.occupiedHoursNumber == 0) fitnessFuntionValue = profit - loss;
        //else fitnessFuntionValue = (profit - loss)*(1 - thisRoom.occupiedHoursNumber / maxH);

        this.fitnessFunction = fitnessFuntionValue;
       // System.out.println(" Recalculated: " + this.fitnessFunction);
        return this.fitnessFunction;
    }

    public double getFitnessFunction() {
        return fitnessFunction;
    }

}
