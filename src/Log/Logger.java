package Log; /**
 * Created by olanre on 2018-10-16.
 */
import java.io.File;
import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Logger {
    private static boolean initialized;
    private LogLevel logLevel;
    private String ClassName, fileName;
    private File file;
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public enum LogLevel {
        INFO, DEBUG, WARN, ERROR,
    }

    public Logger(String logFile, LogLevel logLevel, String ClassName ){
        this.logLevel = logLevel;
        this.fileName = logFile;
        this.ClassName = ClassName;
        init();
    }

    public Logger( String logFile, String ClassName){

        this.fileName = logFile;
        this.ClassName = ClassName;
        this.logLevel = LogLevel.INFO;

        init();

    }

    public void init (){
        //file name only
        file = new File(this.fileName);
        this.file = file;

        try{
            file.createNewFile();

        } catch (IOException e) {
            System.out.println("Unable to create to file " + this.file + " Due to exception: " + e.getMessage());
        }
    }

    public LogLevel getLogLevel(){
        return logLevel;
    }

    public void setLogLevel(LogLevel newLogLevel){
        this.logLevel = newLogLevel;
    }

    private String CastLogLine(String Caller, String message){
        Date date = new Date();
        String Log = String.format("[%s] [%s] [%s] [%s] %s ", sdf.format(date), this.ClassName, this.logLevel, Caller, message);
        if(this.getLogLevel() ==  LogLevel.DEBUG) System.out.println(Log);

        return Log;
    }

    public void Log(String Caller, String message){
        String LogLine = CastLogLine(Caller, message);
        try(FileWriter fw = new FileWriter( this.file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(LogLine);

        } catch (IOException e) {
            System.out.println("Unable to log to file " + this.file + " Due to exception: " + e.getMessage());
        }
    }


}
