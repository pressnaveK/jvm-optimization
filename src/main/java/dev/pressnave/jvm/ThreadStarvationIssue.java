package dev.pressnave.jvm;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import java.util.concurrent.*;
@Service
public class ThreadStarvationIssue {
 private final ExecutorService pool=Executors.newFixedThreadPool(4);
 public int reproduce(int tasks,int seconds) {
  for(int i=0;i<tasks;i++) pool.submit(()->{try{Thread.sleep(seconds*1000L);}catch(InterruptedException e){Thread.currentThread().interrupt();}});
  return tasks;
 }
 @PreDestroy void close(){pool.shutdownNow();}
}