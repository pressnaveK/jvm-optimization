package dev.pressnave.jvm;
import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class HeapMemoryIssue {
 private static final int MAX_ENTRIES=100;
 private final Map<Integer,byte[]> bounded=new ConcurrentHashMap<>();
 HeapMemoryIssue(MeterRegistry registry){Gauge.builder("lab.heap.bounded.objects",bounded,Map::size).register(registry);}
 public int reproduce(int mb){
  for(int i=0;i<mb;i++){
   if(bounded.size()>=MAX_ENTRIES) bounded.remove(bounded.keySet().iterator().next());
   bounded.put(i,new byte[1024*1024]);
  }
  return bounded.size();
 }
 public void clear(){bounded.clear();}
}