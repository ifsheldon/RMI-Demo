package myrmi;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Test
{
    public Future<Integer> calc(Integer i)
    {
        return Executors.newSingleThreadExecutor().submit(() -> i * i);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException
    {
        long timeInMilli = System.currentTimeMillis();
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return 100;
        });
        completableFuture.whenComplete((i, e) -> System.out.println(i+200));
        System.out.println(System.currentTimeMillis()-timeInMilli);
        System.out.println(completableFuture.get());

    }
}
