package systemwide;

import requests.SystemEvent;

/**
 * BoundedBuffer for managing Thread-Safe messaging between system components
 * 
 * @author Lynn Marshall, Julian, Ryan Dash
 */
public class BoundedBuffer {
    // A simple ring buffer is used to hold the data

    // buffer capacity
    private static final int SIZE = 5;
    private final SystemEvent[] buffer = new SystemEvent[SIZE];
    private int inIndex = 0, outIndex = 0, count = 0;

    // If true, there is room for at least one object in the buffer.
    private boolean writeable = true;

    // If true, there is at least one object stored in the buffer.    
    private boolean readable = false;

    /**
     * Returns the amount of items in the buffer.
     *
     * @return number the amount of items int the buffer
     */
    public int getSize() {
        return count;
    }

    /**
     * Adds a SystemEvent to the end of the ring buffer.
     *
     * @param item a request sent to the buffer
     * @param origin the origin from which the request came
     */
    public synchronized void addLast(SystemEvent item, Origin origin)
    {
        while (!writeable) {
            try { 
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        item.setOrigin(origin);
        buffer[inIndex] = item;
        readable = true;
        inIndex = (inIndex + 1) % SIZE;
        count++;
        if (count == SIZE)
            writeable = false;

        notifyAll();
    }

    /**
     * Removes the first SystemEvent from the ring buffer
     *
     * @param origin the origin making the request to remove an object from the buffer
     */
    public synchronized SystemEvent removeFirst(Origin origin)
    {
        SystemEvent item;
        
        while (!readable || identicalOrigin(buffer[outIndex], origin)) {
            try { 
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // remove item from buffer
        item = buffer[outIndex];
        buffer[outIndex] = null;
        writeable = true;
        outIndex = (outIndex + 1) % SIZE;
        count--;
        if (count == 0)
            readable = false;

        notifyAll();

        return item;
    }

    /**
     * Determines whether the request's origin is the same as the provided origin.
     *
     * @param request the topmost SystemEvent in the buffer
     * @param origin the origin that is attempting to remove a SystemEvent
     * @return true if successful, false otherwise
     */
    public boolean identicalOrigin(SystemEvent request, Origin origin) {
        return origin == request.getOrigin();
    }

    /**
     * Prints the contents of a Buffer.
     */
    public void printBufferContents() {
        // expand upon this
        System.out.println("Buffer contents: buffer0: " + buffer[0] + "\n"
                + "buffer1: "+ buffer[1] + "\n");
    }

    /**
     * Determines if Buffer is empty
     */
    public boolean isEmpty() {
        return count == 0;
    }
}
