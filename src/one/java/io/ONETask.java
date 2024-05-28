/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java.io;

/**
 * This class represents a progress object that can be used to monitor the progress of certain
 * ONE read and write tasks
 * @author wsteffen
 */
public class ONETask
{
    //Total number of steps in the process
    private long numSteps = 0;
    //The current step in the process
    private long currentStep = 0;
    //The name of the process being monitored
    private String processName = "";

    public ONETask(String taskName, long numSteps)
    {
        this.processName = taskName;
        this.numSteps = numSteps;
    }
    /**
     * Returns the percentage of the progress done
     * @return 
     */
    public double getPercentDone()
    {
        return(currentStep/numSteps);
    }
    /**
     * @return the numSteps
     */
    public long getNumSteps()
    {
        return numSteps;
    }

    /**
     * @return the currentStep
     */
    public long getCurrentStep()
    {
        return currentStep;
    }

    /**
     * @param currentStep the currentStep to set
     */
    protected void setCurrentStep(long currentStep)
    {
        this.currentStep = currentStep;
    }
    
    protected void addCurentSteps(long toAdd)
    {
        this.currentStep += toAdd;
    }

    /**
     * @return the processName
     */
    public String getTaskName()
    {
        return processName;
    }
}
