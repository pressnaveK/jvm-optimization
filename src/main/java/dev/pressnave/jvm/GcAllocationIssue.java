package dev.pressnave.jvm;
import org.springframework.stereotype.Service;
@Service
public class GcAllocationIssue {
 public long reproduce(int rounds,int kb){
  byte[] reusable=new byte[kb*1024];
  long result=0;
  for(int i=0;i<rounds;i++){reusable[0]=(byte)i;result+=reusable[0];}
  return result;
 }
}