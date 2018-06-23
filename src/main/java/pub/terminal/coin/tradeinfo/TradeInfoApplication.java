package pub.terminal.coin.tradeinfo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class})
@EnableAsync
@EnableScheduling
public class TradeInfoApplication implements CommandLineRunner {

    public static boolean isDebugMode = false;
    public static String webSocketHost = "";
    @Autowired
    private WebSocketClient webSocketClient;

    public static void main(String[] args) {
        Options options = new Options();
        Option debug = new Option("d", "debug", true, "websockt debug mode trigger");
        debug.setRequired(true);
        options.addOption(debug);

        Option host = new Option("h", "host", true, "websocket host");
        host.setRequired(true);
        options.addOption(host);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        isDebugMode = Boolean.valueOf(cmd.getOptionValue("debug"));
        WebSocketImpl.DEBUG = isDebugMode;
        webSocketHost = cmd.getOptionValue("host");
        SpringApplication.run(TradeInfoApplication.class, args);
    }

    @Bean
    public TaskExecutor getTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(100);
        taskExecutor.setMaxPoolSize(2000);
        taskExecutor.setThreadNamePrefix("worker-");
        return taskExecutor;
    }

    @Bean
    public TaskScheduler getTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Override
    public void run(String... args) throws Exception {
        webSocketClient.connect();
    }
}
