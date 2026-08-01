package dev.pressnave.jvm;
import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class HeapMemoryIssue {
 private final List<byte[]> retained = new ArrayList<>();
 HeapMemoryIssue(MeterRegistry registry) { Gauge.builder("lab.heap.retained.objects", retained, List::size).register(registry); }
 public int reproduce(int mb) { for (int i=0;i<mb;i++) retained.add(new byte[1024*1024]); return retained.size(); }
 public void clear() { retained.clear(); }
}