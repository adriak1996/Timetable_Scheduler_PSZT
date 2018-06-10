package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class SecondMatch {

    private DescendantSecond current;
    private DescendantSecond next;
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
    {

        successfulDescendantRatioThread = 1;
        int iterationCounter=0;
        while(successfulDescendantRatioThread > 0.01)
        {
            mThread = 100;
            successAmountThread = 0;
            successfulDescendantRatioThread = 0;

            for(int i = 0; i<mThread; i++)
            {
                DescendantSecond temp = EvolutionaryAlgorithm();
                if (temp != null) {
                    ++successAmountThread;
                    AssignDescendant(temp);
                }
            }
            successfulDescendantRatioThread = (double) successAmountThread /(double) mThread;
            Scheduler.CalculateFinalProfit();
            if (iterationCounter == 100) break;
            iterationCounter++;

        }
        //for (Room r: Scheduler.rooms) {
       //     r.PrintRoom();
        //}
    }

    public DescendantSecond EvolutionaryAlgorithm() {

        successAmount = 0;
        mIteration = 10;
        sigmaRealization = 3; //Scheduler.realizationsAmount/8;
        sigmaRoom = 3; //Scheduler.roomsAmount/8;
        successRatio = 0;

        Random rand = new Random();
        for(int i = 0; i < mIteration; i++) {
        current = new DescendantSecond(rand.nextInt(Scheduler.realizationsAmount),
                rand.nextInt(Scheduler.roomsAmount) );
        current.CalculateFitnessFunction();
            if (ValidateDescendant(current)) break;
            //else if (SuccessfulSwap(current)) break; //ToDo
            else current = null;
        }


        if(current == null) return null;

        while(sigmaRealization > 0.2)
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
                    //System.out.println("nextFitness: " + next.fitnessFuntionValue + ": > currentValue: " + current.fitnessFuntionValue);
                    current = next;
                    ++successAmount;
                    //current.PrintDescendantFirst();
                }

            }

            successRatio = successAmount/mIteration;
            SetSigmas();
        }

        return current;
    }

    private boolean SuccessfulSwap(DescendantSecond descendant) {
        ArrayList<Realization> tempRealizationList = new ArrayList<>();
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);

        for (Realization r: Scheduler.realizations){
            if ((thisRealization.fitnessFunction > r.fitnessFunction)&&
                    (thisRealization.enrolledParticipantAmount <= r.enrolledParticipantAmount)){
                tempRealizationList.add(r);
            }
        }

        if (tempRealizationList.isEmpty()){ return false;} //could not find a better realization
        tempRealizationList.sort(Comparator.comparing(Realization::getFitnessFunction));//smallest first

        //trying to swap places
        for (Realization r: tempRealizationList){
            if (r.roomId != -1){
                Room thisRoom = Scheduler.rooms.get(r.roomId);

                for (int i=0;i<thisRoom.getHoursScheduleRows()-1;i++){
                    for (int j=0;j<thisRoom.getHoursScheduleCols()-1;j++){
                        if (thisRoom.getHoursSchedule()[i][j]==r.realizationId){
                            break;
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean ValidateDescendant(DescendantSecond descendant) {
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);
        Room thisRoom = Scheduler.rooms.get(descendant.roomId);

        thisRoom.CountOccupiedHoursNumber();

        if(thisRealization.enrolledParticipantAmount > thisRoom.seatsNumber) return false;
        // not enough seats for participants

        boolean fitFlag = false;
        for(int daysHours: thisRoom.freeHoursInEachDay)
        {
            if(daysHours >= thisRealization.courseLength)
            {
                fitFlag = true;
                break;
            }

        }
        if(!fitFlag) return false;
        // not enough free hours for running realization in the room

        if(thisRealization.roomId != -1) return false;
        // this realization is already assigned to a room


        return true;
    }


        public void AssignDescendant(DescendantSecond descendant)
    {
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);
        Room thisRoom = Scheduler.rooms.get(descendant.roomId);

        thisRealization.roomId = thisRoom.roomId;
        thisRoom.occupiedHoursNumber += thisRealization.courseLength;
        thisRoom.CountOccupiedHoursNumber();

        int k = 0;
        boolean flag = false;
        for(int daysHours: thisRoom.freeHoursInEachDay)
        {

            if(daysHours >= thisRealization.courseLength)
            {

                for (int j = 0; j < (thisRoom.maxHoursADay - thisRealization.courseLength); j++)
                {
                    if(thisRoom.hoursSchedule[k][j]==0) {
                        for (int l = 0; l < thisRealization.courseLength; l++) {
                            //thisRoom.hoursSchedule[k][j + l] = thisRealization.realizationId;
                            thisRoom.hoursSchedule[k][j] = thisRealization.realizationId;
                        }
                        flag = true;
                        break;
                    }
                    if(flag) break;

                }

                break;
            }
            ++k;
        }
        thisRoom.CountOccupiedHoursNumber();
        //System.out.println("Realization #" + descendant.realizationId + " has been enrolled for room #" + descendant.roomId);
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
