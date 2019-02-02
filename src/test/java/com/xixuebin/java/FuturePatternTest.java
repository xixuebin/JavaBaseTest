package com.xixuebin.java;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.testng.annotations.Test;

/**
 * 参考文档 https://www.cnblogs.com/xiangnanl/p/9939447.html
 */
@Test
public class FuturePatternTest {

  @org.testng.annotations.BeforeMethod
  public void setUp() {
  }

  @org.testng.annotations.AfterMethod
  public void tearDown() {
  }


  @Test
  public void testOne() throws ExecutionException, InterruptedException {
    ExecutorService executor = Executors.newCachedThreadPool();
    Future<Integer> result = executor.submit(new Callable<Integer>() {
      public Integer call() throws Exception {
        return new Random().nextInt();
      }
    });
    executor.shutdown();

    System.out.println(result.get());
  }

  @Test
  public void testThread() throws ExecutionException, InterruptedException {
    FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {
      public Integer call() throws Exception {
        return new Random().nextInt();
      }
    });
    new Thread(task).start();

    System.out.println("result: " + task.get());
  }

  @Test
  public void test3() {
    String result = CompletableFuture.supplyAsync(() -> {
      return "Hello ";
    }).thenApplyAsync(v -> v + "world").join();
    System.out.println(result);

    CompletableFuture.supplyAsync(() -> {
      return "Hello ";
    }).thenAccept(v -> {
      System.out.println("consumer: " + v);
    });

    String result2 = CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return "Hello";
    }).thenCombine(CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return "world";
    }), (s1, s2) -> {
      return s1 + " " + s2;
    }).join();
    System.out.println(result2);

  }

  @Test
  public void testCompletableFuture2() {
    //谁计算快就用谁的
    String result = CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return "Hi Boy";
    }).applyToEither(CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return "Hi Girl";
    }), (s) -> {
      return s;
    }).join();
    System.out.println(result);
  }
  
  //运行时出现了异常，可以通过exceptionally进行补偿

  @Test
  public void testCompletableFuture3() {
    String result = CompletableFuture.supplyAsync(()->{
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if(true) {
        throw new RuntimeException("exception test!");
      }

      return "Hi Boy";
    }).exceptionally(e->{
      System.out.println(e.getMessage());
      return "Hello world!";
    }).join();
    System.out.println(result);
  }
}