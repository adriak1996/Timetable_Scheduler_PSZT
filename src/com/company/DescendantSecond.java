package com.company;

public class DescendantSecond implements IDescendantMethods{

    public int realizationId;
    public int roomId;
    public double fitnessFuntionValue;

    public DescendantSecond(int realizationId, int roomId)
    {
        this.realizationId = realizationId;
        this.roomId = roomId;
    }


    public double CalculateFitnessFunction()
    {
        //---- value = (profit - loss) / number of occupied hours
        //---- profit = participant number * realization cost
        //---- loss = hiring lecturer cost + cost for room leasing for 1 hour * realization length
        Realization thisRealization = Scheduler.realizations.get(realizationId);
        Room thisRoom = Scheduler.rooms.get(roomId);

        int profit = thisRealization.enrolledParticipantAmount * thisRealization.courseCost;
        int loss = thisRealization.costForHiringLecturer + thisRoom.leaseCost * thisRealization.courseLength;

        fitnessFuntionValue = profit - loss;

        thisRealization.fitnessFunction = fitnessFuntionValue;

        return  fitnessFuntionValue;
    }

}
