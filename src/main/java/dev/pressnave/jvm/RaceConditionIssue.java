package dev.pressnave.jvm;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
@Service
public class RaceConditionIssue {
 private final AtomicInteger counter=new AtomicInteger();
 public Map<String,Integer> reproduce(int tasks,int increments)throws InterruptedException{
  counter.set(0);
  try(ExecutorService executor=Executors.newFixedThreadPool(Math.min(tasks,32))){
   CountDownLatch start=new CountDownLatch(1),done=new CountDownLatch(tasks);
   for(int t=0;t<tasks;t++)executor.submit(()->{await(start);for(int i=0;i<increments;i++)counter.incrementAndGet();done.countDown();});
   start.countDown();done.await();
  }
  return Map.of("expected",tasks*increments,"actual",counter.get());
 }
 private static void await(CountDownLatch l){try{l.await();}catch(InterruptedException e){Thread.currentThread().interrupt();}}
}