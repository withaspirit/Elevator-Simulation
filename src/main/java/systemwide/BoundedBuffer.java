package systemwide;

import requests.SystemEvent;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * BoundedBuffer for managing Thread-Safe messaging between system components
 * 
 * @author Lynn Marshall, Julian, Ryan Dash, Liam Tripp
 */
public class BoundedBuffer {

    private final ConcurrentLinkedDeque<SystemEvent> bufferList;

    // A simple ring buffer is used to hold the data

    // buffer capacity
    private static final int SIZE = 10;
    private final SystemEvent[] buffer = new SystemEvent[SIZE];
    private int inIndex = 0, outIndex = 0, count = 0;

    // If true, there is room for at least one object in the buffer.
    private boolean writeable = true;

    // If true, there is at least one object stored in the buffer.    
    private boolean readable = false;

    /**
     * Constructor for BoundedBuffer.
     */
    public BoundedBuffer() {
        bufferList = new ConcurrentLinkedDeque<>();
    }

    /**
     * Returns the amount of items in the buffer.
     *
     * @return number the amount of items in the buffer
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
        bufferList.addLast(item);
        item.setOrigin(origin);
        count++;
        /*
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

         */
        notifyAll();
    }

    /**
     * Removes the first SystemEvent from the ring buffer
     *
     * @param origin the origin making the request to remove an object from the buffer
     */
    public synchronized SystemEvent removeFirst(Origin origin)
    {
        SystemEvent item = bufferList.removeFirst();
        count--;
        /*
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
         */

        notifyAll();

        return item;
    }

    /**
     * Determines  whether a SubsystemMessagePasser can remove the item
     * from the top of the BoundedBuffer.
     *
     * @param origin the identity of the SubsystemMessagePasser
     * @return true if the buffer isn't empty and the request to remove's origin is not the given origin, false otherwise
     */
    public synchronized boolean canRemoveFromBuffer(Origin origin) {
        if (bufferList.isEmpty()) {
            return false;
        }
        return !identicalOrigin(origin);
    }

    /**
     * Determines whether the request's origin is the same as the provided origin.
     *
     * @param origin the origin that is attempting to remove a SystemEvent
     * @return true if successful, false otherwise
     */
    public synchronized boolean identicalOrigin(Origin origin) {
        return origin == bufferList.peek().getOrigin();
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
    public synchronized boolean isEmpty() {
        return bufferList.isEmpty();
    }

    /**
     * Determines whether the Buffer is writable.
     *
     * @return true if the buffer is at maximum capacity, false otherwise
     */
    public synchronized boolean isWritable() {
        return writeable;
    }
}
