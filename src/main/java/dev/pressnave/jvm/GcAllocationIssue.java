package dev.pressnave.jvm;
import org.springframework.stereotype.Service;
@Service
public class GcAllocationIssue {
 public long reproduce(int rounds, int kb) {
  long result=0;
  for (int i=0;i<rounds;i++) {
   byte[] first=new byte[kb*1024];
   byte[] unnecessaryCopy=java.util.Arrays.copyOf(first, first.length);
   result += unnecessaryCopy[0];
  }
  return result;
 }
}