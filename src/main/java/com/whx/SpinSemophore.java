package com.whx;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 基于自旋锁的信号量实现
 */
public class SpinSemophore {
    private volatile int permit=0;
    private static Unsafe UNSAFE;
    private static long permitOffset=0;
    private int count=0;

    public SpinSemophore(){
        this(1);
    }

    public SpinSemophore(int count){
        this.count=count;
        this.permit=count;
    }

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE= (Unsafe) f.get(null);
            permitOffset=UNSAFE.objectFieldOffset( SpinSemophore.class.getDeclaredField("permit"));
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

    public void acquire(){
        for(;;){
            int t = permit;
            if(t!=0 && compareAndSetPermit(t,t-1)){
                break;
            }
        }
    }

    public void release() throws Exception {
        for(;;){
            int t = permit;
            if(t==count){
                throw new Exception("调用release函数的次数大于了调用acquire函数的次数");
            }
            if(compareAndSetPermit(t,t+1)){
                break;
            }
        }
    }
}
