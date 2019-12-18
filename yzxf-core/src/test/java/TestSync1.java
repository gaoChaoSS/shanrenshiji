import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hujoey on 16/7/28.
 */
class RxJava {
    public static void main(String[] args) throws InterruptedException {
        String[] names = new String[]{"joey", "ll", "luo", "ll", "luo", "ll", "luo", "ll", "luo", "ll", "luo", "ll", "luo", "ll", "luo", "ll", "luo", "ll", "luo", "ll", "luo", "ll", "luo"};
        long l = System.currentTimeMillis();
        Action1<String> onNext = new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }

        };
        Action1<Throwable> onError = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                System.out.println("eroor---");
            }
        };
        Observable.from(names)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String name) {
                        System.out.println(name);
                        name += "1";
                        return name;
                    }
                })
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String name) {
                        return name += "1";
                    }
                })
                .subscribe(onNext, onError).unsubscribe();
        System.out.println(System.currentTimeMillis() - l);
        Thread.sleep(225000);
    }
}
