package dev.pressnave.jvm;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
@Service
public class VirtualThreadPinningIssue {
 private final ReentrantLock lock=new ReentrantLock();
 private final ExecutorService executor=Executors.newVirtualThreadPerTaskExecutor();
 public int reproduce(int tasks,int millis){
  for(int i=0;i<tasks;i++)executor.submit(()->{lock.lock();try{Thread.sleep(millis);}catch(InterruptedException e){Thread.currentThread().interrupt();}finally{lock.unlock();}});
  return tasks;
 }
 @PreDestroy void close(){executor.shutdownNow();}
}