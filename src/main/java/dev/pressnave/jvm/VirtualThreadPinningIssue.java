package dev.pressnave.jvm;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import java.util.concurrent.*;
@Service
public class VirtualThreadPinningIssue {
 private final Object monitor=new Object();
 private final ExecutorService executor=Executors.newVirtualThreadPerTaskExecutor();
 public int reproduce(int tasks,int millis) {
  for(int i=0;i<tasks;i++) executor.submit(()->{synchronized(monitor){try{Thread.sleep(millis);}catch(InterruptedException e){Thread.currentThread().interrupt();}}});
  return tasks;
 }
 @PreDestroy void close(){executor.shutdownNow();}
}