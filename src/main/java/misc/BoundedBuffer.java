/**
 * 
 */
package misc;

/**
 * BoundedBuffer for managing Thread-Safe messaging between system components
 * 
 * @author Julian From course notes, Ryan Dash
 */
public class BoundedBuffer {
    // A simple ring buffer is used to hold the data

    // buffer capacity
    public static final int SIZE = 10;
    private final ServiceRequest[] buffer = new ServiceRequest[SIZE];
    private int inIndex = 0, outIndex = 0, count = 0;

    // If true, there is room for at least one object in the buffer.
    private boolean writeable = true;

    // If true, there is at least one object stored in the buffer.    
    private boolean readable = false;

    /**
     * Adds the item to the end of the ring buffer
     * 
     */
    public synchronized void addLast(ServiceRequest item, Thread requestThread)
    {
        while (!writeable) {
            try { 
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        item.setOrigin(requestThread);
        buffer[inIndex] = item;
        readable = true;
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inIndex = (inIndex + 1) % SIZE;
        count++;
        if (count == SIZE)
            writeable = false;

        notifyAll();
    }

    /**
     * Removes the first item from the ring buffer
     * 
     */
    public synchronized ServiceRequest removeFirst()
    {
        ServiceRequest item;
        
        while (!readable) {
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
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        outIndex = (outIndex + 1) % SIZE;
        count--;
        if (count == 0)
            readable = false;

        notifyAll();

        return item;
    }

    public synchronized ServiceRequest checkFirst(){
        while (!readable) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return buffer[outIndex];
    }

    public boolean isEmpty() {
        return count == 0;
    }
}
