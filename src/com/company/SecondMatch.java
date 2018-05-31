package com.company;

import java.util.Random;

public class SecondMatch {

    private DescendantSecond current;
    private DescendantSecond next;
    private double sigmaRealization;
    private double sigmaRoom;

    private int successAmount;            // amount of finding better descendant in last M matches
    private int m;                        // counted number of matches, MAX to 10 and reset
    private double successProbability;    // successAmount / m (max value of M)

    private int successAmountThread;            // amount of finding any acceptable descendant in each calling algorithm
    private int mThread;                        // counted number of calling algorithm
    private double successProbabilityThread;    // successAmountThread / mThread

    private final double C1 = 0.82;
    private final double C2 = 1.2;

    public void StartAlgorithmLoop()
    {

        successProbabilityThread = 1;

        while(successProbabilityThread > 0.01)
        {
            mThread = 50000;
            successAmountThread = 0;
            successProbabilityThread = 0;

            for(int i = 0; i<mThread; i++)
            {
                DescendantSecond temp = EvolutionaryAlgorithm();
                if (temp != null)
                {
                    ++successAmountThread;
                    AssignDescendant(temp);
                }
            }
            successProbabilityThread = successAmountThread / mThread;
        }
        for (Room r: Scheduler.rooms) {
            r.PrintRoom();
        }

       // DescendantSecond temp = EvolutionaryAlgorithm();
       // if (temp != null)
        //    AssignDescendant(temp);
    }

    public DescendantSecond EvolutionaryAlgorithm() {

        successAmount = 0;
        m = 10;
        sigmaRealization = 1; //Scheduler.realizationsAmount/8;
        sigmaRoom = 1; //Scheduler.roomsAmount/8;
        successProbability = 0;

        Random rand = new Random();
        for(int i = 0; i < m; i++) {
        current = new DescendantSecond(rand.nextInt(Scheduler.realizationsAmount),
                rand.nextInt(Scheduler.roomsAmount) );
        current.CalculateFitnessFunction();
            if (ValidateDescendant(current)) break;
            else current = null;
        }

        if(current == null) return null;



        if(current == null) return null;

        while(sigmaRealization > 0.2)
        {
            //m = 0;
            successProbability = 0;
            successAmount = 0;

            for(int i = 0; i<10; i++)
            {

                int newRealizationId = current.realizationId +
                        (int)(sigmaRealization * (rand.nextDouble()*Scheduler.realizationsIndexJump));
                if(newRealizationId < 0) newRealizationId+= Scheduler.realizationsAmount;
                if(newRealizationId > (Scheduler.realizationsAmount-1)) newRealizationId-= Scheduler.realizationsAmount;


                int newRoomId = current.roomId +
                        (int)(sigmaRoom * (rand.nextDouble()*Scheduler.roomsIndexJump));
                if(newRoomId < 0) newRoomId+= Scheduler.roomsAmount;
                if(newRoomId > (Scheduler.roomsAmount-1)) newRoomId-= Scheduler.roomsAmount;

                next = new DescendantSecond(newRealizationId,newRoomId);
                next.CalculateFitnessFunction();

                if((next.fitnessFuntionValue > current.fitnessFuntionValue) && ValidateDescendant(next))
                {
                    current = next;
                    ++successAmount;
                    //current.PrintDescendantFirst();
                }

                ++m;
            }

            successProbability = successAmount/m;
            SetSigmas();
        }

        return current;
    }

    public boolean ValidateDescendant(DescendantSecond descendant) {
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);
        Room thisRoom = Scheduler.rooms.get(descendant.roomId);

        thisRoom.CountOccupiedHoursNumber();

        if(thisRealization.enrolledParticipantAmount > thisRoom.seatsNumber) return false;
        // not enough seats for participants

        boolean flag = true;
        for(int i: thisRoom.freeHoursInEachDay)
        {
            if(i>= thisRealization.courseLength)
            {
                flag = false;
                break;
            }

        }
        if(flag) return false;
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
        for(int i: thisRoom.freeHoursInEachDay)
        {

            if(i>=thisRealization.courseLength)
            {

                for (int j = 0; j < (10 - thisRealization.courseLength); j++)
                {
                    if(thisRoom.hoursSchedule[k][j]==0) {
                        for (int l = 0; l < thisRealization.courseLength; l++) {
                            thisRoom.hoursSchedule[k][j + l] = thisRealization.realizationId;
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
        System.out.println("Realization #" + descendant.realizationId + " has been enrolled for "
        + "room #" + descendant.roomId);
    }


    public void SetSigmas()
    {
        if(successProbability>0.2)
        {
            sigmaRealization = C2 * sigmaRealization;
            sigmaRoom = C2 * sigmaRoom;
        }
        else if(successProbability<0.2)
        {
            sigmaRealization = C1 * sigmaRealization;
            sigmaRoom = C1 * sigmaRoom;
        }

    }

}
