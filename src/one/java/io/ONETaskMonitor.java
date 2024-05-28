

package one.java.io;

import java.util.Stack;

/**
 *
 * @author Nico
 */
public class ONETaskMonitor 
{
    //Stack of tasks we are monitoring 
    private Stack<ONETask> progressStack;
    
    public ONETaskMonitor()
    {
        progressStack = new Stack<>();
    }
    
    /**
     * Adds a new task to this monitor
     * @param task 
     */
    public void add(ONETask task)
    {
        this.progressStack.push(task);
    }
    
    /**
     * Returns the top-most task (the one currently running)
     * @return 
     */
    public ONETask peek()
    {
        return(this.progressStack.peek());
    }
    
    /**
     * Removes the top-most task
     */ 
    public void remove()
    {
        this.progressStack.pop();
    }
}
