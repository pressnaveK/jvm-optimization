package dev.pressnave.jvm;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import java.util.concurrent.*;
@Service
public class ThreadStarvationIssue {
 private final ThreadPoolExecutor pool=new ThreadPoolExecutor(4,4,0L,TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(16),new ThreadPoolExecutor.AbortPolicy());
 public int reproduce(int tasks,int seconds){
  int accepted=0;
  for(int i=0;i<tasks;i++)try{
   pool.submit(()->{try{Thread.sleep(Math.min(seconds,5)*1000L);}catch(InterruptedException e){Thread.currentThread().interrupt();}});
   accepted++;
  }catch(RejectedExecutionException rejected){break;}
  return accepted;
 }
 @PreDestroy void close(){pool.shutdownNow();}
}