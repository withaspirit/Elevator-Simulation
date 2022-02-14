package requests;

import java.time.LocalTime;

public class Requests {

    LocalTime time;
    Thread origin;

    public Requests(LocalTime time, Thread origin){
        this.time = time;
        this.origin = origin;
    }

    /**
     * Returns the time the request was made.
     *
     * @return LocalTime the time the request was made
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Returns Origin, a Thread representing the Runnable system from which the request came from.
     *
     * @return origin, the Runnable system representing the request's origin
     */
    public Thread getOrigin() {
        return origin;
    }

    /**
     * Changes the request's origin.
     *
     * @param origin an enum representing the Runnable system from which the request came from
     */
    public void setOrigin(Thread origin) {
        this.origin = origin;
    }
}
