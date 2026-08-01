package dev.pressnave.jvm;
import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
@Service
public class DirectMemoryIssue {
 private static final int MAX_BUFFERS=64;
 private final ArrayBlockingQueue<ByteBuffer> pool=new ArrayBlockingQueue<>(MAX_BUFFERS);
 DirectMemoryIssue(MeterRegistry registry){Gauge.builder("lab.direct.pooled.buffers",pool,java.util.Collection::size).register(registry);}
 public int reproduce(int mb){
  int count=Math.min(mb,MAX_BUFFERS);
  for(int i=0;i<count;i++) if(!pool.offer(ByteBuffer.allocateDirect(1024*1024))) break;
  return pool.size();
 }
 public void clear(){pool.clear();}
}