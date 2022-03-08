package systemwide;

import requests.SystemEvent;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * BoundedBuffer maintains a Thread-Safe queue of SystemEvents.
 * 
 * @author Julian, Ryan Dash, Liam Tripp, Lynn Marshall
 */
public class BoundedBuffer {

    private final ConcurrentLinkedDeque<SystemEvent> itemQueue;
    private int count = 0;

    /**
     * Constructor for BoundedBuffer.
     */
    public BoundedBuffer() {
        itemQueue = new ConcurrentLinkedDeque<>();
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
    public synchronized void addLast(SystemEvent item, Origin origin) {
        itemQueue.addLast(item);
        item.setOrigin(origin);
        count++;
        notifyAll();
    }

    /**
     * Removes the first SystemEvent from the ring buffer
     *
     * @param origin the origin making the request to remove an object from the buffer
     */
    public synchronized SystemEvent removeFirst(Origin origin) {
        SystemEvent item = itemQueue.removeFirst();
        count--;
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
        if (itemQueue.isEmpty()) {
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
        return origin == itemQueue.peek().getOrigin();
    }

    /**
     * Prints the contents of the Buffer.
     */
    public synchronized void printBufferContents() {
        itemQueue.forEach(systemEvent -> {
            System.out.println(systemEvent.getClass().toString());
        });
    }

    /**
     * Determines if Buffer is empty
     */
    public synchronized boolean isEmpty() {
        return itemQueue.isEmpty();
    }
}
