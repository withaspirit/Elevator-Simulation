package misc;

/**
 * BoundedBuffer for managing Thread-Safe messaging between system components
 * 
 * @author Lynn Marshall, Julian
 */
public class BoundedBuffer {
    // A simple ring buffer is used to hold the data

    // buffer capacity
    private static final int SIZE = 5;
    private final ServiceRequest[] buffer = new ServiceRequest[SIZE];
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
     * Adds the item to the end of the ring buffer
     * 
     */
    public synchronized void addLast(ServiceRequest item, Origin origin)
    {
        while (!writeable) {
            try { 
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
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
     * Removes the first item from the ring buffer
     * 
     */
    public synchronized ServiceRequest removeFirst(Origin origin)
    {
        ServiceRequest item;
        
        while (!readable || identicalOrigin(buffer[outIndex], origin)) {
            try { 
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
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


    public boolean identicalOrigin(ServiceRequest request, Origin origin) {
        return origin == ((ServiceRequest) request).getOrigin();
    }

    // method for verifying whether buffer is empty?
    // method for returning buffer contents?

    /**
     * Method to print the contents of a Buffer.
     */
    public void printBufferContents() {
        // expand upon this
        System.out.println("Buffer contents: buffer0: " + buffer[0] + "\n"
            + "buffer1: "+ buffer[1] + "\n");
    }

    /**
     * Method to determine if Buffer is empty
     */
    public boolean isEmpty()
    {
        if(count==0)
        {
            return true;
        }
        return false;
    }

}
