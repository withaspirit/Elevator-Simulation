/**
 * 
 */
package misc;

/**
 * @author Julian From course notes
 *
 */
public class BoundedBuffer {
    // A simple ring buffer is used to hold the data

    // buffer capacity
    public static final int SIZE = 5;
    private Object[] buffer = new Object[SIZE];
    private int inIndex = 0, outIndex = 0, count = 0;

    // If true, there is room for at least one object in the buffer.
    private boolean writeable = true;

    // If true, there is at least one object stored in the buffer.    
    private boolean readable = false;

    public synchronized void addLast(Object item)
    {
        while (!writeable) {
            try { 
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        
        buffer[inIndex] = item;
        readable = true;

        inIndex = (inIndex + 1) % SIZE;
        count++;
        if (count == SIZE)
            writeable = false;

        notifyAll();
    }

    public synchronized Object removeFirst()
    {
        Object item;
        
        while (!readable) {
            try { 
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }

        item = buffer[outIndex];
        writeable = true;

        outIndex = (outIndex + 1) % SIZE;
        count--;
        if (count == 0)
            readable = false;

        notifyAll();

        return item;
    }
}
