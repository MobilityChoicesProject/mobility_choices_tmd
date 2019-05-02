package at.fhv.tmddemoservice.queue;

import javax.servlet.http.HttpServletRequest;

public abstract class ClassifyThread implements Runnable {

    private int _queuesize;
    private String _name;

    protected String _ipAddress;

    public ClassifyThread(String name, String ipAddress, int queuesize) {
        _name = name;
        _ipAddress = ipAddress;
        _queuesize = queuesize;
    }


    @Override
    public void run() {
        System.out.println("Queue size: " + _queuesize);
        System.out.println(Thread.currentThread().getName() + " Start process: " + _name);
        long start = System.currentTimeMillis();
        process();
        long end = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + " Finished process (" + _name + ") after " + (end - start) + "ms.");
    }

    public abstract void process();
}
