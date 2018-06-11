package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class SecondMatch {

    private DescendantSecond current;
    private DescendantSecond next;
    private DescendantSecond swappingDescendant;
    private double sigmaRealization;
    private double sigmaRoom;

    private int successAmount;            // amount of finding better descendant in last M matches
    private int mIteration;                        // counted number of matches, MAX to 10 and reset
    private double successRatio;          // successAmount / m (max value of M)

    private int successAmountThread;                   // amount of finding any acceptable descendant in each calling algorithm
    private int mThread;                               // counted number of calling algorithm
    private double successfulDescendantRatioThread;    // successAmountThread / mThread

    private final double C1 = 0.82;
    private final double C2 = 1.2;


    public void StartAlgorithmLoop()
    {   String output = "";
        successfulDescendantRatioThread = 1;
        int iterationCounter=0;
        while(successfulDescendantRatioThread >= 0.1 || iterationCounter < 100)
        {
            mThread = 10;
            successAmountThread = 0;
            successfulDescendantRatioThread = 0;
            for(int i = 0; i<mThread; i++)
            {
                DescendantSecond temp = EvolutionaryAlgorithm();
                if (temp != null) {
                    if(temp.CalculateFitnessFunction()> 1000) {
                        ++successAmountThread;
                        AssignDescendant(temp);
                    }
                }
              // else {SuccessfulSwap(swappingDescendant); } //validation failed, trying to put 'temp' instead of something worse
            }
            successfulDescendantRatioThread = (double) successAmountThread /(double) mThread;
            iterationCounter++;
            output += "Loop " + iterationCounter + ":\tFinalProfit is " + Scheduler.CalculateFinalProfit() +"\n";
            System.out.println("Final profit: " + Scheduler.CalculateFinalProfit());
        }

        //writing final profits output to a file
        try {
            Scheduler.WriteFinalProfitToFile(output);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public DescendantSecond EvolutionaryAlgorithm() {

        successAmount = 0;
        mIteration = 200;
        sigmaRealization = 3;
        sigmaRoom = 3;
        successRatio = 0;

        Random rand = new Random();
        for(int i = 0; i < mIteration; i++) {
        current = new DescendantSecond(rand.nextInt(Scheduler.realizationsAmount),rand.nextInt(Scheduler.roomsAmount));

        current.CalculateFitnessFunction();
            if (ValidateDescendant(current)) { break; }
            else { swappingDescendant = current; current = null;}
        }

        if(current == null) return null;

        while(sigmaRealization > 0.01)
        {
            successRatio = 0;
            successAmount = 0;

            for(int i = 0; i<mIteration; i++)
            {
                int newRealizationId = current.realizationId +
                        (int)(sigmaRealization * (rand.nextDouble()*Scheduler.realizationsIndexJump));
                if(newRealizationId > (Scheduler.realizationsAmount-1)) newRealizationId %= Scheduler.realizationsAmount;

                int newRoomId = current.roomId +
                        (int)(sigmaRoom * (rand.nextDouble()*Scheduler.roomsIndexJump));
                if(newRoomId > (Scheduler.roomsAmount-1)) newRoomId %= Scheduler.roomsAmount;
                next = new DescendantSecond(newRealizationId,newRoomId);
                next.CalculateFitnessFunction();

                if((next.fitnessFuntionValue > current.fitnessFuntionValue) && ValidateDescendant(next))
                {
                    current = next;
                    ++successAmount;
                }
            }
            successRatio = (double) successAmount/(double) mIteration;

            SetSigmas();
        }

        return current;
    }

    private boolean SuccessfulSwap(DescendantSecond descendant)
    {
        ArrayList<Realization> tempRealizationList = new ArrayList<>();
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);
        int courseDay = -1;
        int courseHour = -1;

        for (Realization r: Scheduler.realizations){
            if((r.roomId != -1))
            {
                if ((thisRealization.getFitnessFunction() > r.getFitnessFunction())
                        && (thisRealization.enrolledParticipantAmount <= (Scheduler.rooms.get(r.roomId)).seatsNumber))
                {
                    tempRealizationList.add(r);
                }
            }
        }

        tempRealizationList.sort(Comparator.comparing(Realization::getFitnessFunction));//smallest first

        if(tempRealizationList.size() == 0) {System.out.println("~~~~size < 0"); return false;}
        System.out.println("Moja wartosc: " + thisRealization.getFitnessFunction());
        for(Realization r: tempRealizationList) {
            System.out.println("ArrayList ff: " + r.getFitnessFunction());
        }
        //trying to swap places in schedule
        for (Realization r: tempRealizationList) {
            //check only those with assignment to a room
                Room thisRoom = Scheduler.rooms.get(r.roomId);

                //looking for a day in which r realization is in our current schedule
                for (int i = 0; i < thisRoom.getHoursScheduleRows(); i++) {
                    if(courseDay != -1) break;
                    for (int j = 0; j < thisRoom.getHoursScheduleCols(); j++) {
                        if (thisRoom.getHoursSchedule()[i][j] == r.realizationId) {
                            courseDay = i;
                            courseHour = j;
                            break;
                        }
                    }
                }
                if(courseDay == -1) {System.out.println("~~~~courseDay = -1"); return false;}
                //checking if there are free hours needed for assignment
                if(thisRealization.courseLength <= (r.courseLength + thisRoom.getFreeHoursInEachDay()[courseDay])) {
                    //System.out.println("Final profit before " + Scheduler.CalculateFinalProfit());
                    //assign new realization to this room
                    thisRealization.roomId = r.roomId;
                    //clearing old realization
                    r.roomId = -1;
                    //reorganizing courses
                    int counter = 0;
                    for(int j = courseHour; j < thisRoom.getMaxHoursADay()-r.courseLength; j++) {
                        thisRoom.getHoursSchedule()[courseDay][courseHour+(counter)] = thisRoom.getHoursSchedule()[courseDay][j+r.courseLength];
                        counter++;
                    }
                    for(int j=thisRoom.getMaxHoursADay()-r.courseLength; j<thisRoom.getMaxHoursADay(); j++)
                    {
                        thisRoom.getHoursSchedule()[courseDay][j] = -1;
                    }

                    for (int j=0;j < thisRoom.getMaxHoursADay(); j++ )
                    {
                        if(thisRoom.getHoursSchedule()[courseDay][j] ==-1) {
                            for(int k = j; k < j+thisRealization.courseLength; k++) {
                                thisRoom.getHoursSchedule()[courseDay][j] = thisRealization.realizationId;
                            }
                        }

                    }

                    thisRoom.occupiedHoursNumber += (-r.courseLength + thisRealization.courseLength);
                    thisRoom.CountOccupiedHoursNumber();
                    //System.out.println("Swapped: " + r.getFitnessFunction() + " for "+  thisRealization.RecalculateFitnessFunction());
                    //System.out.println("Final profit after " + Scheduler.CalculateFinalProfit());
                    //System.out.println("SWAPPING successful");
                    r.RecalculateFitnessFunction();
                    return true;
                }
        }//for
        return false;
    }

    public boolean ValidateDescendant(DescendantSecond descendant)
    {
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);
        Room thisRoom = Scheduler.rooms.get(descendant.roomId);

        thisRoom.CountOccupiedHoursNumber();

        if(thisRealization.roomId != -1) return false;
        // this realization is already assigned to a room

        if(thisRealization.enrolledParticipantAmount > thisRoom.seatsNumber) return false;
        // not enough seats for participants

        boolean fitFlag = false;

        for(int i = 0; i < thisRoom.freeHoursInEachDay.length; i++)
        {
            if(thisRoom.freeHoursInEachDay[i] >= thisRealization.courseLength)
            {
                fitFlag = true;
                break;
            }

        }

        if(!fitFlag) return false;
        // not enough free hours for running realization in the room
        return true;
    }


    public void AssignDescendant(DescendantSecond descendant)
    {
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);
        Room thisRoom = Scheduler.rooms.get(descendant.roomId);

        thisRealization.roomId = thisRoom.roomId;

        int k = 0;
        boolean assignmentSuccessfulFlag = false;
        for(int daysHours: thisRoom.freeHoursInEachDay)
        {

            if(daysHours >= thisRealization.courseLength)
            //if(thisRoom.getFreeHoursInEachDay()[dayHours] >= thisRealization.courseLength)
            {

                for (int j = 0; j <= (thisRoom.maxHoursADay - thisRealization.courseLength); j++)
                {
                    if(thisRoom.hoursSchedule[k][j]==-1) {
                        for (int x = 0; x < thisRealization.courseLength; x++) {
                            thisRoom.hoursSchedule[k][j + x] = thisRealization.realizationId;
                        }
                        assignmentSuccessfulFlag = true;
                    }
                    if(assignmentSuccessfulFlag) break;
                }

                break;
            }
            ++k;
        }
        thisRoom.CountOccupiedHoursNumber();
        thisRealization.RecalculateFitnessFunction();
    }


    public void SetSigmas()
    {
        if(successRatio >0.2)
        {
            sigmaRealization = C2 * sigmaRealization;
            sigmaRoom = C2 * sigmaRoom;
        }
        else if(successRatio <0.2)
        {
            sigmaRealization = C1 * sigmaRealization;
            sigmaRoom = C1 * sigmaRoom;
        }

    }

}
