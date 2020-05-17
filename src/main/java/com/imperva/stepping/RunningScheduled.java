package com.imperva.stepping;

import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.Date;
import java.util.concurrent.*;

 public class RunningScheduled extends IRunning {
     private String id;
     private long delay;
     private long initialdelay;
     private ScheduledFuture scheduledFuture;
     private ScheduledExecutorService scheduledExecutorService;
     private TimeUnit timeUnit;

     protected RunningScheduled(String id, long delay, long initialdelay, TimeUnit timeUnit, Runnable runnable) {
         this.id = id;
         this.delay = delay;
         this.initialdelay = initialdelay;
         this.runnable = runnable;
         this.timeUnit = timeUnit;
         this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
             @Override
             public Thread newThread(Runnable r) {
                 Thread t = new Thread(r);
                 t.setName(id);
                 return t;
             }
         });
     }

     protected RunningScheduled(String id, String cronExpression, Runnable runnable) {
         long delay = calculateDelay(cronExpression);
         this.id = id;
         this.delay = delay;
         this.initialdelay = delay;
         this.runnable = runnable;
         this.timeUnit = TimeUnit.MILLISECONDS;
         this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
             @Override
             public Thread newThread(Runnable r) {
                 Thread t = new Thread(r);
                 t.setName(id);
                 return t;
             }
         });
     }

     protected RunningScheduled(String id,  Runnable runnable) {
         this.runnable = runnable;
         this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
             @Override
             public Thread newThread(Runnable r) {
                 Thread t = new Thread(r);
                 t.setName(id);
                 return t;
             }
         });
     }

     protected Future<?> awake() {
         this.scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(runnable, initialdelay, delay, timeUnit);
         return scheduledFuture;
     }

     ScheduledExecutorService getScheduledExecutorService() {
         return scheduledExecutorService;
     }

     public void setDelay(long delay, long initialdelay, TimeUnit timeUnit) {
         this.delay = delay;
         this.initialdelay = initialdelay;
         this.timeUnit = timeUnit;
     }

     public void changeDelay(long delay, long initialdelay, TimeUnit timeUnit) {
         scheduledFuture.cancel(false);
         scheduledExecutorService.scheduleWithFixedDelay(runnable, initialdelay, delay, timeUnit);
     }

     public void setDelay(long delay, TimeUnit timeUnit) {
         this.delay = delay;
         this.initialdelay = delay;
         this.timeUnit = timeUnit;
     }

     public void changeDelay(long delay, TimeUnit timeUnit) {
         scheduledFuture.cancel(false);
         scheduledExecutorService.scheduleWithFixedDelay(runnable, delay, delay, timeUnit);
     }

     public void setDelay(String cronExpression){
         this.delay = calculateDelay(cronExpression);
         this.initialdelay = this.delay;
         this.timeUnit = TimeUnit.MILLISECONDS;

     }

     public void setDelay(String cronExpression, long initialdelay){
         this.delay = calculateDelay(cronExpression);
         this.initialdelay = initialdelay;
         this.timeUnit = TimeUnit.MILLISECONDS;

     }

     public void setDelay(String cronExpression, long initialdelay, TimeUnit timeUnit){
         this.delay = calculateDelay(cronExpression);
         this.initialdelay = initialdelay;
         this.timeUnit = timeUnit;

     }

     public void changeDelay(String cronExpression){
         long delay = calculateDelay(cronExpression);
         scheduledFuture.cancel(false);
         scheduledExecutorService.scheduleWithFixedDelay(runnable, delay, delay, TimeUnit.MILLISECONDS);
     }


     private long calculateDelay(String cronExpression)
     {
         try {
             CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpression);
             Date now = new Date();
             Date next = cronSequenceGenerator.next(now);
             long delay = next.getTime() - now.getTime();
             return delay;
         }catch (Exception exe){
             throw new SteppingException(exe.toString());
         }

     }

     public void stop() {
         close(scheduledFuture, false);
     }

     @Override
     public void close() {
         close(scheduledFuture, true);
     }
 }