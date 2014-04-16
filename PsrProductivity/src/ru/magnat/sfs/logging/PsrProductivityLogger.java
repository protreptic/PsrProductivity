package ru.magnat.sfs.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.magnat.sfs.MainActivity;
import ru.magnat.sfs.util.Device;
import android.os.Environment;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
 
public class PsrProductivityLogger {
	private static ch.qos.logback.classic.Logger sInstance = null;
	private static final String LOG_DIR = 
			Environment.getExternalStorageDirectory().getAbsolutePath() 
			+ "/Download/ru.magnat.sfs/logs";
	private PsrProductivityLogger() {}
	public static ch.qos.logback.classic.Logger getLogger() {
		if (sInstance == null) {
			LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		    context.reset();

		    RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
		    rollingFileAppender.setAppend(true);
		    rollingFileAppender.setContext(context);

		    SizeAndTimeBasedFNATP<ILoggingEvent> timeBasedFNATP = new SizeAndTimeBasedFNATP<ILoggingEvent>();	
		    timeBasedFNATP.setMaxFileSize("5MB");
		    timeBasedFNATP.setContext(context);
		    
		    TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
		    rollingPolicy.setFileNamePattern(LOG_DIR + "/" + Device.getDeviceId(MainActivity.getInstance()) + "_%d{yyyy-MM-dd}.%i.txt.zip");
		    rollingPolicy.setMaxHistory(7); 
		    rollingPolicy.setParent(rollingFileAppender); 
		    rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(timeBasedFNATP);
		    rollingPolicy.setContext(context);
		    rollingPolicy.start();

		    rollingFileAppender.setRollingPolicy(rollingPolicy);

		    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		    encoder.setPattern("%-5level %d{HH:mm:ss} : %msg%n");
		    encoder.setContext(context);
		    encoder.start();

		    rollingFileAppender.setEncoder(encoder);
		    rollingFileAppender.start();
			
		    sInstance = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		    
		    sInstance.setLevel(Level.TRACE);
		    sInstance.addAppender(rollingFileAppender);

		    //StatusPrinter.print(context);

		    return sInstance;
		}
		return sInstance;
	}

	public static void i(String message) {
		getLogger().info(message);
	}
	public static void i(String message, Exception exception) {
		getLogger().info(message, exception);
	}
	public static void e(String message) {
		getLogger().error(message);
	}
	public static void e(String message, Exception exception) {
		getLogger().error(message, exception);
	}
	public static void d(String message) {
		getLogger().debug(message);
	}
	public static void d(String message, Exception exception) {
		getLogger().debug(message, exception);
	}
	
	public static class Log extends PsrProductivityLogger {}
}
