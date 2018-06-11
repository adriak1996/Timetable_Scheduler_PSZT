package com.company;

import java.util.Random;

public class FirstMatch {

    private DescendantFirst current;
    private DescendantFirst next;
    private double sigmaParticipant;
    private double sigmaRealization;

    private int successAmount;           // amount of finding better descendant in last M matches
    private int mIteration;              // counted number of matches, MAX to 10 and reset
    private double successRatio;         // successAmount / m (max value of M)

    private int successAmountThread;            // amount of finding any acceptable descendant in each calling algorithm
    private int mThread;                        // counted number of calling algorithm
    private double successfulDescendantRatioThread;    // successAmountThread / mThread

    private final double C1 = 0.82;
    private final double C2 = 1.2;


    public void StartAlgorithmLoop()
    {
        successfulDescendantRatioThread = 1;

        while(successfulDescendantRatioThread > 0.01)
        {
            mThread = 500;
            successAmountThread = 0;
            successfulDescendantRatioThread = 0;

            for(int i = 0; i<mThread; i++)
            {
                DescendantFirst temp = EvolutionaryAlgorithm();
                if (temp != null)
                {
                    ++successAmountThread;
                    AssignDescendant(temp);
                }
            }
            successfulDescendantRatioThread = (double) successAmountThread / (double) mThread;
        }
    }

    public DescendantFirst EvolutionaryAlgorithm()
    {
        successAmount = 0;
        mIteration = 200;
        sigmaParticipant = 3;
        sigmaRealization = 3;
        successRatio = 0.0;

        Random rand = new Random();


        for(int i = 0; i < mIteration; i++) {
            current = new DescendantFirst(rand.nextInt(Scheduler.participantsAmount),
                    rand.nextInt(Scheduler.realizationsAmount));
            current.CalculateFitnessFunction();
            if (ValidateDescendant(current)) break;
            else current = null;
        }

        if(current == null) return null;

        while(sigmaRealization > 0.2)
        {
            successRatio = 0;
            successAmount = 0;

            for(int i = 0; i<mIteration; i++)
            {
                //--generating descendant from current
                int newParticipantId = current.participantId +
                        (int)(sigmaParticipant * (rand.nextDouble()*Scheduler.participantsIndexJump));
                if(newParticipantId > (Scheduler.participantsAmount-1)) newParticipantId %= Scheduler.participantsAmount;


                int newRealizationId = current.realizationId +
                        (int)(sigmaRealization * (rand.nextDouble()*Scheduler.realizationsIndexJump));
                if(newRealizationId > (Scheduler.realizationsAmount-1)) newRealizationId %= Scheduler.realizationsAmount;

                next = new DescendantFirst(newParticipantId,newRealizationId);
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


    public boolean ValidateDescendant(DescendantFirst descendant)
    {
        Participant thisParticipant = Scheduler.participants.get(descendant.participantId);
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);

        if(!(thisParticipant.declarations.containsKey(thisRealization.courseTypeId)))//Course has already  been chosen
            return false;


        if(thisParticipant.declarations.get(thisRealization.courseTypeId) != -1)
            return false;

        if(((thisRealization.maxParticipantAmount - thisRealization.enrolledParticipantAmount)<=0))
            return false;
        // no available seats for realization

        return true;

    }

    public void AssignDescendant(DescendantFirst descendant)
    {
        Participant thisParticipant = Scheduler.participants.get(descendant.participantId);
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);
        thisRealization.enrolledParticipantAmount+=1;
        thisParticipant.declarations.put(thisRealization.courseTypeId, thisRealization.realizationId);
    }


    public void SetSigmas()
    {
        if(successRatio >0.2)
        {
            sigmaRealization = C2 * sigmaRealization;
            sigmaParticipant = C2 * sigmaRealization;
        }
        else if(successRatio <0.2)
        {
            sigmaRealization = C1 * sigmaRealization;
            sigmaParticipant = C1 * sigmaRealization;
        }

    }



}
