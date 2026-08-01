package dev.pressnave.jvm;
import org.springframework.stereotype.Service;
@Service
public class DeadlockIssue {
 private final Object a=new Object(),b=new Object();
 public void reproduce(){
  runOrdered("ordered-A");
  runOrdered("ordered-B");
 }
 private void runOrdered(String name){Thread.ofPlatform().daemon().name(name).start(()->{synchronized(a){synchronized(b){/* consistent order */}}});}
}