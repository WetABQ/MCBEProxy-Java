package jp.dip.hmy2001.mcpeProxy.utils;

import jp.dip.hmy2001.mcpeProxy.ProxyMain;
import jline.console.*;
import java.io.IOException;

public class CommandReader extends Thread{
    private static CommandReader instance = null;
    private ConsoleReader reader;
    private CursorBuffer stashed;
    private boolean isRunning = false;

    public static CommandReader getInstance() {
        return instance;
    }

    public CommandReader() {
        try {
            this.reader = new ConsoleReader();
            reader.setPrompt("> ");
            instance = this;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConsoleReader getReader(){
        return reader;
    }

    public void run() {
        isRunning = true;
        String line;

        try {
            while (isRunning){
                if ((line = reader.readLine()) != null) {
                    ProxyMain.getInstance().onCommand(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stashLine() {
        this.stashed = reader.getCursorBuffer().copy();
        try {
            reader.getOutput().write("\u001b[1G\u001b[K");
            reader.flush();
        } catch (IOException e) {
            // ignore
        }
    }

    public synchronized void unstashLine() {
        try {
            reader.resetPromptLine("> ", this.stashed.toString(), this.stashed.cursor);
        } catch (IOException e) {
            // ignore
        }
    }

    public void shutdown(){
        isRunning = false;

        try {
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void removePromptLine() {
        try {
            reader.resetPromptLine("", "", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
