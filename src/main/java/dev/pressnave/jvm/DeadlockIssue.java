package dev.pressnave.jvm;
import org.springframework.stereotype.Service;
@Service
public class DeadlockIssue {
 private final Object a=new Object(), b=new Object();
 public void reproduce() {
  Thread.ofPlatform().daemon().name("deadlock-A").start(()->{synchronized(a){sleep();synchronized(b){}}});
  Thread.ofPlatform().daemon().name("deadlock-B").start(()->{synchronized(b){sleep();synchronized(a){}}});
 }
 private static void sleep(){try{Thread.sleep(200);}catch(InterruptedException e){Thread.currentThread().interrupt();}}
}