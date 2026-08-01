package dev.pressnave.jvm;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @Profile("lab") @RequestMapping("/lab")
public class LabController {
 private final HeapMemoryIssue heap; private final DirectMemoryIssue direct; private final GcAllocationIssue gc;
 private final RaceConditionIssue race; private final DeadlockIssue deadlock; private final ThreadStarvationIssue starvation; private final VirtualThreadPinningIssue virtual;
 LabController(HeapMemoryIssue heap,DirectMemoryIssue direct,GcAllocationIssue gc,RaceConditionIssue race,DeadlockIssue deadlock,ThreadStarvationIssue starvation,VirtualThreadPinningIssue virtual){
  this.heap=heap;this.direct=direct;this.gc=gc;this.race=race;this.deadlock=deadlock;this.starvation=starvation;this.virtual=virtual;
 }
 @PostMapping("/memory/heap") Object heap(@RequestParam(defaultValue="25")int mb){return Map.of("retained",heap.reproduce(limit(mb,1,128)));}
 @PostMapping("/memory/direct") Object direct(@RequestParam(defaultValue="25")int mb){return Map.of("retained",direct.reproduce(limit(mb,1,128)));}
 @DeleteMapping("/memory") void clear(){heap.clear();direct.clear();}
 @PostMapping("/gc/allocation") Object gc(@RequestParam(defaultValue="20000")int rounds,@RequestParam(defaultValue="64")int kb){return Map.of("result",gc.reproduce(limit(rounds,1,1000000),limit(kb,1,1024)));}
 @PostMapping("/race") Object race(@RequestParam(defaultValue="32")int tasks,@RequestParam(defaultValue="200000")int increments)throws Exception{return race.reproduce(limit(tasks,2,64),limit(increments,1,1000000));}
 @PostMapping("/deadlock") Object deadlock(){deadlock.reproduce();return Map.of("status","created");}
 @PostMapping("/starvation") Object starvation(@RequestParam(defaultValue="40")int tasks,@RequestParam(defaultValue="30")int seconds){return Map.of("submitted",starvation.reproduce(limit(tasks,1,100),limit(seconds,1,120)));}
 @PostMapping("/virtual-thread-pinning") Object virtual(@RequestParam(defaultValue="500")int tasks,@RequestParam(defaultValue="200")int millis){return Map.of("submitted",virtual.reproduce(limit(tasks,1,10000),limit(millis,1,2000)));}
 private static int limit(int v,int min,int max){if(v<min||v>max)throw new IllegalArgumentException("value outside "+min+".."+max);return v;}
}