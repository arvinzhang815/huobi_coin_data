package pub.terminal.coin.tradeinfo;

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


    @Autowired
    private WebSocketClient webSocketClient;

    public static void main(String[] args) {
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
