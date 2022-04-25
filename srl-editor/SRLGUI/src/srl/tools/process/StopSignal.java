/*
 * Copyright (c) 2008, National Institute of Informatics
 *
 * This file is part of SRL, and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://www.fsf.org/licensing/licenses/info/GPLv2.html.
 */
package srl.tools.process;

/**
 * A signal a long running process can take 
 * 
 * @author John McCrae, National Institute of Informatics
 */
public class StopSignal {
    private boolean stop = false;
    private boolean stopCompleted = false;
    
    /**
     * The process should frequently query this function to see if the stop has been
     * completed
     * @return True if it is stopped
     */
    public boolean isStopped() { return stop; }
    
    /**
     * Call this to stop the thread
     */
    public void stop() { stop = true; }
    
    /**
     * Cancel a stop
     * @throws IllegalStateException If the process is already stopped
     */
    public void cancelStop() throws IllegalStateException { 
        if(stopCompleted)
            throw new IllegalStateException();
        stop = false; 
    }
    
     /*
     * This method should be called at the end of the processes run() function 
     */
    public void confirmStop() { 
        stopCompleted = true;
        synchronized(this) {
            notify();
        }
    }
    
    /**
     * Check if the process has stopped
     * @return True if the process has completed
     */
    public boolean isStopConfirmed() { return stopCompleted; }
    
    /**
     * Causes the current thread to wait until the orocess is completed
     * @throws java.lang.InterruptedException
     */
    public void waitOnSignal() throws InterruptedException { 
        wait();
    }
}
