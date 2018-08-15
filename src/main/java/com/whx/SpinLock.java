package com.whx;


import sun.misc.Unsafe;

import java.lang.reflect.Field;

/*
*  基于CAS实现的可重入排它锁
 */
public class SpinLock {
    private volatile int permit=0;
    private volatile Thread owner;
    private static Unsafe UNSAFE;
    private static long permitOffset=0;

     static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE= (Unsafe) f.get(null);
            permitOffset=UNSAFE.objectFieldOffset( SpinLock.class.getDeclaredField("permit"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }
    private boolean compareAndSetPermit(int expect,int updated){
        return UNSAFE.compareAndSwapInt(this,permitOffset,expect,updated);
    }
    private void setPermit(int updated){
         this.permit=updated;
    }

    public void lock() throws Exception {
        for(;;){
            int t = permit;
            if(t==0 && compareAndSetPermit(0,1)){
                owner=Thread.currentThread();
                break;
            }
            else if(Thread.currentThread()==owner){
                if(t+1<0){
                    throw new Exception("嵌套层数过多");
                }
                setPermit(t+1);
                break;
            }
        }
    }

    /*
    *   unclock是单线程运行，不需要竞争
     */
    public void unlock() throws Exception {
        if(owner!=Thread.currentThread()){
            throw  new Exception("illegal status");
        }
        int t = permit-1;
        if(t<0){
            throw new Exception("解锁次数超过了加锁次数");
        }
        if(t==0){
            owner=null;
        }
        setPermit(t);
    }
}
