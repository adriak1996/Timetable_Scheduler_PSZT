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
    {

        successfulDescendantRatioThread = 1;
        int iterationCounter=0;
        while(successfulDescendantRatioThread > 0.01)
        {
            mThread = 100;
            successAmountThread = 0;
            successfulDescendantRatioThread = 0;
            int counter = 0;
            for(int i = 0; i<mThread; i++)
            {
                DescendantSecond temp = EvolutionaryAlgorithm();
                if (temp != null) {
                    if(temp.CalculateFitnessFunction()> 0) {
                        ++successAmountThread;
                        AssignDescendant(temp);
                        //System.out.println("~~Assigned r #"+temp.realizationId + " to a room #" + temp.roomId);
                        //System.out.println("It's fitness function is: " + (Scheduler.realizations.get(temp.realizationId)).RecalculateFitnessFunction() );
                    }
                }
               else {
                    System.out.println(++counter);
                    SuccessfulSwap(swappingDescendant); //validation failed, trying to put 'temp' instead of something worse
                }
            }
            successfulDescendantRatioThread = (double) successAmountThread /(double) mThread;
            System.out.println("successfulDescendantRatio: " + successfulDescendantRatioThread);
            Scheduler.CalculateFinalProfit();
            if (iterationCounter == 100) break;
            iterationCounter++;

        }

    }

    public DescendantSecond EvolutionaryAlgorithm() {

        successAmount = 0;
        mIteration = 2000;
        sigmaRealization = 1; //Scheduler.realizationsAmount/8;
        sigmaRoom = 1; //Scheduler.roomsAmount/8;
        successRatio = 0;

        Random rand = new Random();
        for(int i = 0; i < mIteration; i++) {
        current = new DescendantSecond(rand.nextInt(Scheduler.realizationsAmount),
                rand.nextInt(Scheduler.roomsAmount) );
        //System.out.println("Moj roomID: " + current.roomId);
        current.CalculateFitnessFunction();
            if (ValidateDescendant(current)) { break; } //System.out.println("VALIDATE")
            else { swappingDescendant = current; current = null;}
        }

        if(current == null) return null;

        while(sigmaRealization > 0.01)
        {
            successRatio = 0;
            successAmount = 0;

            for(int i = 0; i<mIteration; i++)
            {

                //int newRealizationId = current.realizationId +
                //        (int)(sigmaRealization * (rand.nextDouble()*Scheduler.realizationsIndexJump));
                //if(newRealizationId > (Scheduler.realizationsAmount-1)) newRealizationId %= Scheduler.realizationsAmount;

                int newRoomId = current.roomId +
                        (int)(sigmaRoom * (rand.nextDouble()*Scheduler.roomsIndexJump));
                if(newRoomId > (Scheduler.roomsAmount-1)) newRoomId %= Scheduler.roomsAmount;
                //System.out.println("Moj new roomID: " + current.roomId);
                next = new DescendantSecond(current.realizationId,newRoomId);
                //next = new DescendantSecond(newRealizationId,newRoomId);
                next.CalculateFitnessFunction();

                if((next.fitnessFuntionValue > current.fitnessFuntionValue) && ValidateDescendant(next))
                {
                    //System.out.println("nextFitness: " + next.fitnessFuntionValue + ": > currentValue: " + current.fitnessFuntionValue);
                    //System.out.println("Fitness przed: " + current.fitnessFuntionValue);
                    current = next;
                    //System.out.println("Fitness po: " + current.fitnessFuntionValue);
                    ++successAmount;
                    //current.PrintDescendantFirst();
                }
            }

            successRatio = (double) successAmount/(double) mIteration;
            //System.out.println("przed: " + current.realizationId);
            //System.out.println("successRatio: " + successRatio);
            SetSigmas();
        }

        return current;
    }

    private boolean SuccessfulSwap(DescendantSecond descendant) {
        ArrayList<Realization> tempRealizationList = new ArrayList<>();
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);
        int courseDay = -1;
        int courseHour = -1;

        for (Realization r: Scheduler.realizations){
            if ((thisRealization.fitnessFunction > r.fitnessFunction)&&
                    (thisRealization.enrolledParticipantAmount <= r.enrolledParticipantAmount)){ //could be seats in a room for r
                tempRealizationList.add(r);
            }
        }

        tempRealizationList.sort(Comparator.comparing(Realization::getFitnessFunction));//smallest first

        //if(tempRealizationList.size() == 0) return false;

        //trying to swap places in schedule
        for (Realization r: tempRealizationList){
            if (r.roomId != -1) {
                Room thisRoom = Scheduler.rooms.get(r.roomId);

                //looking for a day in which r realization is in our current schedule
                for (int i = 0; i < thisRoom.getHoursScheduleRows() - 1; i++) {
                    if(courseDay != -1) break;
                    for (int j = 0; j < thisRoom.getHoursScheduleCols() - 1; j++) {
                        if (thisRoom.getHoursSchedule()[i][j] == r.realizationId) {
                            courseDay = i;
                            courseHour = j;
                            break;
                        }
                    }
                }
                if(courseDay == -1) return false; //could not find a right fit
                //System.out.println("Rows: " + thisRoom.getHoursScheduleRows() + " Cols: " + thisRoom.getHoursScheduleCols());
                //System.out.println("courseDay: " + courseDay +" courseHour: " + courseHour);

                //checking if there are free hours needed for assignment
                if(thisRealization.courseLength <= r.courseLength + thisRoom.getFreeHoursInEachDay()[courseDay]) {
                    System.out.println("Final profit before " + Scheduler.CalculateFinalProfit());
                    //assign new realization to this room
                    thisRealization.roomId = r.roomId;
                    //clearing old realization
                    r.roomId = -1;
                    //reorganizing courses
                    int counter = 0;
                    for(int j = courseHour+r.courseLength; j < thisRoom.getMaxHoursADay()-1; j++) {
                        thisRoom.getHoursSchedule()[courseDay][courseHour+(counter)] = thisRoom.getHoursSchedule()[courseDay][j];
                        counter++;
                    }

                    thisRoom.occupiedHoursNumber += (-r.courseLength + thisRealization.courseLength);
                    thisRoom.CountOccupiedHoursNumber();
                    System.out.println("Swapped: " + r.fitnessFunction + " for "+  thisRealization.fitnessFunction);
                    System.out.println("Final profit after " + Scheduler.CalculateFinalProfit());
                    return true;

                }
            }//if
        }//for
        return false;
    }

    public boolean ValidateDescendant(DescendantSecond descendant) {
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);
        Room thisRoom = Scheduler.rooms.get(descendant.roomId);

        thisRoom.CountOccupiedHoursNumber();

        if(thisRealization.roomId != -1) return false;
        // this realization is already assigned to a room

        if(thisRealization.enrolledParticipantAmount > thisRoom.seatsNumber) return false;
        // not enough seats for participants

        boolean fitFlag = false;
        /*for(int daysHours: thisRoom.freeHoursInEachDay)
        {
            if(daysHours >= thisRealization.courseLength)
            {
                fitFlag = true;
                break;
            }

        }
        */
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
            {

                for (int j = 0; j < (thisRoom.maxHoursADay - thisRealization.courseLength); j++)
                {
                    if(thisRoom.hoursSchedule[k][j]==-1) {
                        for (int l = 0; l < thisRealization.courseLength; l++) {
                            thisRoom.hoursSchedule[k][j + l] = thisRealization.realizationId;
                        }
                        assignmentSuccessfulFlag = true;
                        break;
                    }
                    if(assignmentSuccessfulFlag) break;
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
