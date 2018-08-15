package com.whx;

public class Test {

    public void testSpinLock(){
        SpinLock lock = new SpinLock();
        for(int i=0;i<2;i++){
            Thread t = new Thread(()->{
                try{
                    lock.lock();
                    lock.lock();
                    System.out.println("haha");
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    try {
                        lock.unlock();


                        lock.unlock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("我出来了");
            });
            t.start();
        }
    }

    public void testSpinSemphore(){
        SpinSemophore ssm = new SpinSemophore(3);
        for(int i=0;i<10;i++){
            Thread t = new Thread(()->{
                try{
                    ssm.acquire();
                    System.out.println("haha");
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    try {
                     ssm.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }

    public static void main(String [] args) {
        new Test().testSpinSemphore();
   }
}
