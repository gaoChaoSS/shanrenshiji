import sun.net.www.http.HttpClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by hujoey on 16/7/28.
 */
public class TestSync {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                future.complete("joey");
                //future.completeExceptionally(new RuntimeException("失败了"));
            }
        }.start();
        Consumer<? super String> x = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("end:" + s);
            }
        };
        BiConsumer<? super String, ? super Throwable> wc = new BiConsumer<String, Throwable>() {
            @Override
            public void accept(String s, Throwable throwable) {
                if (throwable != null) {
                    System.out.println("有错误哦:");
                    throwable.printStackTrace();
                }

            }

        };
        //future.whenComplete(wc);
        //System.out.println("任务交给对方了");

        System.out.println(future.get(3, TimeUnit.SECONDS));

    }

    public void handle() {
        CompletableFuture futureCount = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        Thread.sleep(3000);
                        System.out.println("joey");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return 10;
                });
        CompletableFuture f2 = futureCount.thenApply(
                (i) -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int j = 10 + (int) i;
                    return j;
                });
        try {
            int count = (int) f2.get();
            System.out.println(count);
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
            // Exceptions that occur in future will be thrown here.
        }

    }
}
