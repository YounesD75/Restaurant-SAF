package com.InventoryService.InventoryService.agent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractAgent implements Runnable {

    protected final String name;
    private final BlockingQueue<Message> mailbox;
    private boolean running = true;

    public AbstractAgent(String name) {
        this.name = name;
        this.mailbox = new LinkedBlockingQueue<>();
        startAgentThread();
    }

    private void startAgentThread() {
        Thread t = new Thread(this);
        t.setName("Agent-" + name);
        t.start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Message msg = mailbox.take();
                onMessage(msg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("⚠️ Agent " + name + " interrupted.");
            }
        }
    }

    public void send(Message msg) {
        mailbox.offer(msg);
    }

    protected abstract void onMessage(Message msg);

    public abstract String getName();

    public void stop() {
        this.running = false;
    }
}

