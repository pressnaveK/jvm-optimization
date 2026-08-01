package dev.pressnave.jvm;
import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;
import java.nio.ByteBuffer;
import java.util.*;
@Service
public class DirectMemoryIssue {
 private final List<ByteBuffer> retained = new ArrayList<>();
 DirectMemoryIssue(MeterRegistry registry) { Gauge.builder("lab.direct.retained.buffers", retained, List::size).register(registry); }
 public int reproduce(int mb) { for (int i=0;i<mb;i++) retained.add(ByteBuffer.allocateDirect(1024*1024)); return retained.size(); }
 public void clear() { retained.clear(); }
}