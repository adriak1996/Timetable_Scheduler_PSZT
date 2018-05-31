package com.company;

import java.util.Random;

public class FirstMatch {

    private DescendantFirst current;
    private DescendantFirst next;
    private double sigmaParticipant;
    private double sigmaRealization;

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
                DescendantFirst temp = EvolutionaryAlgorithm();
                if (temp != null)
                {
                    ++successAmountThread;
                    AssignDescendant(temp);
                }
            }
            successProbabilityThread = successAmountThread / mThread;
        }
        for (Participant p: Scheduler.participants) {
            p.printParticipant();
        }

        for (Realization r: Scheduler.realizations) {
            r.PrintRealization();
        }



    };
    public DescendantFirst EvolutionaryAlgorithm()
    {

        successAmount = 0;
        m = 10;
        sigmaParticipant = 1; //Scheduler.participantsAmount/8;
        sigmaRealization = 1; //Scheduler.realizationsAmount/8;
        successProbability = 0;

        Random rand = new Random();


        for(int i = 0; i < m; i++) {
            current = new DescendantFirst(rand.nextInt(Scheduler.participantsAmount),
                    rand.nextInt(Scheduler.realizationsAmount));
            current.CalculateFitnessFunction();
            if (ValidateDescendant(current)) break;
            else current = null;
        }

        if(current == null) return null;

        while(sigmaRealization > 0.2)
        {
            m = 0;
            successProbability = 0;
            successAmount = 0;

            for(int i = 0; i<10; i++)
            {

                int newParticipantId = current.participantId +
                        (int)(sigmaParticipant * (rand.nextDouble()*Scheduler.participantsIndexJump));
                if(newParticipantId < 0) newParticipantId+= Scheduler.participantsAmount;
                if(newParticipantId > (Scheduler.participantsAmount-1)) newParticipantId-= Scheduler.participantsAmount;


                int newRealizationId = current.realizationId +
                        (int)(sigmaRealization * (rand.nextDouble()*Scheduler.realizationsIndexJump));
                if(newRealizationId < 0) newRealizationId+= Scheduler.realizationsAmount;
                if(newRealizationId > (Scheduler.realizationsAmount-1)) newRealizationId-= Scheduler.realizationsAmount;

                next = new DescendantFirst(newParticipantId,newRealizationId);
                next.CalculateFitnessFunction();

                if((next.fitnessFuntionValue > current.fitnessFuntionValue) && ValidateDescendant(next))
                {
                    current = next;
                    ++successAmount;
                    current.PrintDescendantFirst();
                }

                ++m;
            }

            successProbability = successAmount/10;
            SetSigmas();
        }




        return current;
    };




    public boolean ValidateDescendant(DescendantFirst descendant)
    {
        Participant thisParticipant = Scheduler.participants.get(descendant.participantId);
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);

        if(!(thisParticipant.declarations.containsKey(thisRealization.courseTypeId)))
            return false;


        if(thisParticipant.declarations.get(thisRealization.courseTypeId) != -1)
            return false;

        if(((thisRealization.maxParticipantAmount - thisRealization.enrolledParticipantAmount)<=0))
            return false;
        // no available seats for realization

        return true;

    };


    public void AssignDescendant(DescendantFirst descendant)
    {
        Participant thisParticipant = Scheduler.participants.get(descendant.participantId);
        Realization thisRealization = Scheduler.realizations.get(descendant.realizationId);
        thisRealization.enrolledParticipantAmount+=1;
        thisParticipant.declarations.put(thisRealization.courseTypeId, thisRealization.realizationId);

        //System.out.println("Participant #" + thisParticipant.participantId + " has been enrolled for realization #"
        //+ thisRealization.realizationId);
        System.out.println(Scheduler.realizations.get(0).enrolledParticipantAmount);


    };


    public void SetSigmas()
    {
        if(successProbability>0.2)
        {
            sigmaRealization = C2 * sigmaRealization;
            sigmaParticipant = C2 * sigmaRealization;
        }
        else if(successProbability<0.2)
        {
            sigmaRealization = C1 * sigmaRealization;
            sigmaParticipant = C1 * sigmaRealization;
        }

    }



}
