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
    private static boolean suppresRedundantErrors;
    private LogLevel logLevel;
    private String ClassName;
    private File file;
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    public enum LogLevel {
        INFO, DEBUG, WARN, ERROR,
    }

    public Logger(File logFile, LogLevel logLevel, String ClassName ){
        this.file = logFile;
        this.logLevel = logLevel;
        this.ClassName = ClassName;
    }

    public Logger( File logFile, String ClassName){
        this.file = logFile;
        this.ClassName = ClassName;
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
